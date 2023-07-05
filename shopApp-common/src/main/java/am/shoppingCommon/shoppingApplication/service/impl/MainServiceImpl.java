package am.shoppingCommon.shoppingApplication.service.impl;



import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductResponseDto;
import am.shoppingCommon.shoppingApplication.mapper.ProductMapper;
import am.shoppingCommon.shoppingApplication.service.MainService;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final ProductRepository productRepository;

    @Value("${shopping-app.upload.image.path}")
    private String imageUploadPath;

    @Override
    public byte[] getImage(String imageName) throws IOException {
        File file = new File(imageUploadPath + imageName);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            return IOUtils.toByteArray(fis);
        }
        return null;
    }

    @Override
    public List<CreateProductResponseDto> search(String value) {
        List<Product> byNameContainingIgnoreCase = productRepository.findByNameContainingIgnoreCase(value);
        return ProductMapper.mapToListDto(byNameContainingIgnoreCase);
    }
}