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
import com.intellij.ui.SortedListModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.Collection;

/**
 * @author andy
 */
class EntriesListModel implements ListModel {

    private final SortedListModel<Entry> listModel = SortedListModel.create(Entry.PUBLICATION_DATE_COMPARATOR);

    public EntriesListModel() {
    }

    @Override
    public int getSize() {
        return listModel.getSize();
    }

    @NotNull
    @Override
    public Entry getElementAt(int index) {
        return (Entry) listModel.getElementAt(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listModel.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listModel.removeListDataListener(l);
    }

    public void add(@NotNull Entry entry) {
        if (!entry.isDeleted() && !listModel.getItems().contains(entry)) {
            listModel.add(entry);
        }
    }

    public void addAll(@NotNull Collection<Entry> entries) {
        for (@NotNull Entry e : entries) {
            add(e);
        }
    }

    public void remove(Entry entry) {
        listModel.remove(entry);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(Entry entry) {
        return listModel.getItems().contains(entry);
    }
}
