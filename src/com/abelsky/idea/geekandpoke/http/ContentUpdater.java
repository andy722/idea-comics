package com.abelsky.idea.geekandpoke.http;

/**
 *
 *
 * @author andy
 */
public interface ContentUpdater {
    void fetchLastPage(EntryHandler handler);

    void fetchMore(EntryHandler handler);
}
