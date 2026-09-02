package co.edu.udistrital.mdp.ZZZ.entities; 

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class PetEntity extends BaseEntity {
    
    // Aquí van tus atributos específicos, por ejemplo:
    // private String name;
    // private String species;
    // private int age;
    
}