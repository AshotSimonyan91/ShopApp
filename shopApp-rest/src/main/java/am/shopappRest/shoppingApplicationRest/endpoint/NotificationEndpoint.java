package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationEndpoint {
    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationRequestDto notificationRequestDto) {
        notificationService.save(notificationRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeNotification(@RequestParam("id") int id){
        notificationService.remove(id);
        return ResponseEntity.ok().build();
    }

}