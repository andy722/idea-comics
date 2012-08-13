package com.abelsky.idea.geekandpoke.ui.impl;

import com.abelsky.idea.geekandpoke.entries.Entry;
import com.intellij.ui.SortedListModel;

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

    public void add(Entry entry) {
        if (!entry.isDeleted() && !listModel.getItems().contains(entry)) {
            listModel.add(entry);
        }
    }

    public void addAll(Collection<Entry> entries) {
        for (Entry e : entries) {
            add(e);
        }
    }

    public void remove(Entry entry) {
        listModel.remove(entry);
    }

    public boolean contains(Entry entry) {
        return listModel.getItems().contains(entry);
    }
}
