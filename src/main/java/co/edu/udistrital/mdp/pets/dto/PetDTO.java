package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class PetDTO {
    private Long id;
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
    
    // Asociación de cardinalidad 1
    private MedicalHistoryDTO medicalHistory;
    
    private ShelterDTO shelter;
}