package com.abelsky.idea.geekandpoke.ui;

import com.abelsky.idea.geekandpoke.ComicsPlugin;
import com.abelsky.idea.geekandpoke.Util;
import com.abelsky.idea.geekandpoke.entries.OfflineCache;
import com.abelsky.idea.geekandpoke.entries.OfflineEntry;
import com.abelsky.idea.geekandpoke.http.ContentUpdater;
import com.abelsky.idea.geekandpoke.messages.MessageBundle;
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
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * The main toolbar panel displaying titles and images.
 *
 * @author andy
 */
public class ComicsPanel extends JPanel implements ListSelectionListener {

    private final Logger log = Logger.getInstance(getClass());

    private final JList entriesList;
    private final JPanel imagePanel;

    private final ContentUpdater updateTask = new ContentUpdater(new NewEntryHandlerImpl());

    private final EntriesListModel entriesListModel = new EntriesListModel();
    private OfflineEntry currentEntry;

    public ComicsPanel() {
        super(new BorderLayout());

        // Toolbar on the top: sync/fetch more buttons
        add(createToolbar(), BorderLayout.NORTH);

        imagePanel = createImagePanel();
        entriesList = createEntriesList();

        final JScrollPane entriesListScroll = new JScrollPane(entriesList);
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, entriesListScroll, imagePanel);

        add(splitPane, BorderLayout.CENTER);

        entriesListModel.addAll(ServiceManager.getService(OfflineCache.class).getCached());
    }

    private JList createEntriesList() {
        final JList entriesList = new JList(entriesListModel);
        entriesList.setCellRenderer(new ComicTitleCellRenderer());
        entriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entriesList.addListSelectionListener(this);
        entriesList.setVisibleRowCount(5);

        entriesList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() != KeyEvent.VK_DELETE) {
                    return;
                }

                final int idx = entriesList.getSelectedIndex();
                if (idx < 0 || idx >= entriesListModel.getSize()) {
                    return;
                }

                final OfflineEntry entry = entriesListModel.getElementAt(idx);

                if (log.isDebugEnabled()) {
                    log.debug("Deleting entry: " + e);
                }
                entry.setDeleted();

                entriesListModel.remove(entry);
                entriesList.revalidate();
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
                drawCurrentImage();
            }
        });
        return imagePanel;
    }

    public void forceUpdate() {
        updateTask.fetchLastPage();
    }

    public void more() {
        updateTask.fetchMore();
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

    /**
     * Handles entry selection, displays a corresponding image.
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        final int idx = entriesList.getSelectedIndex();
        updateIndex(idx);
    }

    private boolean updateIndex(int idx) {
        if ((idx < 0) || (idx >= entriesListModel.getSize())) {
            return false;
        }
        final OfflineEntry entry = entriesListModel.getElementAt(idx);
        if (currentEntry == null || !currentEntry.equals(entry)) {
            currentEntry = entry;
            drawCurrentImage();

            entry.setAlreadyRead(true);
            entriesList.revalidate();

            return true;
        }
        return false;
    }

    private void drawCurrentImage() {
        if (currentEntry == null) {
            return;
        }

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

        imagePanel.setMinimumSize(new Dimension(0, 0));
    }

    private class OpenPermLinkAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                BrowserUtil.launchBrowser(currentEntry.getUrl());
            }
        }
    }

    private class NewEntryHandlerImpl implements ContentUpdater.NewEntryHandler {
        @Override
        public void handleNewEntry(final OfflineEntry entry) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!entriesListModel.contains(entry)) {
                        notifyNewEntry();
                    }
                    entriesListModel.add(entry);
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

    public void startUpdateTimer() {
        final java.util.Timer timer = new java.util.Timer("Geek and Poke updater");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTask.fetchLastPage();
            }
        }, 0, ComicsPlugin.UPDATE_PERIOD);
    }

}
