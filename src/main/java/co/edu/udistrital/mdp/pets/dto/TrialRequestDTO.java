package co.edu.udistrital.mdp.pets.dto;
 
import java.util.Date;
 
import lombok.Data;
 
@Data
public class TrialRequestDTO {
    
	private Long id;
	private String trialId;
	private Date date;
	private String status;
	private String description;
}