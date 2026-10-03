package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class NotificationDetailDTO  extends NotificationDTO {
    private UserDTO user;
    private AdoptionDTO adoption;
    private PetDTO pet;
    
}
