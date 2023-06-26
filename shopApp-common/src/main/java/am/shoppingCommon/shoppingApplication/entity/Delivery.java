package am.shoppingCommon.shoppingApplication.entity;

import lombok.Data;
import jakarta.persistence.*;


/**
 * Created by Ashot Simonyan on 21.05.23.
 */

@Entity
@Data
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private Order order;

    @ManyToOne
    private User user;
}
