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

package com.abelsky.idea.geekandpoke.http.impl;

import com.abelsky.idea.geekandpoke.entries.Entry;
import com.abelsky.idea.geekandpoke.entries.EntryInfo;
import com.intellij.util.net.HttpConfigurable;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    public Entry read(@NotNull EntryInfo entry) throws IOException {
        @NotNull final URL url = entry.getImageUrl();

        // Ensure that proxy (if any) is set up for this request.
        final HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
        httpConfigurable.prepareURL(url.toExternalForm());

        @NotNull final InputStream in = new BufferedInputStream(url.openStream());
        try {
            return new Entry(entry, ImageIO.read(in));
        } finally {
            in.close();
        }
    }
}
