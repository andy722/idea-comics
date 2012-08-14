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

package com.abelsky.idea.geekandpoke.ui.impl;

import com.abelsky.idea.geekandpoke.entries.Entry;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Displays entry title.
 *
 * @author andy
 */
class ComicTitleCellRenderer extends ColoredListCellRenderer {

    @Override
    protected void customizeCellRenderer(final JList list, Object value, int index, boolean selected, boolean hasFocus) {
        @NotNull final Entry entry = (Entry) value;

        SimpleTextAttributes attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
        if (entry.isUnread()) {
            attributes = SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
        }

        append(entry.getEntryInfo().getTitle(), attributes);
    }
}
