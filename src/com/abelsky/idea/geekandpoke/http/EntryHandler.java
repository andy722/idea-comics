package com.abelsky.idea.geekandpoke.http;

import com.abelsky.idea.geekandpoke.entries.Entry;

/**
 * Handles new fetched entry.
 *
 * @author andy
 */
public interface EntryHandler {
    public void handle(Entry newEntry);
}
