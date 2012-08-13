package com.abelsky.idea.geekandpoke.entries;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
public class Entry implements Serializable {

    private static final transient Logger log = Logger.getInstance(Entry.class);

    /**
     * Newer entries come first.
     */
    public static final Comparator<Entry> PUBLICATION_DATE_COMPARATOR = new Comparator<Entry>() {
        @Override
        public int compare(Entry o1, Entry o2) {
            return -o1.entryInfo.date.compareTo(o2.entryInfo.date);
        }
    };


    private final EntryInfo entryInfo;

    /**
     * Store image on disk, read on demand.
     */
    private transient SoftReference<BufferedImage> imageRef;

    /**
     * {@code true} iff this entry has not already been opened.
     */
    private boolean isUnread = true;

    /**
     * {@code true} iff this entry has been deleted from the UI. Deleted entry is marked with this flag,
     * its' image is set to {@code null} and the entry is written back to the cache (to skip downloading it again).
     */
    private boolean isDeleted;

    public Entry(EntryInfo onlineEntry, BufferedImage image) {
        entryInfo = onlineEntry;
        this.imageRef = new SoftReference<BufferedImage>(image);
    }

    public BufferedImage getImage() {
        if (imageRef == null) {
            log.assertTrue(isDeleted());
            return null;
        }

        log.assertTrue(!isDeleted());

        BufferedImage image = imageRef.get();
        if (image == null) {
            imageRef = new SoftReference<BufferedImage>(image = fetchCachedImage());
        }
        return image;
    }

    private BufferedImage fetchCachedImage() {
        final Entry cached = ServiceManager.getService(EntryCache.class).getCached(entryInfo);
        log.assertTrue(cached != null, "An entry has missed the cache");

        //noinspection ConstantConditions
        return cached.getImage();
    }

    public boolean isUnread() {
        return isUnread;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted() {
        if (!isDeleted) {
            isDeleted = true;
            imageRef = null;
            ServiceManager.getService(EntryCache.class).asyncCache(this);
        }
    }

    public void setAlreadyRead() {
        if (isUnread) {
            isUnread = false;
            ServiceManager.getService(EntryCache.class).asyncCache(this);
        }
    }

    public EntryInfo getEntryInfo() {
        return entryInfo;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Entry) && new EqualsBuilder().appendSuper(super.equals(o)).isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).toHashCode();
    }

    ////////////////////
    ////////////////////  (De)serialization support.
    ////////////////////

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (imageRef != null) {
            BufferedImage image = imageRef.get();
            if (image == null) {
                image = fetchCachedImage();
                imageRef = new SoftReference<BufferedImage>(image);
            }
            ImageIO.write(image, "png", ImageIO.createImageOutputStream(out));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        imageRef = new SoftReference<BufferedImage>(ImageIO.read(ImageIO.createImageInputStream(in)));
    }
}
