package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class PetEventEntity extends BaseEntity {
    
    // Aquí van los atributos específicos:
    // private Date eventDate;
    // private String description;
    
}