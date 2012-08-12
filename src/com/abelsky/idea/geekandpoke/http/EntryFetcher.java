package com.abelsky.idea.geekandpoke.http;

import com.abelsky.idea.geekandpoke.entries.OnlineEntry;
import com.abelsky.idea.geekandpoke.entries.OfflineEntry;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author andy
 */
class EntryFetcher {

    /**
     * Downloads an entry.
     */
    public OfflineEntry read(OnlineEntry entry) throws IOException {
        URL url = new URL(entry.getImageUrl());

        final InputStream in = url.openStream();
        try {
            return new OfflineEntry(entry, ImageIO.read(in));
        } finally {
            in.close();
        }
    }
}
