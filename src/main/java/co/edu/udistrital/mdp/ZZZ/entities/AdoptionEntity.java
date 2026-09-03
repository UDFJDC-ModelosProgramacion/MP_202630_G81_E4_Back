package co.edu.udistrital.mdp.ZZZ.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;


@Data
@Entity
public class AdoptionEntity extends BaseEntity {

	
	private String adoptionId;
	private String status;

    
	@Temporal(TemporalType.DATE)
	private Date date;

	
	@PodamExclude
	@OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
	private FollowUpEntity followUp;

	
	@PodamExclude
	@OneToOne
	private AdopterEntity adopter;

	
	@PodamExclude
	@OneToOne
	private PetEntity pet;

	
	@PodamExclude
	@OneToMany(mappedBy = "adoption", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<NotificationEntity> notifications = new ArrayList<>();

}