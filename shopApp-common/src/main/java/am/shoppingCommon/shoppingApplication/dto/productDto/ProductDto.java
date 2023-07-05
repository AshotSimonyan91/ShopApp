package am.shoppingCommon.shoppingApplication.dto.productDto;


import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.imageDto.ImageDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private int id;
    private String name;
    private String productCode;
    private String description;
    private double price;
    private int count;
    private UserDto user;
    private List<ImageDto> images;
    private List<CategoryDto> categories;
}
