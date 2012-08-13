package com.abelsky.idea.geekandpoke.http.impl;

import com.abelsky.idea.geekandpoke.entries.Entry;
import com.abelsky.idea.geekandpoke.entries.EntryInfo;
import com.intellij.util.net.HttpConfigurable;

import javax.imageio.ImageIO;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Fetches entry info by its URL (actually, only an image - other stuff is read by now).
 *
 * @author andy
 */
class EntryFetcher {

    /**
     * Downloads an entry.
     */
    public Entry read(EntryInfo entry) throws IOException {
        final URL url = entry.getImageUrl();

        // Ensure that proxy (if any) is set up for this request.
        final HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
        httpConfigurable.prepareURL(url.toExternalForm());

        final InputStream in = new BufferedInputStream(url.openStream());
        try {
            return new Entry(entry, ImageIO.read(in));
        } finally {
            in.close();
        }
    }
}
