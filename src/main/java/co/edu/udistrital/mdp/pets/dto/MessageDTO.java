package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private Long id;
    private String content;
    private String date;
    private boolean read;

    // Asociaciones de cardinalidad 1: van en el DTO, no en el DetailDTO
    private UserDTO sender;
    private UserDTO receiver;
}