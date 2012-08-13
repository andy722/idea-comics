package com.abelsky.idea.geekandpoke.entries;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

public class EntryInfo implements Serializable {

    private final String id;
    private final String title;
    protected final Date date;
    private final URL permLink;
    private final URL imageUrl;

    public EntryInfo(@NotNull String id, @NotNull String title, @NotNull Date date, @NotNull URL permLink, @NotNull URL imageUrl) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.permLink = permLink;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public URL getPermLink() {
        return permLink;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "EntryInfo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", permLink='" + permLink + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof EntryInfo) && new EqualsBuilder().append(id, ((EntryInfo) o).id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
}
