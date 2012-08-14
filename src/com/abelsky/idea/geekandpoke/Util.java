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

package com.abelsky.idea.geekandpoke;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;

/**
 * A dump of various utilities.
 *
 * @author andy
 */
public class Util {

    /**
     * Returns a copy of {@code image} scaled proportionally to fit
     * into {@code targetWidht} x {@code targetHeight} box.
     */
    @NotNull
    public static BufferedImage resizeToFit(@NotNull final BufferedImage image, final int targetWidth, final int targetHeight) {
        final double imageWidth = image.getWidth();
        final double imageHeight = image.getHeight();

        final double scale = Math.min(targetWidth / imageWidth, targetHeight / imageHeight);

        final int resultWidth = (int) (imageWidth * scale);
        final int resultHeight = (int) (imageHeight * scale);

        @NotNull
        final BufferedImage resizedImage = new BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_ARGB);

        @NotNull final AffineTransform at = new AffineTransform() {{
            scale(scale, scale);
        }};

        final Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);

        //noinspection NullableProblems
        g.drawImage(image, at, null);

        return resizedImage;
    }
}
