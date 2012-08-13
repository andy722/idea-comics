package com.abelsky.idea.geekandpoke.http.impl;

import com.abelsky.idea.geekandpoke.entries.Entry;
import com.abelsky.idea.geekandpoke.entries.EntryCache;
import com.abelsky.idea.geekandpoke.entries.EntryInfo;
import com.abelsky.idea.geekandpoke.http.ContentUpdater;
import com.abelsky.idea.geekandpoke.http.EntryHandler;
import com.abelsky.idea.geekandpoke.messages.MessageBundle;
import com.google.common.base.Predicate;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;

/**
 * @author andy
 */
public class ContentUpdaterImpl implements ContentUpdater {
    private final Logger log = Logger.getInstance(getClass());

    private int lastPageChecked = 0;

    private static interface BackgroundUpdateTask {
        void run(@NotNull ProgressIndicator indicator);
    }

    private void runUpdateTask(final BackgroundUpdateTask task) {
        final Task.Backgroundable updateTask = new Task.Backgroundable(null,
                MessageBundle.message("update.progress.title"), false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                task.run(indicator);
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateTask.queue();
            }
        });
    }

    @Override
    public void fetchLastPage(final EntryHandler handler) {
        runUpdateTask(new BackgroundUpdateTask() {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                final List<EntryInfo> onlineEntries = new PageScanner().update(0);

                fetchEntries(indicator, onlineEntries, handler);
            }
        });
    }

    @Override
    public void fetchMore(final EntryHandler handler) {
        runUpdateTask(new BackgroundUpdateTask() {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                doGetMore(indicator, handler);
            }
        });
    }

    private void fetchEntries(ProgressIndicator indicator, List<EntryInfo> onlineEntries, EntryHandler handler) {
        final EntryFetcher fetcher = new EntryFetcher();
        final EntryCache cache = ServiceManager.getService(EntryCache.class);

        final Collection<EntryInfo> newEntries = filter(onlineEntries, new Predicate<EntryInfo>() {
            @Override
            public boolean apply(EntryInfo entry) {
                return cache.getCached(entry) == null;
            }
        });

        final float toProcess = newEntries.size();

        indicator.setIndeterminate(false);
        indicator.setFraction(0);

        int i = 0;
        for (EntryInfo onlineEntry : newEntries) {
            try {
                final Entry local = fetcher.read(onlineEntry);
                cache.asyncCache(local);
                handler.handle(local);

            } catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e);
                }
            }

            indicator.setFraction(++i / toProcess);
        }
    }

    private void doGetMore(ProgressIndicator indicator, EntryHandler handler) {
        final EntryCache cache = ServiceManager.getService(EntryCache.class);

        List<EntryInfo> newEntries = new ArrayList<EntryInfo>();

        while (newEntries.isEmpty()) {
            newEntries.addAll(filter(new PageScanner().update(lastPageChecked++), new Predicate<EntryInfo>() {
                @Override
                public boolean apply(EntryInfo entry) {
                    return cache.getCached(entry) == null;
                }
            }));
        }

        fetchEntries(indicator, newEntries, handler);
    }

}
