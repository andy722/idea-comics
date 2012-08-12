package com.abelsky.idea.geekandpoke.ui;

import com.abelsky.idea.geekandpoke.messages.MessageBundle;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;

/**
 * @author andy
 */
public class ComicsPanelFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        final ComicsPanel panel = ServiceManager.getService(ComicsPanel.class);
        toolWindow.setTitle(MessageBundle.message("panel.title"));
        toolWindow.getContentManager().addContent(
                ContentFactory.SERVICE.getInstance().createContent(panel, "by Oliver Widder (http://www.geekandpoke.com)", false));
    }

}
