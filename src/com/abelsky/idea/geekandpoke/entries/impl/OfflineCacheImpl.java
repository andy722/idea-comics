package com.abelsky.idea.geekandpoke.entries.impl;

import com.abelsky.idea.geekandpoke.ComicsPlugin;
import com.abelsky.idea.geekandpoke.entries.Entry;
import com.abelsky.idea.geekandpoke.entries.EntryCache;
import com.abelsky.idea.geekandpoke.entries.EntryInfo;
import com.google.common.base.Function;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static java.util.Collections.emptyList;

/**
 * Caches entries on disk in the IDEA plug-ins folder, like
 * <pre>%USERPROFILE%/.IdeaIC11/config/plugins/geek-and-poke/cache</pre>
 *
 * @author andy
 */
public class OfflineCacheImpl implements EntryCache {

    private final Logger log = Logger.getInstance(getClass());

    private final File cacheBase;

    public OfflineCacheImpl() {
        final PluginId id = PluginId.getId(ComicsPlugin.PLUGIN_ID);

        final IdeaPluginDescriptor plugin = PluginManager.getPlugin(id);
        log.assertTrue(plugin != null, "Cannot find plugin \"" + ComicsPlugin.PLUGIN_ID + "\"");

        //noinspection ConstantConditions
        cacheBase = new File(plugin.getPath(), "cache");
    }

    /**
     * @return All cached entries.
     */
    @Override
    public @NotNull
    List<Entry> getCached() {
        final File[] files = cacheBase.listFiles();
        if (files == null) {
            // Cache base is not a directory or IO error occurred.
            if (log.isDebugEnabled()) {
                log.debug("Cannot list files in: " + cacheBase);
            }

            return emptyList();
        }

        return newLinkedList(filter(transform(newArrayList(files), new Function<File, Entry>() {
            @Override
            public Entry apply(File input) {
                return readFromFile(input);
            }
        }), notNull()));
    }

    /**
     * Tries to read persisted entry from the {@code file}.
     *
     * @return De-serialized object or {@code null} on any error.
     */
    private @Nullable
    Entry readFromFile(@NotNull File name) {
        try {
            final ObjectInputStream in = new ObjectInputStream(new FileInputStream(name));
            try {
                return (Entry) in.readObject();
            } finally {
                in.close();
            }
        } catch (ClassNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
        }
        return null;
    }

    @Override
    public @Nullable Entry getCached(@NotNull EntryInfo entry) {
        synchronized (entry) {
            return readFromFile(new File(cacheBase, entry.getId()));
        }
    }

    private void physicallyDelete(@NotNull Entry entry) {
        if (!entryFile(entry).delete()) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot delete entry: "  + entry);
            }
        }
    }

    public void cache(@NotNull Entry entry) {
        synchronized (entry.getEntryInfo()) {
            if (getCached(entry.getEntryInfo()) != null) {
                physicallyDelete(entry);
            }

            final File entryFile = entryFile(entry);
            FileUtil.createIfDoesntExist(entryFile);

            try {
                final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(entryFile));
                try {
                    out.writeObject(entry);
                } finally {
                    out.close();
                }
            } catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.warn(e);
                }

                // Ensure there's no incomplete entry.
                physicallyDelete(entry);
            }
        }
    }

    private ExecutorService cachingLog = Executors.newSingleThreadExecutor();

    @Override
    public void asyncCache(@NotNull final Entry entry) {
        cachingLog.execute(new Runnable() {
            @Override
            public void run() {
                cache(entry);
            }
        });
    }

    private @NotNull File entryFile(@NotNull Entry e) {
        return new File(cacheBase, e.getEntryInfo().getId());
    }
}
