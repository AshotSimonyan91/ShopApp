package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.entity.Notification;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.NotificationMapper;
import am.shoppingCommon.shoppingApplication.repository.NotificationRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */

/**
 * Service implementation class responsible for handling notification-related operations.
 * It implements the NotificationService interface and provides functionalities for retrieving, saving, and removing notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;


    /**
     * Retrieves a list of notifications for a user based on the provided user ID.
     *
     * @param id The ID of the user to retrieve notifications for.
     * @return List of NotificationDto representing the user's notifications as a list.
     */
    @Override
    public List<NotificationDto> findAllByUserId(int id) {
        List<Notification> allByUserId = notificationRepository.findAllByUser_Id(id);
        return NotificationMapper.mapToListNotificationDto(allByUserId);
    }

    /**
     * Removes a notification based on the provided notification ID.
     *
     * @param id The ID of the notification to be removed.
     */
    @Override
    public void remove(int id) {
        notificationRepository.deleteById(id);
    }

    /**
     * Saves a new notification based on the provided NotificationRequestDto.
     *
     * @param notificationRequestDto The DTO containing details of the notification to be saved.
     * @return NotificationDto representing the saved notification as a response.
     */
    @Override
    public NotificationDto save(NotificationRequestDto notificationRequestDto) {
        notificationRequestDto.setEmail(notificationRequestDto.getEmail().trim());
        Optional<User> userOptional = userRepository.findByEmail(notificationRequestDto.getEmail());
        if (notificationRequestDto.getEmail() != null && notificationRequestDto.getMessage() != null && userOptional.isPresent()) {
            Notification notification = NotificationMapper.map(notificationRequestDto);
            notification.setUser(userOptional.get());
            Notification save = notificationRepository.save(notification);
            log.info("notification is created by ID : {} & by user ID{}", save.getId(), save.getUser().getId());
            return NotificationMapper.mapToDto(save);

        }
        return null;
    }


    /**
     * Retrieves a list of notifications for a user with pagination based on the provided user ID.
     *
     * @param id The ID of the user to retrieve notifications for.
     * @return List of NotificationDto representing the user's notifications with pagination.
     */
    @Override
    public List<NotificationDto> notifications(int id) {
        Pageable pageable = PageRequest.of(0, 20);
        List<Notification> lastNotifications = notificationRepository.findAllByUserIdOrderByDateTimeDesc(id, pageable);
        return NotificationMapper.mapToListNotificationDto(lastNotifications);
    }


    /**
     * Retrieves the last 3 notifications for a user based on the provided user ID.
     *
     * @param userId The ID of the user to retrieve the last 3 notifications for.
     * @return List of NotificationDto representing the user's last 3 notifications.
     */
    @Override
    public List<NotificationDto> last3Notifications(int userId) {
        Pageable pageable = PageRequest.of(0, 3);
        List<Notification> lastNotifications = notificationRepository.findAllByUserIdOrderByDateTimeDesc(userId, pageable);
        return NotificationMapper.mapToListNotificationDto(lastNotifications);
    }
}
