package com.abelsky.idea.geekandpoke.ui;

import com.abelsky.idea.geekandpoke.http.ContentUpdater;
import com.abelsky.idea.geekandpoke.messages.MessageBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.IconLoader;

/**
 * @author andy
 */
public class RefreshAction extends AnAction {

    public RefreshAction() {
        super(MessageBundle.message("action.refresh.text"),
                MessageBundle.message("action.refresh.description"),
                IconLoader.getIcon("/actions/sync.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        fetchLastPage();
    }

    public static void fetchLastPage() {
        final ComicsPanel panel = ServiceManager.getService(ComicsPanel.class);
        final ContentUpdater updater = ServiceManager.getService(ContentUpdater.class);

        updater.fetchLastPage(panel.getEntryHandler());
    }
}
