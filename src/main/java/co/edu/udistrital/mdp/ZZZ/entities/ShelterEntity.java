package co.edu.udistrital.mdp.ZZZ.entities;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class ShelterEntity extends BaseEntity {

    private Integer nit;
    private String description;
    private String capacity;
    private String city;
    private String photo;
    private String video;

    //@PodamExclude
    //@OneToMany(mappedBy = "shelter", cascade = CascadeType.PERSIST, orphanRemoval = true)
    //private List<PetEntity> pets = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<VeterinarianEntity> veterinarians = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ShelterEventEntity> shelterEvents = new ArrayList<>();
}