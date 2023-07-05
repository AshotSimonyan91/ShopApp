package am.shoppingCommon.shoppingApplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private String subject;
    @CreationTimestamp
    private LocalDateTime dateTime;
    @ManyToOne(optional = false)
    private User user;
}
