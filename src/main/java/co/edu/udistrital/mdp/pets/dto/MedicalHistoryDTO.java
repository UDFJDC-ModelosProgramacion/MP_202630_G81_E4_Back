package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class MedicalHistoryDTO {
    private Long id;
    private String vaccine;
    private String diseases;
    private String treatment;
    private Boolean sterilized;
}