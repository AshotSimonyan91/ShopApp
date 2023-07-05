package am.shopappRest.shoppingApplicationRest.restDto.mainRequestDto;

import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchPaginationDto {

    private String value;
    private Page<ProductDto> result;
    private int page;
    private int totalPages;
    private List<Integer> pageNumbers;
}
