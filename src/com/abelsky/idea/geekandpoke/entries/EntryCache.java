package com.abelsky.idea.geekandpoke.entries;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Caches fetched entries locally.
 *
 * @author andy
 */
public interface EntryCache {
    @NotNull
    List<Entry> getCached();

    @Nullable
    Entry getCached(@NotNull EntryInfo entry);

    void asyncCache(@NotNull Entry entry);
}
