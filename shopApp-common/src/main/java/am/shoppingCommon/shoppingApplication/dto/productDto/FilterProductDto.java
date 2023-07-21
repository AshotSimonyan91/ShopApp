package am.shoppingCommon.shoppingApplication.dto.productDto;

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
//    private List<Integer> categoryIds;
    private String sortBy;
    private String sortDirection;
}
