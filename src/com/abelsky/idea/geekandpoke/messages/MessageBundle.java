package com.abelsky.idea.geekandpoke.messages;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * @author andy
 */
public class MessageBundle {
    private static Reference<ResourceBundle> bundleRef;

    @NonNls
    private static final String BUNDLE = "com.abelsky.idea.geekandpoke.messages.MessageBundle";

    private MessageBundle() {
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE)String key, Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;
        if (MessageBundle.bundleRef != null) {
            bundle = MessageBundle.bundleRef.get();
        }

        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            MessageBundle.bundleRef = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }

}
