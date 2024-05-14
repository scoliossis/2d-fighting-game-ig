package scoliosis.Libs;

import scoliosis.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static scoliosis.Main.resourcesFile;
import static scoliosis.mainjframe.mainframe;

public class RenderLib {

    public static int numberOfImages = 0;
    public static BufferedImage[] allimages = new BufferedImage[]{};
    public static String[] ImageNames;
    public static ArrayList growing = new ArrayList();

    public static void drawRect(int x, int y, int width, int height, Color color, Graphics g) {
        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        height = (int) (height / 270f * (float) (mainframe.getHeight()));
        width = (int) (width / 480f * (float) (mainframe.getWidth()));

        g.setColor(color);
        //g.drawRect(x, y, width, height);
        g.fillPolygon(new int[]{x, x + width, x + width, x}, new int[]{y, y, y + height, y + height}, 4);
    }

    public static void drawCenteredRect(int x, int y, int width, int height, Color color, Graphics g) {
        x -= width / 2;
        y -= height / 2;

        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        height = (int) (height / 270f * (float) (mainframe.getHeight()));
        width = (int) (width / 480f * (float) (mainframe.getWidth()));

        g.setColor(color);
        //g.drawRect(x, y, width, height);
        g.fillPolygon(new int[]{x, x + width, x + width, x}, new int[]{y, y, y + height, y + height}, 4);
    }

    public static void drawCenteredRoundRect(int x, int y, int width, int height, int aW, int aH, Color color, Graphics g) {
        x -= width / 2;
        y -= height / 2;

        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        height = (int) (height / 270f * (float) (mainframe.getHeight()));
        width = (int) (width / 480f * (float) (mainframe.getWidth()));

        g.setColor(color);
        g.fillRoundRect(x, y, width, height, aW, aH);
    }

    public static void drawRoundRect(int x, int y, int width, int height, int aW, int aH, Color color, Graphics g) {
        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        height = (int) (height / 270f * (float) (mainframe.getHeight()));
        width = (int) (width / 480f * (float) (mainframe.getWidth()));

        g.setColor(color);
        g.fillRoundRect(x, y, width, height, aW, aH);
    }

    public static void drawOutline(int x, int y, int width, int height, Color color, Graphics g) {
        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        height = (int) (height / 270f * (float) (mainframe.getHeight()));
        width = (int) (width / 480f * (float) (mainframe.getWidth()));

        g.setColor(color);
        g.drawRect(x, y, width, height);
    }

    public static void drawCircle(int x, int y, int width, int height, Color color, Graphics g) {
        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        width = (int) (width / 480f * (float) (mainframe.getWidth()));
        height = (int) (height / 270f * (float) (mainframe.getHeight()));

        g.setColor(color);
        g.fillOval(x, y, width, height);
    }

    public static void drawCircleOutline(int x, int y, int width, int height, Color color, Graphics g) {
        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        width = (int) (width / 480f * (float) (mainframe.getWidth()));
        height = (int) (height / 270f * (float) (mainframe.getHeight()));

        g.setColor(color);
        g.drawOval(x, y, width, height);
    }

    public static void drawCircleRealLocation(int x, int y, int width, int height, Color color, Graphics g) {
        width = (int) (width / 480f * (float) (mainframe.getWidth()));
        height = (int) (height / 270f * (float) (mainframe.getHeight()));

        g.setColor(color);
        g.fillOval(x, y, width, height);
    }

    public static void drawCircleOutlineRealLocation(int x, int y, int width, int height, Color color, Graphics g) {
        width = (int) (width / 480f * (float) (mainframe.getWidth()));
        height = (int) (height / 270f * (float) (mainframe.getHeight()));

        g.setColor(color);
        g.drawOval(x, y, width, height);
    }


    public static Font getFont(Font font) {
        return new Font(font.getName(), font.getStyle(), (int) (font.getSize() / 480f * (float) (mainframe.getWidth())));
    }

    public static void drawString(Graphics graphics, String text, int x, int y, int fontsize, String fontname, int style, Color color) {
        Font font = new Font(fontname, style, (int) (fontsize / 480f * (float) (mainframe.getWidth())));

        graphics.setFont(font);
        graphics.setColor(color);

        graphics.drawString(text, (int) (x / 480f * (float) (mainframe.getWidth())), (int) (y / 270f * (float) (mainframe.getHeight())));
    }

