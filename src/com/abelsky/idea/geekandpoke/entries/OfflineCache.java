package com.abelsky.idea.geekandpoke.entries;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Caches entries locally.
 *
 * @author andy
 */
public class OfflineCache {

    private static final String PLUGIN_ID = "com.abelsky.idea.geekandpoke";

    private final Logger log = Logger.getInstance(getClass());

    private final File cacheBase;

    public OfflineCache() {
        final PluginId id = PluginId.getId(PLUGIN_ID);

        final IdeaPluginDescriptor plugin = PluginManager.getPlugin(id);
        log.assertTrue(plugin != null);

        //noinspection ConstantConditions
        cacheBase = new File(plugin.getPath(), "cache");
    }

    /**
     * @return All cached entries.
     */
    public @NotNull
    List<OfflineEntry> getCached() {
        File[] files = cacheBase.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        final ArrayList<OfflineEntry> read = new ArrayList<OfflineEntry>();
        for (File f : files) {
            OfflineEntry entry = readFromFile(f);
            if (entry != null) {
                read.add(entry);
            }
        }
        return read;
    }

    /**
     * Tries to read persisted entry from the {@code file}.
     *
     * @return De-serialized object or {@code null} on any error.
     */
    private @Nullable OfflineEntry readFromFile(@NotNull File name) {
        try {
            ObjectInputStream out = null;
            try {
                out = new ObjectInputStream(new FileInputStream(name));
                return (OfflineEntry) out.readObject();
            } finally {
                if (out != null) {
                    out.close();
                }
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

    public @Nullable <T extends OnlineEntry> OfflineEntry getCached(@NotNull T entry) {
        return readFromFile(new File(cacheBase, entry.getId()));
    }

    private void physicallyDelete(@NotNull OfflineEntry entry) {
        if (!getFileForEntry(entry).delete()) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot delete entry: "  + entry);
            }
        }
    }

    public void cache(@NotNull OfflineEntry entry) {
        if (getCached(entry) != null) {
            physicallyDelete(entry);
        }

        final File entryBase = getFileForEntry(entry);
        FileUtil.createIfDoesntExist(entryBase);

        try {
            final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(entryBase));
            try {
                out.writeObject(entry);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            log.warn(e);

            // Ensure there's no incomplete entry.
            physicallyDelete(entry);
        }
    }

    private @NotNull File getFileForEntry(@NotNull OfflineEntry e) {
        return new File(cacheBase, e.getId());
    }
}
