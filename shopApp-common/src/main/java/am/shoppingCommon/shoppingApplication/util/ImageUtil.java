package am.shoppingCommon.shoppingApplication.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * This utility class provides static methods for handling image upload and resizing in a shopping application.
 * It offers functionalities to upload images from multipart files and save them to the specified image upload path.
 * Additionally, it provides a method to resize images while maintaining their aspect ratio and saving the resized
 * version to the specified image upload path. The class uses the ImageIO and BufferedImage classes from the
 * javax.imageio package for image handling and resizing.
 */
public class ImageUtil {

    /**
     * Uploads the provided MultipartFile as an image and saves it to the specified image upload path.
     *
     * @param multipartFile   The MultipartFile representing the image file to be uploaded.
     * @param imageUploadPath The path to the directory where the image should be saved.
     * @return The filename of the uploaded image.
     * @throws IOException If there's an issue with uploading or saving the image file.
     */
    public static String imageUpload(MultipartFile multipartFile, String imageUploadPath) throws IOException {
        String fileName = "";
        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);
            multipartFile.transferTo(file);
        }
        return fileName;
    }


    /**
     * Uploads the provided MultipartFile as an image, resizes it to the specified target width and height while
     * maintaining the aspect ratio, and saves the resized image to the specified image upload path.
     *
     * @param multipartFile   The MultipartFile representing the image file to be uploaded and resized.
     * @param imageUploadPath The path to the directory where the resized image should be saved.
     * @return The filename of the resized image.
     * @throws IOException If there's an issue with uploading, resizing, or saving the image file.
     */
    public static String imageUploadWithResize(MultipartFile multipartFile, String imageUploadPath) throws IOException {
        String fileName = "";
        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);

            resizeAndSaveImage(multipartFile, 1024, 768, file);
        }
        return fileName;
    }

    /**
     * Resizes the provided MultipartFile representing an image to the specified target width and height
     * while maintaining the aspect ratio, and saves the resized image to the specified output file.
     *
     * @param originalImage The MultipartFile representing the original image to be resized.
     * @param targetWidth   The desired width of the resized image.
     * @param targetHeight  The desired height of the resized image.
     * @param outputFile    The File object representing the output file where the resized image should be saved.
     * @throws IOException If there's an issue with reading, resizing, or saving the image file.
     */
    private static void resizeAndSaveImage(MultipartFile originalImage, int targetWidth, int targetHeight, File outputFile) throws IOException {
        BufferedImage img = ImageIO.read(originalImage.getInputStream());

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, img.getType());
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(img, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        String formatName = getFileFormat(originalImage.getOriginalFilename());

        ImageIO.write(resizedImage, formatName, outputFile);
    }

    /**
     * Extracts the file format (extension) from the provided filename.
     *
     * @param fileName The name of the file from which the format should be extracted.
     * @return The file format (extension) in lowercase, or "jpg" as the default if no extension is found.
     */
    private static String getFileFormat(String fileName) {
        String formatName = "jpg";

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            formatName = fileName.substring(dotIndex + 1).toLowerCase();
        }

        return formatName;
    }

}
