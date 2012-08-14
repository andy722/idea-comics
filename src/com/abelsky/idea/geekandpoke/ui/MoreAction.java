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

import com.abelsky.idea.geekandpoke.http.ContentUpdater;
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
        fetchMore();
    }

    @SuppressWarnings("WeakerAccess")
    public static void fetchMore() {
        final ComicsPanel panel = ServiceManager.getService(ComicsPanel.class);
        final ContentUpdater updater = ServiceManager.getService(ContentUpdater.class);

        updater.fetchMore(panel.getEntryHandler());
    }
}
