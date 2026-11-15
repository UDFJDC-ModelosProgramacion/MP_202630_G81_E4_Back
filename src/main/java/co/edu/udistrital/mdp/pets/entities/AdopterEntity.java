package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class AdopterEntity extends UserEntity {
    private String address;
    private String phone;
    private String occupation;
}