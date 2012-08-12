package com.abelsky.idea.geekandpoke;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;

public class Util {

    public static String readToString(BufferedReader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();

        String tmp;
        while ((tmp = reader.readLine()) != null) {
            builder.append(tmp);
        }

        return builder.toString();
    }

    public static BufferedImage resizeToFit(final BufferedImage image, final int width, final int height) {
        final double scaleX = width / (double) image.getWidth();
        final double scaleY = height / (double) image.getHeight();
        final double scale = Math.min(scaleX, scaleY);

        final BufferedImage resizedImage = new BufferedImage((int) (image.getWidth() * scale), (int) (image.getHeight() * scale), BufferedImage.TYPE_INT_ARGB);

        final AffineTransform at = new AffineTransform() {{
            scale(scale, scale);
        }};

        final Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, at, null);

        return resizedImage;
    }
}
