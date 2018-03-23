package myapps.solutions.huddil.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import sun.misc.BASE64Encoder;

public class ImageScaler {

    /**
     * Scale an image while preserving aspect ratio
     */
    public static void main(String[] args) throws Exception {
        // scaleImage("d:/image2.png", 265, 144);
        System.out.println(encodeFileToBase64Binary(new File("d:/image2.png"), 256, 144));
    }

    /**
     * 
     * Scale an image while preserving aspect ratio
     * 
     * @param image
     *            The image to be scaled
     * @param newWidth
     *            The required width
     * @param newHeight
     *            The required width
     * 
     * @return The scaled image
     * @throws IOException
     */
    public static BufferedImage scaleImage(File file, int newWidth, int newHeight) throws IOException {
        BufferedImage image = ImageIO.read(file);
        int imageType = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        double thumbRatio = (double) newWidth / (double) newHeight;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double aspectRatio = (double) imageWidth / (double) imageHeight;

        if (thumbRatio < aspectRatio) {
            newHeight = (int) (newWidth / aspectRatio);
        } else {
            newWidth = (int) (newHeight * aspectRatio);
        }

        // Draw the scaled image
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // ImageIO.write(newImage, "PNG", file);
        return newImage;
    }

	public static String encodeFileToBase64Binary(File file, int newWidth, int newHeight) throws IOException {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(scaleImage(file, newWidth, newHeight), "PNG", bos);
        byte[] imageBytes = bos.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        imageString = encoder.encode(imageBytes);
        bos.close();
        return imageString.replaceAll("\r", "").replace("\n", "");
    }
}