    public static void drawString(String text, int x, int y, Color color, Graphics g) {
        g.setColor(color);
        g.drawString(text, (int) (x / 480f * (float) (mainframe.getWidth())), (int) (y / 270f * (float) (mainframe.getHeight())));
    }

    public static void drawCenteredString(Graphics graphics, String text, int x, int y, int fontsize, String fontname, int style, Color color) {
        Font font = new Font(fontname, style, (int) (fontsize / 480f * (float) (mainframe.getWidth())));
        x = (int) (x / 480f * (float) (mainframe.getWidth()));

        graphics.setFont(font);
        graphics.setColor(color);
        x -= graphics.getFontMetrics().stringWidth(text) / 2;

        graphics.drawString(text, x, (int) (y / 270f * (float) (mainframe.getHeight())));
    }

    public static int getStringWidth(String text, Font font, Graphics graphics, boolean scale) {

        if (scale)
            graphics.setFont(new Font(font.getName(), font.getStyle(), (int) (font.getSize() / 480f * (float) (mainframe.getWidth()))));
        else graphics.setFont(new Font(font.getName(), font.getStyle(), font.getSize()));

        return graphics.getFontMetrics().stringWidth(text);
    }

    public static void drawLine(int x, int y, int x2, int y2, Color color, Graphics g) {
        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        x2 = (int) (x2 / 480f * (float) (mainframe.getWidth()));
        y2 = (int) (y2 / 270f * (float) (mainframe.getHeight()));

        g.setColor(color);
        g.drawLine(x, y, x2, y2);
    }

    public static void drawPoligon(int[] x, int[] y, Color color, Graphics g) {

        g.setColor(color);

        for (int i = 0; i < x.length; i++) {
            x[i] = (int) (x[i] / 480f * (float) (mainframe.getWidth()));
            y[i] = (int) (y[i] / 270f * (float) (mainframe.getHeight()));
        }

        g.fillPolygon(x, y, x.length);

    }

    public static void drawCenteredImageNoY(int x, int y, int width, int height, BufferedImage bi) {
        x -= width / 2;

        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        height = (int) (height / 270f * (float) (mainframe.getHeight())) + 1;
        width = (int) (width / 480f * (float) (mainframe.getWidth())) + 1;

        Game.g.drawImage(bi, x, y, width, height, null);
    }

    public static void drawCenteredImage(int x, int y, int width, int height, BufferedImage bi) {
        x -= width / 2;
        y -= height / 2;

        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        height = (int) (height / 270f * (float) (mainframe.getHeight())) + 1;
        width = (int) (width / 480f * (float) (mainframe.getWidth())) + 1;

        Game.g.drawImage(bi, x, y, width, height, null);
    }

    public static float growEffectExponential(String growName, float growSize, float growTime) {
        if (!growing.contains(growName)) {
            growing.add(growName);
            growing.add(System.currentTimeMillis());
        }

        for (int i = 0; i < growing.size(); i += 2) {
            if (growing.get(i).equals(growName)) {
                long growStartTime = (long) growing.get(i + 1);

                float grownAmount = ((System.currentTimeMillis() - growStartTime) / (growTime));
                float percent = (Math.min(grownAmount, growSize) / growSize);

                grownAmount *= percent * percent;

                return Math.min((grownAmount) * growSize, growSize);
            }
        }

        return 0;
    }

    public static float growEffect(String growName, float growSize, float growTime) {
        if (!growing.contains(growName)) {
            growing.add(growName);
            growing.add(System.currentTimeMillis());
        }

        for (int i = 0; i < growing.size(); i += 2) {
            if (growing.get(i).equals(growName)) {
                long growStartTime = (long) growing.get(i + 1);

                float grownAmount = ((System.currentTimeMillis() - growStartTime) / growTime);

                return Math.min((grownAmount) * growSize, growSize);
            }
        }

        return 0;
    }

    public static void resetGrow(String growName) {
        if (growing.contains(growName)) {
            int index = growing.indexOf(growName);
            growing.remove(index);
            growing.remove(index);
        }
    }

