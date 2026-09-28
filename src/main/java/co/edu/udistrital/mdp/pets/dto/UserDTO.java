package co.edu.udistrital.mdp.pets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;

    // Se acepta al crear/actualizar, pero no se devuelve en las respuestas de la API
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}