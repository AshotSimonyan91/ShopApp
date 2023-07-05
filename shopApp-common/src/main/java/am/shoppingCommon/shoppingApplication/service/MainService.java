package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductResponseDto;

import java.io.IOException;
import java.util.List;

public interface MainService {
    byte[] getImage(String imageName) throws IOException;

    List<CreateProductResponseDto> search(String value);
}
