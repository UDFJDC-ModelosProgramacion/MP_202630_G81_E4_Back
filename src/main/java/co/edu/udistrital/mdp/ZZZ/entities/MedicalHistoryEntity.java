package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class MedicalHistoryEntity extends BaseEntity {
    
    // Aquí van los atributos específicos:
    // private String diagnosis;
    // private String treatment;
    
}