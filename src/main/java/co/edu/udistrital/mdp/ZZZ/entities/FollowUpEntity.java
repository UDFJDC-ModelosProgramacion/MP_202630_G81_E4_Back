package co.edu.udistrital.mdp.ZZZ.entities;
 
import java.util.Date;
 
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
 
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
 

@Data
@Entity
public class FollowUpEntity extends BaseEntity {
 
	private String followId;
 
	private String notes;
 
	@Temporal(TemporalType.DATE)
	private Date date;
 
	private String petCondition;
 

	@PodamExclude
	@ManyToOne
	private VeterinarianEntity veterinarian;
 
}