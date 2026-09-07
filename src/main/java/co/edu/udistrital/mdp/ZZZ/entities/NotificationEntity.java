package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class NotificationEntity extends CommunicationEntity{

    @PodamExclude
    @ManyToOne
    private UserEntity user;

    @PodamExclude
    @ManyToOne
    private AdoptionEntity adoption;

    @PodamExclude
    @ManyToOne
    private PetEntity pet;
}
