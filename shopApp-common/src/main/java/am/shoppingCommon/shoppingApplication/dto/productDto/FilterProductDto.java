package am.shoppingCommon.shoppingApplication.dto.productDto;

import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterProductDto {
    private String name;
    private String brand;
    private String productCode;
    private double minPrice;
    private double maxPrice;
    private List<CategoryDto> categories;
    private String serialNumber;
    private String sortBy;
    private String sortDirection;
}
