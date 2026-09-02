package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ReviewEntity extends BaseEntity {
    private Integer rating;
    private String comment, date;

    @ManyToOne
    private PetEntity pet;
}
