package com.abelsky.idea.geekandpoke.ui;

import com.abelsky.idea.geekandpoke.messages.MessageBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.IconLoader;

/**
 * @author andy
 */
public class MoreAction extends AnAction {

    public MoreAction() {
        super(MessageBundle.message("action.more.text"),
              MessageBundle.message("action.more.description"),
              IconLoader.getIcon("/actions/checkOut.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        ServiceManager.getService(ComicsPanel.class).more();
    }
}
