package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;
import lombok.Data;

@Data
public class ShelterEventDTO {
    private Long id;
    private String eventId;
    private String name;
    private String description;
    private Date date;
    private String type;
}