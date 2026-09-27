package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data 
public class ReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private String date;
}
