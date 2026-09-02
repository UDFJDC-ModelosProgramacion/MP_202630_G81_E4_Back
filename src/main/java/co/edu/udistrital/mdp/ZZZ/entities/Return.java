package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ReturnEntity extends BaseEntity {
    private String returnType;
    private String description;
    private String date;
    
}
