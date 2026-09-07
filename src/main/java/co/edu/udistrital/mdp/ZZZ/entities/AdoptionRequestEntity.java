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
public class AdoptionRequestEntity extends BaseEntity {

	private String requestId;

	@Temporal(TemporalType.DATE)
	private Date dateRequest;

	private Boolean status;

	private String description;

	@PodamExclude
	@ManyToOne
	private AdopterEntity adopter;


	@PodamExclude
	@ManyToOne
	private PetEntity pet;

}