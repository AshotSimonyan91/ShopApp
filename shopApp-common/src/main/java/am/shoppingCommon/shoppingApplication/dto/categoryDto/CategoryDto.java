package am.shoppingCommon.shoppingApplication.dto.categoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Ashot Simonyan on 08.06.23.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private int id;
    private String name;
    private String parentCategory;
    private String image;
}
