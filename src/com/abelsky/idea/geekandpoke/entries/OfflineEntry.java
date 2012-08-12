package com.abelsky.idea.geekandpoke.entries;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Comparator;

/**
 * Downloaded entry. Has an image (instead of just URL) and some additional flags.
 */
public class OfflineEntry extends OnlineEntry implements Serializable {

    private static final transient Logger log = Logger.getInstance(OfflineEntry.class);

    /**
     * Newer entries come first.
     */
    public static final Comparator<OfflineEntry> PUBLICATION_DATE_COMPARATOR = new Comparator<OfflineEntry>() {
        @Override
        public int compare(OfflineEntry o1, OfflineEntry o2) {
            return -o1.date.compareTo(o2.date);
        }
    };

    /**
     * Store image on disk, read on demand.
     */
    private transient SoftReference<BufferedImage> iconRef;

    /**
     * {@code true} iff this entry has already been opened.
     */
    private boolean alreadyRead;

    /**
     * {@code true} iff this entry has been deleted from the UI. Deleted entry is marked with this flag,
     * its' image is set to {@code null} and the entry is written back to the cache (to skip downloading it again).
     */
    private boolean deleted;

    public OfflineEntry(OnlineEntry onlineEntry, BufferedImage icon) {
        super(onlineEntry);
        this.iconRef = new SoftReference<BufferedImage>(icon);
    }

    public BufferedImage getImage() {
        if (iconRef == null) {
            log.assertTrue(isDeleted());
            return null;
        }

        log.assertTrue(!isDeleted());

        BufferedImage image = iconRef.get();
        if (image == null) {
            iconRef = new SoftReference<BufferedImage>(image = fetchCachedImage());
        }
        return image;
    }

    private BufferedImage fetchCachedImage() {
        final OfflineEntry cached = ServiceManager.getService(OfflineCache.class).getCached(this);
        log.assertTrue(cached != null, "An entry has missed the cache");

        //noinspection ConstantConditions
        return cached.getImage();
    }

    public boolean isAlreadyRead() {
        return alreadyRead;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted() {
        deleted = true;
        iconRef = null;
        ServiceManager.getService(OfflineCache.class).cache(this);
    }

    public void setAlreadyRead(boolean b) {
        alreadyRead = b;
        ServiceManager.getService(OfflineCache.class).cache(this);
    }

    ////////////////////
    ////////////////////  (De)serialization support.
    ////////////////////

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (iconRef != null) {
            BufferedImage image = iconRef.get();
            if (image == null) {
                image = fetchCachedImage();
                iconRef = new SoftReference<BufferedImage>(image);
            }
            ImageIO.write(image, "png", ImageIO.createImageOutputStream(out));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        iconRef = new SoftReference<BufferedImage>(ImageIO.read(ImageIO.createImageInputStream(in)));
    }
}
