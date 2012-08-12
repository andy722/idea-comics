package com.abelsky.idea.geekandpoke.http;

import com.abelsky.idea.geekandpoke.entries.OfflineCache;
import com.abelsky.idea.geekandpoke.entries.OfflineEntry;
import com.abelsky.idea.geekandpoke.entries.OnlineEntry;
import com.abelsky.idea.geekandpoke.messages.MessageBundle;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author andy
 */
public class ContentUpdater {
    private final Logger log = Logger.getInstance(getClass());

    private final NewEntryHandler handler;

    private int lastPageChecked = 0;

    public ContentUpdater(NewEntryHandler handler) {
        this.handler = handler;
    }

    public void fetchLastPage() {
        final Task.Backgroundable updateTask = new Task.Backgroundable(null,
                MessageBundle.message("update.progress.title"), false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                doUpdate(indicator);
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateTask.queue();
            }
        });
    }

    public void fetchMore() {
        final Task.Backgroundable updateTask = new Task.Backgroundable(null,
                MessageBundle.message("update.progress.title"), false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                doGetMore(indicator);
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateTask.queue();
            }
        });
    }

    private void doUpdate(ProgressIndicator indicator) {
        final PageScanner reader = ServiceManager.getService(PageScanner.class);
        final EntryFetcher fetcher = ServiceManager.getService(EntryFetcher.class);

        final List<OnlineEntry> onlineEntries = reader.update(0);

        fetchEntries(indicator, fetcher, onlineEntries);
    }

    private void fetchEntries(ProgressIndicator indicator, EntryFetcher fetcher, List<OnlineEntry> onlineEntries) {
        indicator.setIndeterminate(false);
        indicator.setFraction(0);

        int i = 0;
        for (OnlineEntry onlineEntry : onlineEntries) {
            if (ServiceManager.getService(OfflineCache.class).getCached(onlineEntry) == null) {
                try {
                    final OfflineEntry local = fetcher.read(onlineEntry);
                    ServiceManager.getService(OfflineCache.class).cache(local);
                    handler.handleNewEntry(local);

                } catch (IOException e) {
                    log.error(e);
                }
            }

            indicator.setFraction(++i / (double) onlineEntries.size());
        }
    }

    private void doGetMore(ProgressIndicator indicator) {
        final PageScanner reader = ServiceManager.getService(PageScanner.class);
        final EntryFetcher fetcher = ServiceManager.getService(EntryFetcher.class);

        List<OnlineEntry> newEntries = new ArrayList<OnlineEntry>();

        while (newEntries.isEmpty()) {
            final List<OnlineEntry> onlineEntries = reader.update(lastPageChecked++);

            for (OnlineEntry e : onlineEntries) {
                if (ServiceManager.getService(OfflineCache.class).getCached(e) == null) {
                    newEntries.add(e);
                }
            }
        }

        fetchEntries(indicator, fetcher, newEntries);
    }

    public static interface NewEntryHandler {
        public void handleNewEntry(OfflineEntry e);
    }
}
