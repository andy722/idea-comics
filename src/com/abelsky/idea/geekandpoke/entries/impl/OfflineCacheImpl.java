/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
import com.intellij.util.ConcurrencyUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

    private final ExecutorService cachingLog = ConcurrencyUtil.newSingleThreadExecutor("Comics updater");

    @Nullable
    private final File root;

    public OfflineCacheImpl() {
        root = initRootDirectory();
        if (log.isDebugEnabled()) {
            log.debug("Using cache at \"" + root + "\"");
        }
    }

    private @Nullable File initRootDirectory() {
        final PluginId id = PluginId.getId(ComicsPlugin.PLUGIN_ID);

        @Nullable final IdeaPluginDescriptor plugin = PluginManager.getPlugin(id);
        log.assertTrue(plugin != null, "Cannot find plugin \"" + ComicsPlugin.PLUGIN_ID + "\"");

        //noinspection ConstantConditions
        final File pluginPath = plugin.getPath();

        if (pluginPath.isDirectory()) {
            // <my-plugin>/cache
            return new File(pluginPath, "cache");

        } else {
            // <plugins-directory>/<my-plugin>.jar
            final String absolutePath = pluginPath.getAbsolutePath();

            // <plugins-directory>
            final String rootPath = FilenameUtils.getFullPath(absolutePath);

            // <my-plugin>
            final String baseName = FilenameUtils.getBaseName(absolutePath);

            // <plugins-directory>/<my-plugin>.cache
            final File cachePath = new File(rootPath, baseName + ".cache");

            if (cachePath.isFile()) {
                FileUtil.delete(cachePath);
            }

            if (!cachePath.exists()) {
                if (!cachePath.mkdir()) {
                    // Couldn't create anything useful, just return some temporary directory.
                    if (log.isDebugEnabled()) {
                        log.debug("Couldn't initialize cache at " + cachePath);
                    }

                    try {
                        return FileUtil.createTempDirectory(baseName, "");
                    } catch (IOException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Couldn't create temporary directory with base name \"" + baseName +
                                    "\" in \"" + FileUtil.getTempDirectory() + "\"");
                        }
                    }
                }
            }

            return cachePath;
        }
    }

    private boolean isEnabled() {
        return root != null && root.isDirectory();
    }

    private List<File> getCacheContents() {
        if (!isEnabled()) {
            return emptyList();
        }

        @Nullable final File[] files = root.listFiles();
        if (files == null) {
            // Cache base is not a directory or IO error occurred.
            if (log.isDebugEnabled()) {
                log.debug("Cannot list files in: " + root);
            }

            return emptyList();
        }

        return newArrayList(files);
    }

    /**
     * @return All cached entries.
     */
    @Override
    public @NotNull
    List<Entry> getCached() {
        return newLinkedList(filter(transform(getCacheContents(), new Function<File, Entry>() {
            @Nullable
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
    Entry readFromFile(@NotNull File file) {
        if (log.isDebugEnabled()) {
            log.debug("Reading entry from \"" + file + "\"");
        }

        try {
            @NotNull final ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
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
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (entry) {
            return readFromFile(new File(root, entry.getId()));
        }
    }

    private void physicallyDelete(@NotNull Entry entry) {
        if (!entryFile(entry).delete()) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot delete entry: "  + entry);
            }
        }
    }

    private void cache(@NotNull Entry entry) {
        if (!isEnabled()) {
            return;
        }

        synchronized (entry.getEntryInfo()) {
            if (getCached(entry.getEntryInfo()) != null) {
                physicallyDelete(entry);
            }

            @NotNull final File entryFile = entryFile(entry);
            FileUtil.createIfDoesntExist(entryFile);

            try {
                @NotNull final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(entryFile));
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

    @Override
    public void asyncCache(@NotNull final Entry entry) {
        cachingLog.execute(new Runnable() {
            @Override
            public void run() {
                cache(entry);
            }
        });
    }

    @Override
    public void clear() {
        for (@NotNull File cacheElement : getCacheContents()) {
            FileUtil.delete(cacheElement);
        }
    }

    @Override
    public long getCacheSizeInBytes() {
        if (!isEnabled()) {
            return 0L;
        }
        return FileUtils.sizeOf(root);
    }

    private @NotNull File entryFile(@NotNull Entry e) {
        return new File(root, e.getEntryInfo().getId());
    }
}
