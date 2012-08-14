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

import com.abelsky.idea.geekandpoke.Util;
import com.abelsky.idea.geekandpoke.entries.Entry;
import com.abelsky.idea.geekandpoke.entries.EntryCache;
import com.abelsky.idea.geekandpoke.http.EntryHandler;
import com.abelsky.idea.geekandpoke.messages.MessageBundle;
import com.abelsky.idea.geekandpoke.ui.ComicsPanel;
import com.abelsky.idea.geekandpoke.ui.MoreAction;
import com.abelsky.idea.geekandpoke.ui.RefreshAction;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * The main toolbar panel displaying titles and images.
 *
 * @author andy
 */
class ComicsPanelImpl extends JPanel implements ComicsPanel {

    private final Logger log = Logger.getInstance(getClass());

    private final JList entriesList;
    private final JPanel imagePanel;

    private final EntryHandler entryHandler = new NewEntryHandlerImpl();

    private final EntriesListModel entriesListModel = new EntriesListModel();
    private Entry currentEntry;

    public ComicsPanelImpl() {
        super(new BorderLayout());

        // Toolbar on the top: sync/fetch more buttons
        add(createToolbar(), BorderLayout.NORTH);

        imagePanel = createImagePanel();
        entriesList = createEntriesList();

        final JScrollPane entriesListScroll = new JScrollPane(entriesList);
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, entriesListScroll, imagePanel);

        add(splitPane, BorderLayout.CENTER);

        entriesListModel.addAll(ServiceManager.getService(EntryCache.class).getCached());
    }

    private JList createEntriesList() {
        final JList entriesList = new JList(entriesListModel);
        entriesList.setCellRenderer(new ComicTitleCellRenderer());
        entriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entriesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Displays a corresponding image on entry selection.
                final int idx = entriesList.getSelectedIndex();
                selectEntry(idx);
            }
        });
        entriesList.setVisibleRowCount(5);

        entriesList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() != KeyEvent.VK_DELETE) {
                    return;
                }

                final int idx = entriesList.getSelectedIndex();
                deleteEntry(e, idx);
            }
        });

        return entriesList;
    }

    private JPanel createImagePanel() {
        final JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Adapt image on panel resize.
                if (currentEntry != null) {
                    drawCurrentImage();
                }
            }
        });
        return imagePanel;
    }

    private JComponent createToolbar() {
        final ActionManager actionManager = ActionManager.getInstance();
        final DefaultActionGroup actionGroup = new DefaultActionGroup();

        actionGroup.add(new RefreshAction());
        actionGroup.add(new MoreAction());

        final ActionToolbar actionToolbar = actionManager.createActionToolbar("ComicsPanelToolbar", actionGroup, true);
        final JComponent actionToolbarComponent = actionToolbar.getComponent();
        final JPanel actionsPanel = new JPanel();

        actionToolbar.setReservePlaceAutoPopupIcon(false);
        actionsPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        actionsPanel.add(actionToolbarComponent);

        return actionsPanel;
    }

    private void selectEntry(int idx) {
        if ((idx < 0) || (idx >= entriesListModel.getSize())) {
            return;
        }

        final Entry entry = entriesListModel.getElementAt(idx);
        if (entry.equals(currentEntry)) {
            return;
        }

        currentEntry = entry;
        drawCurrentImage();

        entry.setAlreadyRead();
        entriesList.revalidate();
    }

    private void deleteEntry(KeyEvent e, int idx) {
        if ((idx < 0) || (idx >= entriesListModel.getSize())) {
            return;
        }

        final Entry entry = entriesListModel.getElementAt(idx);

        if (log.isDebugEnabled()) {
            log.debug("Deleting entry: " + e);
        }
        entry.setDeleted();

        entriesListModel.remove(entry);
        entriesList.revalidate();
    }

    private void drawCurrentImage() {
        final BufferedImage image = currentEntry.getImage();
        if (image == null) {
            imagePanel.removeAll();
            imagePanel.revalidate();
            return;
        }

        final int width = imagePanel.getWidth();
        final int height = imagePanel.getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        imagePanel.removeAll();

        final BufferedImage resizedImage = Util.resizeToFit(image, width, height);

        final JLabel label = new JLabel(new ImageIcon(resizedImage));
        label.addMouseListener(new OpenPermLinkAdapter());
        label.setToolTipText(MessageBundle.message("entry.image.tooltip"));
        imagePanel.add(label);
        imagePanel.revalidate();

        // Allows this panel to be downsized.
        imagePanel.setMinimumSize(new Dimension(0, 0));
    }

    private class OpenPermLinkAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                final String link = currentEntry.getEntryInfo().getPermLink().toExternalForm();
                BrowserUtil.launchBrowser(link);
            }
        }
    }

    private class NewEntryHandlerImpl implements EntryHandler {
        @Override
        public void handle(final Entry newEntry) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!entriesListModel.contains(newEntry)) {
                        notifyNewEntry();
                    }
                    entriesListModel.add(newEntry);
                }
            });
        }
    }

    private void notifyNewEntry() {
        final Notification newEntryNotification = new Notification(
                MessageBundle.message("notification.new.strip.group"),
                MessageBundle.message("notification.new.strip.title"),
                MessageBundle.message("notification.new.strip.content"),
                NotificationType.INFORMATION);

        Notifications.Bus.notify(newEntryNotification);
    }

    @Override
    public EntryHandler getEntryHandler() {
        return entryHandler;
    }

    @Override
    public JComponent getUIComponent() {
        return this;
    }


}
