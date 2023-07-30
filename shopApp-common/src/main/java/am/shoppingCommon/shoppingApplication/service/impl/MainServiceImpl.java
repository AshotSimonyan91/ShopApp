package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.mapper.ProductMapper;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Service implementation class that provides various functionalities related to products and images.
 * It implements the MainService interface and is responsible for retrieving product images and performing product searches.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceImpl implements MainService {
    private final ProductRepository productRepository;

    @Value("${shopping-app.upload.image.path}")
    private String imageUploadPath;

    /**
     * Retrieves the image data corresponding to the given image name.
     *
     * @param imageName The name of the image file to be retrieved.
     * @return byte array representing the image data if the image file exists; otherwise, it returns null.
     * @throws IOException if there is an error while reading the image file.
     */
    @Override
    public byte[] getImage(String imageName) throws IOException {
        if (imageName != null && !imageName.equals("")) {
            File file = new File(imageUploadPath + imageName);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                log.info("Image by {} name was opened",imageName);
                return IOUtils.toByteArray(fis);
            }
        }
        log.info("Image did not open");
        return null;
    }

    /**
     * Searches for products containing the provided value in their names.
     *
     * @param value The search value to be used for product search.
     * @return List of CreateProductResponseDto representing the search results.
     */
    @Override
    public List<CreateProductResponseDto> search(String value) {
        List<Product> byNameContainingIgnoreCase = productRepository.findByNameContainingIgnoreCase(value);
        log.info("Found products by {} value",value);
        return ProductMapper.mapToListDto(byNameContainingIgnoreCase);
    }
}