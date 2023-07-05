package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.imageDto.ImageDto;
import am.shoppingCommon.shoppingApplication.entity.Image;

/**
 * Created by Ashot Simonyan on 11.06.23.
 */

public class ImageMapper {

    public static Image imageDtoToImage(ImageDto imageDto) {
        if (imageDto == null) {
            return null;
        }
        return Image.builder()
                .id(imageDto.getId())
                .image(imageDto.getImage())
                .build();
    }

    public static ImageDto imageToImageDto(Image image) {
        if (image == null) {
            return null;
        }
        return ImageDto
                .builder()
                .id(image.getId())
                .image(image.getImage())
                .build();
    }
}
