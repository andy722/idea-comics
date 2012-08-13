package com.abelsky.idea.geekandpoke;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;

public class Util {

    public static BufferedImage resizeToFit(final BufferedImage image, final int targetWidth, final int targetHeight) {
        final double imageWidth = image.getWidth();
        final double imageHeight = image.getHeight();

        final double scale = Math.min(targetWidth / imageWidth, targetHeight / imageHeight);

        final int resultWidth = (int) (imageWidth * scale);
        final int resultHeight = (int) (imageHeight * scale);

        final BufferedImage resizedImage = new BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_ARGB);

        final AffineTransform at = new AffineTransform() {{
            scale(scale, scale);
        }};

        final Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);

        //noinspection NullableProblems
        g.drawImage(image, at, null);

        return resizedImage;
    }
}
