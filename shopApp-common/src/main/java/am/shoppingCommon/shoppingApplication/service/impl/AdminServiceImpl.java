package am.shoppingCommon.shoppingApplication.service.impl;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.repository.DeliveryRepository;
import am.shoppingCommon.shoppingApplication.repository.OrderRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.AdminService;
import am.shoppingCommon.shoppingApplication.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;

    @Value("${shopping-app.upload.image.path}")
    private String imageUploadPath;

    @Override
    public void block(int id, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            Optional<User> byId = userRepository.findById(id);
            if (byId.isPresent()) {
                User user = byId.get();
                user.setEnabled(false);
                userRepository.save(user);
            }
        }
    }

    @Override
    public void unBlock(int id, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            Optional<User> byId = userRepository.findById(id);
            if (byId.isPresent()) {
                User user = byId.get();
                user.setEnabled(true);
                userRepository.save(user);
            }
        }
    }

    @Override
    @Transactional
    public Order editOrder(OrderDto orderDto, int deliveryId, User currentUser) {
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

                return orderRepository.save(orderFromDb);
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void updateUser(UserDto userDto, MultipartFile multipartFile) throws IOException {
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isPresent() && userDto.getRole() != Role.ADMIN) {
            User user = userOptional.get();
            user.setName(userDto.getName());
            user.setSurname(userDto.getSurname());
            user.setEmail(userDto.getEmail());
            user.setRole(userDto.getRole());
            user.setPhoneNumber(userDto.getPhoneNumber());
            String fileName = ImageUtil.imageUpload(multipartFile,imageUploadPath);
            user.setProfilePic(fileName);
            userRepository.save(user);
        }
    }
}
