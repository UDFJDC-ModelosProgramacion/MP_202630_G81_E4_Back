package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class VeterinarianEntity extends BaseEntity {

    private String veterinarianId;
    private String specialty;
    private String availability;

    @PodamExclude
    @ManyToOne
    private ShelterEntity shelter;

    
}