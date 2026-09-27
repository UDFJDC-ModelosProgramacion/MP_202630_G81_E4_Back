package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PetDetailDTO extends PetDTO {
    // Asociación de cardinalidad Muchos (colección)
    private List<PetEventDTO> events;
}