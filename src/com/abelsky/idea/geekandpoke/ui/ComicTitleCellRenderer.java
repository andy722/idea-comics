package com.abelsky.idea.geekandpoke.ui;

import com.abelsky.idea.geekandpoke.entries.OfflineEntry;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;

/**
 * @author andy
 */
class ComicTitleCellRenderer extends ColoredListCellRenderer {

    private final Logger log = Logger.getInstance(getClass());

    @Override
    protected void customizeCellRenderer(final JList list, Object value, int index, boolean selected, boolean hasFocus) {
        final OfflineEntry entry = (OfflineEntry) value;

        SimpleTextAttributes attrs = SimpleTextAttributes.REGULAR_ATTRIBUTES;
        if (!entry.isAlreadyRead()) {
            attrs = SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
        }

        append(entry.getTitle(), attrs);
    }
}
