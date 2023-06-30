package am.shoppingCommon.shoppingApplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Size(min = 2,max = 15,message = "Name length it should be min 2 max 15 characters")
    @Column(nullable = false)
    private String name;
    @NotNull
    @Size(min = 2,max = 15,message = "Surname length it should be min 2 max 15 characters")
    @Column(nullable = false)
    private String surname;
    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Email is no valid")
    @Column(nullable = false)
    private String email;
//    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
//            message = "Should be min 8 character,include digit and capital letter")
    @Column(nullable = false)
    private String password;
    private String phoneNumber;
    private String profilePic;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    @Column(columnDefinition = "TINYINT default 0")
    private boolean enabled;
    private String token;

    @ManyToMany(cascade = {CascadeType.ALL,CascadeType.REMOVE})
    @JoinTable(name = "user_address", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "address_id"}),
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<Address> addresses;
}