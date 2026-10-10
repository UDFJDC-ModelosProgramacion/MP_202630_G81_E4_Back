package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data 
public class NotificationDTO {
    private Long id;
    private String content;
    private String date;
    private boolean read;
    private String channel;
}
