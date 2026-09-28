package co.edu.udistrital.mdp.pets.dto;
 
import java.util.ArrayList;
import java.util.List;
 
import lombok.Data;
import lombok.EqualsAndHashCode;
 
@Data
@EqualsAndHashCode(callSuper = true)
public class AdoptionDetailDTO extends AdoptionDTO {
 
	
	private List<FollowUpDTO> followUps = new ArrayList<>();
	private List<NotificationDTO> notifications = new ArrayList<>();
}
