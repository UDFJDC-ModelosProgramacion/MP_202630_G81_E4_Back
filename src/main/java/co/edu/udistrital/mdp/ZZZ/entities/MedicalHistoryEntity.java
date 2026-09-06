package co.edu.udistrital.mdp.ZZZ.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class MedicalHistoryEntity extends BaseEntity {

    private String vaccine;
    private String diseases;
    private String treatment;
    private Boolean sterilized;

    @PodamExclude
    @OneToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;
}