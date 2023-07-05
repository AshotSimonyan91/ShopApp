package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.imageDto.ImageDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductResponseDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.entity.Category;
import am.shoppingCommon.shoppingApplication.entity.Image;
import am.shoppingCommon.shoppingApplication.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductMapper {
    public static Product map(CreateProductRequestDto dto) {
        if (dto == null) {
            return null;
        }
        List<CategoryDto> categories = dto.getCategories();
        List<Category> categoriesList = new ArrayList<>();
        for (CategoryDto category : categories) {
            categoriesList.add(CategoryMapper.dtoToCategory(category));
        }
        return Product.builder()
                .name(dto.getName())
                .productCode(dto.getProductCode())
                .description(dto.getDescription())
                .count(dto.getCount())
                .price(dto.getPrice())
                .categories(new ArrayList<Category>(categoriesList))
                .build();
    }

    public static Product map(CreateProductResponseDto dto) {
        if (dto == null) {
            return null;
        }
        List<CategoryDto> categories = dto.getCategories();
        List<Category> categoriesList = new ArrayList<>();
        for (CategoryDto category : categories) {
            categoriesList.add(CategoryMapper.dtoToCategory(category));
        }
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .productCode(dto.getProductCode())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .categories(new ArrayList<Category>(categoriesList))
                .build();
    }

    public static CreateProductResponseDto mapToResponseDto(Product entity) {
        if (entity == null) {
            return null;
        }
        List<Image> list = entity.getImages();
        List<ImageDto> imageDtos = new ArrayList<>();
        for (Image image : list) {
            imageDtos.add(ImageMapper.imageToImageDto(image));
        }
        List<Category> list1 = entity.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : list1) {
            categoryDtos.add(CategoryMapper.categoryToDto(category));
        }
        return CreateProductResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .productCode(entity.getProductCode())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .images(imageDtos)
                .categories(categoryDtos)
                .build();
    }

    public static CreateProductRequestDto mapToRequestDto(Product entity) {
        if (entity == null) {
            return null;
        }
        List<Image> list = entity.getImages();
        List<ImageDto> imageDtos = new ArrayList<>();
        for (Image image : list) {
            imageDtos.add(ImageMapper.imageToImageDto(image));
        }
        List<Category> list1 = entity.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : list1) {
            categoryDtos.add(CategoryMapper.categoryToDto(category));
        }
        return CreateProductRequestDto.builder()
                .name(entity.getName())
                .productCode(entity.getProductCode())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .images(imageDtos)
                .categories(categoryDtos)
                .build();
    }

    public static Page<ProductDto> mapPageToDto(Page<Product> productPage) {
        if (productPage == null) {
            return null;
        }
        List<ProductDto> productDtoList = productPage.getContent()
                .stream()
                .map(ProductMapper::mapToDto)
                .toList();

        return new PageImpl<>(productDtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    public static ProductDto mapToDto(Product entity) {
        if (entity == null) {
            return null;
        }
        List<Image> list = entity.getImages();
        List<ImageDto> imageDtos = new ArrayList<>();
        if (list != null) {
            for (Image image : list) {
                imageDtos.add(ImageMapper.imageToImageDto(image));
            }
        }
        List<Category> list1 = entity.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : list1) {
            categoryDtos.add(CategoryMapper.categoryToDto(category));
        }
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .productCode(entity.getProductCode())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .count(entity.getCount())
                .user(UserMapper.userToUserDto(entity.getUser()))
                .images(imageDtos)
                .categories(categoryDtos)
                .build();
    }

    public static Set<CreateProductResponseDto> mapToDto(Set<Product> entity) {
        if (entity == null) {
            return null;
        }
        return entity.stream()
                .map(ProductMapper::mapToResponseDto)
                .collect(Collectors.toSet());
    }

    public static List<CreateProductResponseDto> mapToListDto(List<Product> entity) {
        if (entity == null) {
            return null;
        }
        return entity.stream()
                .map(ProductMapper::mapToResponseDto)
                .toList();
    }

    public static List<ProductDto> mapToListProductDto(List<Product> entity) {
        if (entity == null) {
            return null;
        }
        return entity.stream()
                .map(ProductMapper::mapToDto)
                .toList();
    }
}

