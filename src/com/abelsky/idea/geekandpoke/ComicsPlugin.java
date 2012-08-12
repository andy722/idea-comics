package com.abelsky.idea.geekandpoke;

import com.abelsky.idea.geekandpoke.ui.ComicsPanel;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author andy
 */
public class ComicsPlugin implements ApplicationComponent {

    public static final int UPDATE_PERIOD = 15 * 60 * 60 * 1000;

    @Override
    public void initComponent() {
        ServiceManager.getService(ComicsPanel.class).startUpdateTimer();
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getName();
    }

}
