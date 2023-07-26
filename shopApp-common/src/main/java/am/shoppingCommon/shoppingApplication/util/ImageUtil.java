package am.shoppingCommon.shoppingApplication.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageUtil {

    public static String imageUpload(MultipartFile multipartFile, String imageUploadPath) throws IOException {
        String fileName = "";
        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);
            multipartFile.transferTo(file);
        }
        return fileName;
    }


    public static String imageUploadWithResize(MultipartFile multipartFile, String imageUploadPath) throws IOException {
        String fileName = "";
        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);

            // Resize and save the image
            resizeAndSaveImage(multipartFile, 1280, 720, file);
        }
        return fileName;
    }

    private static void resizeAndSaveImage(MultipartFile originalImage, int targetWidth, int targetHeight, File outputFile) throws IOException {
        BufferedImage img = ImageIO.read(originalImage.getInputStream());

        // Create a resized image with the specified dimensions
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, img.getType());
        Graphics2D g2d = resizedImage.createGraphics();

        // Use bicubic interpolation for better image quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(img, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        // Determine the file format
        String formatName = getFileFormat(originalImage.getOriginalFilename());

        // Save the resized image to the file
        ImageIO.write(resizedImage, formatName, outputFile);
    }

    private static String getFileFormat(String fileName) {
        String formatName = "jpg"; // Default to jpg format

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            formatName = fileName.substring(dotIndex + 1).toLowerCase();
        }

        return formatName;
    }

}
