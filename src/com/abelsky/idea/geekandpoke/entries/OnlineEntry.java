package com.abelsky.idea.geekandpoke.entries;

import java.io.Serializable;
import java.util.Date;

public class OnlineEntry implements Serializable {

    private final String id;
    private final String title;
    protected final Date date;
    private final String url;
    private final String imageUrl;

    public OnlineEntry(OnlineEntry other) {
        this(other.id, other.title, other.date, other.url, other.imageUrl);
    }

    public OnlineEntry(String id, String title, Date date, String url, String imageUrl) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "OnlineEntry{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", url='" + url + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OnlineEntry that = (OnlineEntry) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
