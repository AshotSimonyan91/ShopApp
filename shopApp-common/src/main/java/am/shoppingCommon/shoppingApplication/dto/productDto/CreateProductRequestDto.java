package am.shoppingCommon.shoppingApplication.dto.productDto;

import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.imageDto.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductRequestDto {

    private String name;
    private String brand;
    private String productCode;
    private String description;
    private int count;
    private double price;
    private List<ImageDto> images;
    private List<CategoryDto> categories;
}
