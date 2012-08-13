package com.abelsky.idea.geekandpoke.ui;

import com.abelsky.idea.geekandpoke.http.EntryHandler;

import javax.swing.*;

/**
 * @author andy
 */
public interface ComicsPanel {
    EntryHandler getEntryHandler();

    JComponent getUIComponent();
}
