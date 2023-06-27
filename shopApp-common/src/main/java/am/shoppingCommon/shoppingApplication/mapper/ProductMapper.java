package am.shoppingCommon.shoppingApplication.mapper;



import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.imageDto.ImageDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductResponseDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.entity.Category;
import am.shoppingCommon.shoppingApplication.entity.Image;
import am.shoppingCommon.shoppingApplication.entity.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductMapper {
    public static Product map(CreateProductRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();

        product.setName(dto.getName());
        product.setProductCode(dto.getProductCode());
        product.setDescription(dto.getDescription());
        product.setCount(dto.getCount());
        product.setPrice(dto.getPrice());
        List<CategoryDto> categories = dto.getCategories();
        List<Category> categoriesList = new ArrayList<>();
        for (CategoryDto category : categories) {
            categoriesList.add(CategoryMapper.dtoToCategory(category));
        }
        if (categoriesList != null) {
            product.setCategories(new ArrayList<Category>(categoriesList));
        }

        return product;
    }

    public static Product map(CreateProductResponseDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();

        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setProductCode(dto.getProductCode());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        List<CategoryDto> categories = dto.getCategories();
        List<Category> categoriesList = new ArrayList<>();
        for (CategoryDto category : categories) {
            categoriesList.add(CategoryMapper.dtoToCategory(category));
        }
        if (categoriesList != null) {
            product.setCategories(new ArrayList<Category>(categoriesList));
        }

        return product;
    }

    public static CreateProductResponseDto mapToResponseDto(Product entity) {
        if (entity == null) {
            return null;
        }

        CreateProductResponseDto createProductResponseDto = new CreateProductResponseDto();

        createProductResponseDto.setId(entity.getId());
        createProductResponseDto.setName(entity.getName());
        createProductResponseDto.setProductCode(entity.getProductCode());
        createProductResponseDto.setDescription(entity.getDescription());
        createProductResponseDto.setPrice(entity.getPrice());
        List<Image> list = entity.getImages();
        List<ImageDto> imageDtos = new ArrayList<>();
        for (Image image : list) {
            imageDtos.add(ImageMapper.imageToImageDto(image));
        }
        if (imageDtos != null) {
            createProductResponseDto.setImages(imageDtos);
        }
        List<Category> list1 = entity.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : list1) {
            categoryDtos.add(CategoryMapper.categoryToDto(category));
        }
        if (categoryDtos != null) {
            createProductResponseDto.setCategories(categoryDtos);
        }

        return createProductResponseDto;
    }

    public static CreateProductRequestDto mapToRequestDto(Product entity) {
        if (entity == null) {
            return null;
        }

        CreateProductRequestDto createProductRequestDto = new CreateProductRequestDto();

        createProductRequestDto.setName(entity.getName());
        createProductRequestDto.setProductCode(entity.getProductCode());
        createProductRequestDto.setDescription(entity.getDescription());
        createProductRequestDto.setPrice(entity.getPrice());
        List<Image> list = entity.getImages();
        List<ImageDto> imageDtos = new ArrayList<>();
        for (Image image : list) {
            imageDtos.add(ImageMapper.imageToImageDto(image));
        }
        if (imageDtos != null) {
            createProductRequestDto.setImages(imageDtos);
        }
        List<Category> list1 = entity.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : list1) {
            categoryDtos.add(CategoryMapper.categoryToDto(category));
        }
        if (categoryDtos != null) {
            createProductRequestDto.setCategories(categoryDtos);
        }
        return createProductRequestDto;
    }

    public static ProductDto mapToDto(Product entity) {
        if (entity == null) {
            return null;
        }

        ProductDto productDto = new ProductDto();

        productDto.setId(entity.getId());
        productDto.setName(entity.getName());
        productDto.setProductCode(entity.getProductCode());
        productDto.setDescription(entity.getDescription());
        productDto.setPrice(entity.getPrice());
        List<Image> list = entity.getImages();
        List<ImageDto> imageDtos = new ArrayList<>();
        for (Image image : list) {
            imageDtos.add(ImageMapper.imageToImageDto(image));
        }
        if (imageDtos != null) {
            productDto.setImages(imageDtos);
        }
        List<Category> list1 = entity.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : list1) {
            categoryDtos.add(CategoryMapper.categoryToDto(category));
        }
        if (categoryDtos != null) {
            productDto.setCategories(categoryDtos);
        }

        return productDto;
    }

    public static Set<CreateProductResponseDto> mapToDto(Set<Product> entity) {
        if (entity == null) {
            return null;
        }
        Set<CreateProductResponseDto> productResponseDtoSet = new HashSet<>();
        for (Product product : entity) {
            CreateProductResponseDto productResponseDto = new CreateProductResponseDto();
            productResponseDto.setId(product.getId());
            productResponseDto.setName(product.getName());
            productResponseDto.setProductCode(product.getProductCode());
            productResponseDto.setDescription(product.getDescription());
            productResponseDto.setPrice(product.getPrice());
            List<Image> list = product.getImages();
            List<ImageDto> imageDtos = new ArrayList<>();
            for (Image image : list) {
                imageDtos.add(ImageMapper.imageToImageDto(image));
            }
            if (imageDtos != null) {
                productResponseDto.setImages(imageDtos);
            }
            List<Category> list1 = product.getCategories();
            List<CategoryDto> categoryDtos = new ArrayList<>();
            for (Category category : list1) {
                categoryDtos.add(CategoryMapper.categoryToDto(category));
            }
            if (categoryDtos != null) {
                productResponseDto.setCategories(categoryDtos);
            }

            productResponseDtoSet.add(productResponseDto);
        }
        return productResponseDtoSet;
    }

    public static List<CreateProductResponseDto> mapToListDto(List<Product> entity) {
        if (entity == null) {
            return null;
        }
        List<CreateProductResponseDto> productResponseDtoList = new ArrayList<>();
        for (Product product : entity) {
            CreateProductResponseDto productResponseDto = new CreateProductResponseDto();
            productResponseDto.setId(product.getId());
            productResponseDto.setName(product.getName());
            productResponseDto.setProductCode(product.getProductCode());
            productResponseDto.setDescription(product.getDescription());
            productResponseDto.setPrice(product.getPrice());
            List<Image> list = product.getImages();
            List<ImageDto> imageDtos = new ArrayList<>();
            for (Image image : list) {
                imageDtos.add(ImageMapper.imageToImageDto(image));
            }
            if (imageDtos != null) {
                productResponseDto.setImages(imageDtos);
            }
            List<Category> list1 = product.getCategories();
            List<CategoryDto> categoryDtos = new ArrayList<>();
            for (Category category : list1) {
                categoryDtos.add(CategoryMapper.categoryToDto(category));
            }
            if (categoryDtos != null) {
                productResponseDto.setCategories(categoryDtos);
            }

            productResponseDtoList.add(productResponseDto);
        }
        return productResponseDtoList;
    }
}

