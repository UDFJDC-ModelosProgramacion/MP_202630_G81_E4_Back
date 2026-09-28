package co.edu.udistrital.mdp.pets.dto;
 
import java.util.Date;
 
import lombok.Data;
 
@Data
public class FollowUpDTO {
	private Long id;
	private String followId;
	private String notes;
	private Date date;
	private String petCondition;
 

	private VeterinarianDTO veterinarian;
	private AdoptionDTO adoption;
	private PetDTO pet;
}