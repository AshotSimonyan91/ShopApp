package am.shoppingCommon.shoppingApplication.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */

@Entity
@Data
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
