package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */

/**
 * RestController class responsible for handling notification-related API operations.
 * It provides endpoints to send and remove notifications.
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationEndpoint {
    private final NotificationService notificationService;


    /**
     * Sends a notification based on the provided NotificationRequestDto.
     *
     * @param notificationRequestDto The DTO containing notification details.
     * @return ResponseEntity with the created NotificationDto as a JSON response.
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationDto> sendNotification(@RequestBody NotificationRequestDto notificationRequestDto) {
        return ResponseEntity.ok(notificationService.save(notificationRequestDto));
    }


    /**
     * Removes a notification with the given ID.
     *
     * @param id The ID of the notification to be removed.
     * @return ResponseEntity with no content (204 No Content) if the notification is successfully removed.
     * If the notification with the given ID does not exist, it also returns 204 No Content.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeNotification(@RequestParam("id") int id) {
        notificationService.remove(id);
        return ResponseEntity.noContent().build();
    }

}
