package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data 
public class ReviewDetailDTO extends ReviewDTO {
    private PetDTO pet;
    private AdopterDTO adopter;

}
