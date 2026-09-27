package co.edu.udistrital.mdp.pets.dto;
 
import java.util.Date;
 
import lombok.Data;
 
@Data
public class AdoptionDTO {
	private Long id;
	private String adoptionId;
	private String status;
	private Date date;
}