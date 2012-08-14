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

package com.abelsky.idea.geekandpoke.entries;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 *
 * @author andy
 */
public class Entry implements Serializable {

    private static final transient Logger log = Logger.getInstance(Entry.class);

    /**
     * Newer entries come first.
     */
    public static final Comparator<Entry> PUBLICATION_DATE_COMPARATOR = new Comparator<Entry>() {
        @Override
        public int compare(@NotNull Entry o1, @NotNull Entry o2) {
            return -o1.entryInfo.date.compareTo(o2.entryInfo.date);
        }
    };


    private final EntryInfo entryInfo;

    /**
     * Store image on disk, read on demand.
     */
    @Nullable
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

    @Nullable
    public BufferedImage getImage() {
        if (imageRef == null) {
            log.assertTrue(isDeleted());
            return null;
        }

        log.assertTrue(!isDeleted());

        @Nullable BufferedImage image = imageRef.get();
        if (image == null) {
            imageRef = new SoftReference<BufferedImage>(image = fetchCachedImage());
        }
        return image;
    }

    @Nullable
    private BufferedImage fetchCachedImage() {
        @Nullable final Entry cached = ServiceManager.getService(EntryCache.class).getCached(entryInfo);
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

    private void writeObject(@NotNull ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (imageRef != null) {
            @Nullable BufferedImage image = imageRef.get();
            if (image == null) {
                image = fetchCachedImage();
                imageRef = new SoftReference<BufferedImage>(image);
            }
            ImageIO.write(image, "png", ImageIO.createImageOutputStream(out));
        }
    }

    private void readObject(@NotNull ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        imageRef = new SoftReference<BufferedImage>(ImageIO.read(ImageIO.createImageInputStream(in)));
    }
}
