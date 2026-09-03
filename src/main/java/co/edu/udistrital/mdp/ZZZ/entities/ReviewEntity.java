package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ReviewEntity extends BaseEntity {
    private Integer rating;
    private String comment, date;

    @PodamExclude
    @ManyToOne
    private PetEntity pet;
}
