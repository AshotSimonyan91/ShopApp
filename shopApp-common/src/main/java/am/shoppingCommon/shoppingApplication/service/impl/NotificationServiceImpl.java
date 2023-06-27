package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.service.NotificationService;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Notification;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.NotificationMapper;
import am.shoppingCommon.shoppingApplication.repository.NotificationRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;


    @Override
    public List<NotificationResponseDto> findAllByUserId(int id) {
        List<Notification> allByUserId = notificationRepository.findAllByUser_Id(id);
        return NotificationMapper.map(allByUserId);
    }

    @Override
    public void remove(int id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public void save(NotificationRequestDto notificationRequestDto) {
        notificationRequestDto.setEmail(notificationRequestDto.getEmail().trim());
        Optional<User> userOptional = userRepository.findByEmail(notificationRequestDto.getEmail());
        if (notificationRequestDto.getEmail() != null && notificationRequestDto.getMessage() != null && userOptional.isPresent()) {
            Notification notification = NotificationMapper.map(notificationRequestDto);
            notification.setUser(userOptional.get());
            notificationRepository.save(notification);
        }
    }
}
