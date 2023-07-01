package am.shoppingCommon.shoppingApplication.entity;

import lombok.Data;
import jakarta.persistence.*;


/**
 * Created by Ashot Simonyan on 21.05.23.
 */

@Entity
@Data
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String parentCategory;
    private String image;
}
