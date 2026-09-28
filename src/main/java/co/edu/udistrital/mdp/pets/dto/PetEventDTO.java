package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class PetEventDTO {
    private Long id;
    private String type;
    private String date;
    private String description;
}