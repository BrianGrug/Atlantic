package com.doctordark.util.imagemessage;

import java.awt.*;
import java.awt.Color;

import com.google.common.base.*;

import org.bukkit.*;

import java.net.*;

import javax.imageio.*;

import java.io.*;
import java.awt.geom.*;
import java.awt.image.*;

import org.bukkit.craftbukkit.libs.joptsimple.internal.*;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.*;

import org.bukkit.entity.*;

public final class ImageMessage {
    private static final char TRANSPARENT_CHAR = ' ';
    private final String[] lines;
    private static final Color[] colors;

    private ImageMessage(final String... lines) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object) lines, (Object) "Lines cannot be null");
        this.lines = lines;
    }

    private ImageMessage(final BufferedImage image, final int height, final char imageCharacter) throws IllegalArgumentException {
        this(toImageMessage(toColourArray(image, height), imageCharacter));
    }

    public static ImageMessage newInstance(final BufferedImage image, final int height, final char imageCharacter) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object) image, (Object) "Image cannot be null");
        Preconditions.checkArgument(height >= 0, (Object) "Height must be positive");
        return new ImageMessage(image, height, imageCharacter);
    }

    public static ImageMessage newInstance(final ChatColor[][] chatColors, final char imageCharacter) {
        return new ImageMessage(toImageMessage(chatColors, imageCharacter));
    }

    public static ImageMessage newInstance(final String url, final int height, final char imageCharacter) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object) url, (Object) "Image URL cannot be null");
        Preconditions.checkArgument(height >= 0, (Object) "Height must be positive");
        try {
            return newInstance(ImageIO.read(new URL(url)), height, imageCharacter);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static ImageMessage newInstance(final String fileName, final File folder, final int height, final char imageCharacter) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object) fileName, (Object) "File name cannot be null");
        Preconditions.checkNotNull((Object) folder, (Object) "Folder cannot be null");
        try {
            return newInstance(ImageIO.read(new File(folder, fileName)), height, imageCharacter);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public ImageMessage appendText(final String... text) {
        for (int i = 0; i < Math.min(text.length, this.lines.length); ++i) {
            final StringBuilder sb = new StringBuilder();
            final String[] lines = this.lines;
            final int n = i;
            lines[n] = sb.append(lines[n]).append(' ').append(text[i]).toString();
        }
        return this;
    }

    public ImageMessage appendCenteredText(final String... text) {
        for (int i = 0; i < Math.min(text.length, this.lines.length); ++i) {
            final String line = this.lines[i];
            this.lines[i] = line + this.center(text[i], 65 - line.length());
        }
        return this;
    }

    private static ChatColor[][] toColourArray(final BufferedImage image, final int height) {
        final double ratio = image.getHeight() / image.getWidth();
        final BufferedImage resizedImage = resizeImage(image, (int) (height / ratio), height);
        final ChatColor[][] chatImage = new ChatColor[resizedImage.getWidth()][resizedImage.getHeight()];
        for (int x = 0; x < resizedImage.getWidth(); ++x) {
            for (int y = 0; y < resizedImage.getHeight(); ++y) {
                final ChatColor closest = getClosestChatColor(new Color(resizedImage.getRGB(x, y), true));
                chatImage[x][y] = closest;
            }
        }
        return chatImage;
    }

    private static String[] toImageMessage(final ChatColor[][] colors, final char imageCharacter) {
        final String[] results = new String[colors[0].length];
        for (int i = 0; i < colors[0].length; ++i) {
            final StringBuilder line = new StringBuilder();
            for (final ChatColor[] color : colors) {
                final ChatColor current = color[i];
                line.append((current != null) ? (current.toString() + imageCharacter) : ImageMessage.TRANSPARENT_CHAR);
            }
            results[i] = line.toString() + ChatColor.RESET;
        }
        return results;
    }

    private static BufferedImage resizeImage(final BufferedImage image, final int width, final int height) {
        final AffineTransform transform = new AffineTransform();
        transform.scale(width / image.getWidth(), height / image.getHeight());
        return new AffineTransformOp(transform, 1).filter(image, null);
    }

    private static double getDistance(final Color c1, final Color c2) {
        final int red = c1.getRed() - c2.getRed();
        final int green = c1.getGreen() - c2.getGreen();
        final int blue = c1.getBlue() - c2.getBlue();
        final double redMean = (c1.getRed() + c2.getRed()) / 2.0;
        final double weightRed = 2.0 + redMean / 256.0;
        final double weightGreen = 4.0;
        final double weightBlue = 2.0 + (255.0 - redMean) / 256.0;
        return weightRed * red * red + weightGreen * green * green + weightBlue * blue * blue;
    }

    private static boolean areIdentical(final Color c1, final Color c2) {
        return Math.abs(c1.getRed() - c2.getRed()) <= 5 && Math.abs(c1.getGreen() - c2.getGreen()) <= 5 && Math.abs(c1.getBlue() - c2.getBlue()) <= 5;
    }

    private static ChatColor getClosestChatColor(final Color color) {
        if (color.getAlpha() < 128) {
            return null;
        }
        for (int i = 0; i < ImageMessage.colors.length; ++i) {
            if (areIdentical(ImageMessage.colors[i], color)) {
                return ChatColor.values()[i];
            }
        }
        int index = 0;
        double best = -1.0;
        for (int j = 0; j < ImageMessage.colors.length; ++j) {
            final double distance = getDistance(color, ImageMessage.colors[j]);
            if (distance < best || best == -1.0) {
                best = distance;
                index = j;
            }
        }
        return ChatColor.values()[index];
    }

    private String center(final String string, final int length) {
        if (string.length() > length) {
            return string.substring(0, length);
        }
        if (string.length() == length) {
            return string;
        }
        return Strings.repeat(' ', (length - string.length()) / 2) + string;
    }

    public String[] getLines() {
        return Arrays.copyOf(this.lines, this.lines.length);
    }

    public void sendToPlayer(final Player player) {
        player.sendMessage(this.lines);
    }

    static {
        colors = new Color[] { new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0),
                new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255),
                new Color(255, 255, 85), new Color(255, 255, 255) };
    }
}
