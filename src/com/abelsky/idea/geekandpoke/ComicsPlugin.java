package com.abelsky.idea.geekandpoke;

import com.abelsky.idea.geekandpoke.http.ContentUpdater;
import com.abelsky.idea.geekandpoke.ui.ComicsPanel;
import com.abelsky.idea.geekandpoke.ui.RefreshAction;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author andy
 */
public class ComicsPlugin implements ApplicationComponent {

    private static final int UPDATE_PERIOD = 15 * 60 * 60 * 1000;
    public static final String PLUGIN_ID = "com.abelsky.idea.geekandpoke";

    @Override
    public void initComponent() {
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        final Timer timer = new Timer("Geek and Poke updater");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RefreshAction.fetchLastPage();
            }
        }, 0, ComicsPlugin.UPDATE_PERIOD);
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
