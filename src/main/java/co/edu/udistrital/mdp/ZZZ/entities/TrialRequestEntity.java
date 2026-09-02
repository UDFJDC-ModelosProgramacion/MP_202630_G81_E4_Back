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
public class TrialRequestEntity extends BaseEntity {

	private String trialId;

	@Temporal(TemporalType.DATE)
	private Date date;

	private String status;

	private String description;

	@PodamExclude
	@ManyToOne
	private AdopterEntity adopter;

	@PodamExclude
	@ManyToOne
	private ShelterEntity shelter;

}