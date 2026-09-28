package co.edu.udistrital.mdp.pets.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShelterDetailDTO extends ShelterDTO {
    private List<VeterinarianDTO> veterinarians = new ArrayList<>();
    private List<ShelterEventDTO> shelterEvents = new ArrayList<>();
    private List<PetDTO> pets = new ArrayList<>();
}