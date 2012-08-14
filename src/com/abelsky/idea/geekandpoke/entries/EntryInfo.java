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
