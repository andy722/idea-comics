package com.abelsky.idea.geekandpoke.ui.impl;

import com.abelsky.idea.geekandpoke.entries.Entry;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;

/**
 * Displays entry title.
 *
 * @author andy
 */
class ComicTitleCellRenderer extends ColoredListCellRenderer {

    @Override
    protected void customizeCellRenderer(final JList list, Object value, int index, boolean selected, boolean hasFocus) {
        final Entry entry = (Entry) value;

        SimpleTextAttributes attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
        if (entry.isUnread()) {
            attributes = SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
        }

        append(entry.getEntryInfo().getTitle(), attributes);
    }
}
