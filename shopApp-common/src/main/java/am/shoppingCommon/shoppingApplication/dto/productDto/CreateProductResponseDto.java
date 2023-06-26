package am.shoppingCommon.shoppingApplication.dto.productDto;


import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.imageDto.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductResponseDto {

    private int id;
    private String name;
    private String productCode;
    private String description;
    private double price;
    private List<ImageDto> images;
    private List<CategoryDto> categories;
}
