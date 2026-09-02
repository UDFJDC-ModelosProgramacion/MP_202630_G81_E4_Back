package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class UserEntity extends BaseEntity {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String address;
}