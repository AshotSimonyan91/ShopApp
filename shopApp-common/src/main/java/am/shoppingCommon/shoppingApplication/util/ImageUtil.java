package am.shoppingCommon.shoppingApplication.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


public class ImageUtil {
    public static String imageUpload(MultipartFile multipartFile, String imageUploadPath) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);
            multipartFile.transferTo(file);
            return fileName;
        }
        return null;
    }
}
