package co.edu.udistrital.mdp.pets.dto;
 
import java.util.Date;
 
import lombok.Data;
 
@Data
public class AdoptionRequestDTO {
	private Long id;
	private String requestId;
	private Date dateRequest;
	private String status;
	private String description;
}
 