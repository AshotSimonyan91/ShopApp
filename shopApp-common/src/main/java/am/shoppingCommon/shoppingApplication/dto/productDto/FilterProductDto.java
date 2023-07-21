package am.shoppingCommon.shoppingApplication.dto.productDto;

import am.shoppingCommon.shoppingApplication.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterProductDto {
    private String brand;
    private String productCode;
    private double minPrice;
    private double maxPrice;
    private Category category;
    private String sortBy;
    private String sortDirection;
}
