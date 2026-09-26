package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class ShelterDTO {
    private Long id;
    private Integer nit;
    private String description;
    private String capacity;
    private String city;
    private String photo;
    private String video;
}