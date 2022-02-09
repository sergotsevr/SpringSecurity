package security.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "username")
    private String username;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "password")
    private String password;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Roles role;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
