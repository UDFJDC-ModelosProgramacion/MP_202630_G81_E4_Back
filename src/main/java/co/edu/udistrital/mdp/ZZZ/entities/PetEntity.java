package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class PetEntity extends BaseEntity {

    private String name;
    private String specie;
    private String breed;
    private Integer age;
    private String sex;
    private String size;
    private Double weight;
    private String healthStatus;
    private String adoptionStatus;
    private String description;
    private String admissionDate;
    private String photo;

    @PodamExclude
    @OneToOne(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private MedicalHistoryEntity medicalHistory;

    @PodamExclude
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetEventEntity> events = new ArrayList<>();
}