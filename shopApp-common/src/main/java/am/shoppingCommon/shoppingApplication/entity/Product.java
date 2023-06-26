package am.shoppingCommon.shoppingApplication.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String productCode;
    @Column(columnDefinition = "text")
    private String description;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private int count;
    @ManyToOne(optional = false)
    private User user;
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Image> images;
    @ManyToMany
    @JoinTable(name = "category_product", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "category_id"}),
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;
}
