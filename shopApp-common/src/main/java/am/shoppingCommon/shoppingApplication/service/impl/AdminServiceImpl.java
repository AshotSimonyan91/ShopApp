package am.shoppingCommon.shoppingApplication.service.impl;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.mapper.CategoryMapper;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
import am.shoppingCommon.shoppingApplication.mapper.ProductMapper;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.repository.DeliveryRepository;
import am.shoppingCommon.shoppingApplication.repository.OrderRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.AdminService;
import am.shoppingCommon.shoppingApplication.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides the implementation for the AdminService interface, offering functionalities
 * specific to administrators in a shopping application. It manages user blocking/unblocking, order editing,
 * user profile updates, and product editing with image uploads. The class utilizes repositories to interact
 * with the underlying data storage and a custom ImageUtil for handling image uploads and resizing.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final ProductRepository productRepository;

    @Value("${shopping-app.upload.image.path}")
    private String imageUploadPath;

    /**
     * Blocks the user with the given ID by the current admin user. Only an admin user can perform this action.
     *
     * @param id          The unique identifier of the user to be blocked.
     * @param currentUser The admin user performing the blocking action.
     * @return The UserDto representing the blocked user, or null if the operation is not allowed.
     */
    @Override
    public UserDto block(int id, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            Optional<User> byId = userRepository.findById(id);
            if (byId.isPresent()) {
                User user = byId.get();
                user.setEnabled(false);
                User save = userRepository.save(user);
                log.info("user by ID : {} is blocked", id);
                return UserMapper.userToUserDto(save);
            }
        }
        return null;
    }

    /**
     * Unblocks the user with the given ID by the current admin user. Only an admin user can perform this action.
     *
     * @param id          The unique identifier of the user to be unblocked.
     * @param currentUser The admin user performing the unblocking action.
     * @return The UserDto representing the unblocked user, or null if the operation is not allowed.
     */
    @Override
    public UserDto unBlock(int id, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            Optional<User> byId = userRepository.findById(id);
            if (byId.isPresent()) {
                User user = byId.get();
                user.setEnabled(true);
                User save = userRepository.save(user);
                log.info("user by ID : {} is unblocked", id);
                return UserMapper.userToUserDto(save);
            }
        }
        return null;
    }

    /**
     * Edits the order details, including the total amount and status, and updates the delivery information
     * with a new user assigned by the current admin user. Only an admin user can perform this action.
     *
     * @param orderDto    The OrderDto containing the updated order information.
     * @param deliveryId  The ID of the user to be assigned for delivery.
     * @param currentUser The admin user performing the order edit action.
     * @return The OrderDto representing the edited order, or null if the operation is not allowed or data is not found.
     */
    @Override
    @Transactional
    public OrderDto editOrder(OrderDto orderDto, int deliveryId, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {

            Optional<Order> byId = orderRepository.findById(orderDto.getId());
            Optional<Delivery> deliveryOptional = deliveryRepository.findAllByOrder_Id(orderDto.getId());
            Optional<User> userOptional = userRepository.findById(deliveryId);

            if (byId.isPresent() && deliveryOptional.isPresent() && userOptional.isPresent()) {
                Order orderFromDb = byId.get();
                orderFromDb.setTotalAmount(orderDto.getTotalAmount());
                orderFromDb.setStatus(orderDto.getStatus());
                Delivery delivery = deliveryOptional.get();
                delivery.setUser(userOptional.get());
                deliveryRepository.save(delivery);

                Order save = orderRepository.save(orderFromDb);
                log.info("order by admin is updated by ID : {} ", save.getId());
                return OrderMapper.orderToOrderDto(save);
            }
        }
        return null;
    }

    /**
     * Updates the user profile information, including name, surname, email, role, phone number, and profile picture.
     * The profile picture is uploaded and saved to the specified imageUploadPath. The operation is performed by the admin
     * user for regular users, and it includes image uploading and resizing. Admin user profiles cannot be updated.
     *
     * @param userDto       The UserDto containing the updated user information.
     * @param multipartFile The profile picture image file to be uploaded.
     * @return The UserDto representing the updated user, or the original UserDto if the operation is not allowed
     *         or the user is an admin.
     * @throws IOException If there's an issue with uploading or processing the image file.
     */
    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, MultipartFile multipartFile) throws IOException {
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isPresent() && userDto.getRole() != Role.ADMIN) {
            User user = userOptional.get();
            user.setName(userDto.getName());
            user.setSurname(userDto.getSurname());
            user.setEmail(userDto.getEmail());
            user.setRole(userDto.getRole());
            user.setPhoneNumber(userDto.getPhoneNumber());
            String fileName = ImageUtil.imageUpload(multipartFile, imageUploadPath);
            user.setProfilePic(fileName);
            User save = userRepository.save(user);
            log.info("user is updated by ID : {} ", save.getId());
            return UserMapper.userToUserDto(save);
        }
        return userDto;
    }

    /**
     * Edits a product's details, including its name, description, price, and categories. The associated images are uploaded,
     * resized, and saved to the specified imageUploadPath. The operation is performed by an admin user. The product's original
     * creator remains unchanged, and the review count is reset to zero. The product's ID is used to identify the product to be edited.
     *
     * @param productRequestDto The CreateProductRequestDto containing the updated product information.
     * @param files             The array of image files associated with the product.
     * @param user              The admin user performing the product edit action.
     * @param id                The ID of the product to be edited.
     * @throws IOException If there's an issue with uploading or processing the image files.
     */
    @Override
    @Transactional
    public void editProduct(CreateProductRequestDto productRequestDto, MultipartFile[] files, User user, int id) throws IOException {
        Optional<Product> productOptional = productRepository.findById(id);
        Product product = ProductMapper.map(productRequestDto);
        if (productOptional.isPresent()) {
            Product productFromDb = productOptional.get();
            productRequestDto.getCategories().removeIf(category -> category.getId() == 0);
            product.setCategories(CategoryMapper.dtoListMapper(productRequestDto.getCategories()));
            List<Image> imageList = new ArrayList<>();
            Optional<User> byId = userRepository.findById(user.getId());
            if (byId.isPresent()) {
                product.setUser(byId.get());
                for (MultipartFile multipartFile : files) {
                    if (multipartFile != null && !multipartFile.isEmpty()) {
                        Image image = new Image();
                        image.setImage(ImageUtil.imageUploadWithResize(multipartFile, imageUploadPath));
                        imageList.add(image);
                    }
                }
            }
            product.setUser(productFromDb.getUser());
            product.setId(productFromDb.getId());
            product.setImages(imageList);
            product.setReview(0L);
            Product save = productRepository.save(product);
            log.info("product is saved by ID: {}", save.getId());
        }
    }

}
