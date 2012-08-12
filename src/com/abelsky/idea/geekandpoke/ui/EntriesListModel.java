package com.abelsky.idea.geekandpoke.ui;

import com.abelsky.idea.geekandpoke.entries.OfflineEntry;
import com.intellij.ui.SortedListModel;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.Collection;

/**
 * @author andy
 */
class EntriesListModel implements ListModel {

    private final SortedListModel<OfflineEntry> listModel = SortedListModel.create(OfflineEntry.PUBLICATION_DATE_COMPARATOR);

    public EntriesListModel() {
    }

    @Override
    public int getSize() {
        return listModel.getSize();
    }

    @Override
    public OfflineEntry getElementAt(int index) {
        return (OfflineEntry) listModel.getElementAt(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listModel.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listModel.removeListDataListener(l);
    }

    public void add(OfflineEntry entry) {
        if (!entry.isDeleted() && !listModel.getItems().contains(entry)) {
            listModel.add(entry);
        }
    }

    public void addAll(Collection<OfflineEntry> entries) {
        for (OfflineEntry e : entries) {
            add(e);
        }
    }

    public void remove(OfflineEntry entry) {
        listModel.remove(entry);
    }

    public boolean contains(OfflineEntry entry) {
        return listModel.getItems().contains(entry);
    }
}
