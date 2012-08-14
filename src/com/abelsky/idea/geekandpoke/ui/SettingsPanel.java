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

package com.abelsky.idea.geekandpoke.ui;

import com.abelsky.idea.geekandpoke.entries.EntryCache;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.abelsky.idea.geekandpoke.messages.MessageBundle.message;

/**
 * @author andy
 */
public class SettingsPanel implements Configurable {

    private Panel panel;

    private static class Panel extends JPanel {
        private final JLabel cacheSizeValueLabel;

        public Panel() {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            final JPanel cachePanel = new JPanel(new BorderLayout());
            final Border border = new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED), new EmptyBorder(3, 5, 3, 5));
            cachePanel.setBorder(new TitledBorder(border, message("configuration.panel.cache.title")));

            final JPanel gridWrapper = new JPanel(new GridLayout(1, 2));
            gridWrapper.add(new JLabel(message("configuration.panel.cache.size")));
            gridWrapper.add(cacheSizeValueLabel = new JLabel());
            cachePanel.add(gridWrapper, BorderLayout.NORTH);

            final JButton clearCacheButton = new JButton(message("configuration.panel.cache.clear"));
            clearCacheButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ServiceManager.getService(EntryCache.class).clear();
                    updateCacheSize();
                }
            });

            JPanel buttonWrapper = new JPanel(new GridLayout(1, 3));
            buttonWrapper.setBorder(IdeBorderFactory.createEmptyBorder(3));
            buttonWrapper.add(clearCacheButton);
            buttonWrapper.add(new Spacer());
            buttonWrapper.add(new Spacer());
            cachePanel.add(buttonWrapper, BorderLayout.SOUTH);

            cachePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, cachePanel.getPreferredSize().height));
            add(cachePanel);

            updateCacheSize();
        }

        public void updateCacheSize() {
            final long cacheSizeInBytes = ServiceManager.getService(EntryCache.class).getCacheSizeInButes();
            cacheSizeValueLabel.setText(FileUtils.byteCountToDisplaySize(cacheSizeInBytes));
        }
    }

    @Nls
    @Override
    public String getDisplayName() {
        return message("configuration.display.name");
    }

    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/icon-16x16.png");
    }

    @Override
    public String getHelpTopic() {
        // No help is available.
        return null;
    }

    @Override
    public JComponent createComponent() {
        if (panel == null) {
            panel = new Panel();
        }
        return panel;
    }

    @Override
    public boolean isModified() {
        // Nothing to edit, never modified.
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        // Nothing here.
    }

    @Override
    public void reset() {
        // Nothing here.
    }

    @Override
    public void disposeUIResources() {
        // Nothing here.s
    }
}
