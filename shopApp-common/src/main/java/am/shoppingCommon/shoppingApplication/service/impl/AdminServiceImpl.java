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
