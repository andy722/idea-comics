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

package com.abelsky.idea.geekandpoke.messages;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * Access to localized texts.
 *
 * @author andy
 */
public class MessageBundle {
    @Nullable
    private static Reference<ResourceBundle> bundleRef;

    @NonNls
    private static final String BUNDLE = "com.abelsky.idea.geekandpoke.messages.MessageBundle";

    private MessageBundle() {
    }

    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE)String key, Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    @NotNull
    private static ResourceBundle getBundle() {
        @Nullable ResourceBundle bundle = null;
        if (bundleRef != null) {
            bundle = bundleRef.get();
        }

        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            bundleRef = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }

}