    public static void drawImage(float x, float y, int width, int height, BufferedImage bi) {
        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));

        height = (int) (height / 270f * (float) (mainframe.getHeight())) + 1;
        width = (int) (width / 480f * (float) (mainframe.getWidth())) + 1;

        Game.g.drawImage(bi, (int) x, (int) y, width, height, null);
    }

    public static BufferedImage splitBufferedImage(int x, int y, int w, int h, BufferedImage image) {
        BufferedImage image2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int xc = 0; xc < w; xc++) {
            for (int yc = 0; yc < h; yc++) {
                image2.setRGB(xc, yc, image.getRGB(xc + x, yc + y));
            }
        }
        return image2;
    }

    public static BufferedImage getBufferedImage(String h) {
        if (allimages.length > 0) {
            BufferedImage image2 = allimages[0];

            for (int i = 0; i < numberOfImages; i++) {
                if (ImageNames[i] != null && ImageNames[i].replace(".png", "").replace(" ", "").equalsIgnoreCase(h)) {
                    image2 = allimages[i];
                }
            }

            return image2;
        }
        return new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
    }

    public static BufferedImage getCroppedImage(BufferedImage image, int x, int maxW) {
        float widthMulti = (float) image.getWidth() / maxW;

        x *= widthMulti;

        return image.getSubimage(x, 0, image.getWidth() - x, image.getHeight());
    }

    public static BufferedImage BlurBufferedImage(BufferedImage image) {
        int radius = 11;
        int size = radius * 2 + 1;
        float weight = 1.0f / (size * size);
        float[] data = new float[size * size];

        Arrays.fill(data, weight);

        Kernel kernel = new Kernel(size, size, data);
        ConvolveOp filter = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
        BufferedImage BlurredImage = filter.filter(image, null);


        return BlurredImage.getSubimage(12, 12, image.getWidth() - 12 - (image.getWidth() / 10), image.getHeight() - 12 - (image.getHeight() / 10));
    }


    public static BufferedImage BlurBufferedImage(BufferedImage image, int radius) {
        int size = radius * 2 + 1;
        float weight = 1.0f / (size * size);
        float[] data = new float[size * size];

        Arrays.fill(data, weight);

        Kernel kernel = new Kernel(size, size, data);
        ConvolveOp filter = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
        BufferedImage BlurredImage = filter.filter(image, null);


        return BlurredImage.getSubimage(12, 12, image.getWidth() - 12 - (image.getWidth() / 10), image.getHeight() - 12 - (image.getHeight() / 10));
    }


    public static void loadDirImages(File fileDir) {
        for (File file : Objects.requireNonNull(fileDir.listFiles())) {
            if (file.isDirectory()) {
                loadDirImages(file);
            }
            if (file.getName().endsWith(".png")) {
                numberOfImages++;
            }
        }
    }

    static int imagesLoaded = 0;

    public static void setDirImages(File fileDir) throws IOException {
        for (File file : Objects.requireNonNull(fileDir.listFiles())) {
            if (file.isDirectory()) {
                setDirImages(file);
            }
            if (file.getName().endsWith(".png")) {
                allimages[imagesLoaded] = ImageIO.read(Game.getFile(fileDir + "\\" + file.getName()));
                ImageNames[imagesLoaded] = file.getPath().replace(resourcesFile, "").substring(1);
                imagesLoaded++;
            }
        }
    }


    public static void loadImages() {
        try {
            if (!Game.getFile(resourcesFile).isFile()) {
                Game.getFile(resourcesFile).mkdirs();
            }

            for (File file : Objects.requireNonNull(Paths.get(resourcesFile).toFile().listFiles())) {
                if (file.isDirectory()) {
                    loadDirImages(file);
                }
                if (file.getName().endsWith(".png")) {
                    numberOfImages++;
                }
            }
            allimages = new BufferedImage[numberOfImages];
            ImageNames = new String[numberOfImages];

            for (File file : Objects.requireNonNull(Game.getFile(resourcesFile).listFiles())) {
                if (file.isDirectory()) {
                    setDirImages(file);
                }
                if (file.getName().endsWith(".png")) {
                    allimages[imagesLoaded] = ImageIO.read(Game.getFile(resourcesFile + "\\" + file.getName()));
                    ImageNames[imagesLoaded] = file.getPath().replace(resourcesFile, "").substring(1);
                    imagesLoaded++;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}