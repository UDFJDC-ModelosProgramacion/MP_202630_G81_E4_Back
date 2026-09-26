package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.entities.FollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.TrialRequestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRequestRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.TrialRequestRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la lógica de negocio para AdoptionEntity.
 *
 * Posibles valores del atributo "status": "ACTIVA", "FINALIZADA", "CANCELADA".
 */
@Slf4j
@Service
public class AdoptionService {

	public static final String ACTIVA = "ACTIVA";
	public static final String FINALIZADA = "FINALIZADA";

	@Autowired
	private AdoptionRepository adoptionRepository;

	@Autowired
	private AdopterRepository adopterRepository;

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private AdoptionRequestRepository adoptionRequestRepository;

	@Autowired
	private TrialRequestRepository trialRequestRepository;

	/**
	 * Crea una nueva Adoption.
	 */
	@Transactional
	public AdoptionEntity createAdoption(AdoptionEntity adoption) throws IllegalOperationException {
		log.info("Inicia proceso de creación de la adopción");

		if (adoption.getAdopter() == null || adoption.getAdopter().getId() == null)
			throw new IllegalOperationException("Adopter is not valid");

		Optional<AdopterEntity> adopter = adopterRepository.findById(adoption.getAdopter().getId());
		if (adopter.isEmpty())
			throw new IllegalOperationException("Adopter is not valid");

		if (adoption.getPet() == null || adoption.getPet().getId() == null)
			throw new IllegalOperationException("Pet is not valid");

		Optional<PetEntity> pet = petRepository.findById(adoption.getPet().getId());
		if (pet.isEmpty())
			throw new IllegalOperationException("Pet is not valid");

		List<AdoptionEntity> petAdoptions = adoptionRepository.findByPetId(pet.get().getId());
		for (AdoptionEntity existing : petAdoptions) {
			if (ACTIVA.equals(existing.getStatus()))
				throw new IllegalOperationException("Unable to create Adoption, the pet already has an active Adoption");
		}

		List<AdoptionRequestEntity> requests = adoptionRequestRepository
				.findByAdopterIdAndPetId(adopter.get().getId(), pet.get().getId());
		boolean approvedFound = false;
		for (AdoptionRequestEntity req : requests) {
			if ("APROBADO".equals(req.getStatus()))
				approvedFound = true;
		}
		if (!approvedFound)
			throw new IllegalOperationException(
					"Unable to create Adoption, there is no approved AdoptionRequest for this adopter and pet");

		List<TrialRequestEntity> trialRequests = trialRequestRepository
				.findByAdopterId(adopter.get().getId());
		for (TrialRequestEntity trial : trialRequests) {
			if (trial.getPet() != null && pet.get().getId().equals(trial.getPet().getId())
					&& "ACTIVA".equals(trial.getStatus()))
				throw new IllegalOperationException(
						"Unable to create Adoption, the adopter has an active TrialRequest for this pet");
		}

		log.info("Termina proceso de creación de la adopción");
		return adoptionRepository.save(adoption);
	}

	/**
	 * Obtiene todas las adopciones.
	 */
	@Transactional
	public List<AdoptionEntity> getAdoptions() {
		log.info("Inicia proceso de consultar todas las adopciones");
		return adoptionRepository.findAll();
	}

	/**
	 * Obtiene una adopción por id.
	 */
	@Transactional
	public AdoptionEntity getAdoption(Long id) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar la adopción con id = {}", id);
		Optional<AdoptionEntity> adoption = adoptionRepository.findById(id);
		if (adoption.isEmpty())
			throw new EntityNotFoundException("Adoption not found");
		return adoption.get();
	}

	/**
	 * Actualiza una adopción.
	 */
	@Transactional
	public AdoptionEntity updateAdoption(Long id, AdoptionEntity adoption)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de actualizar la adopción con id = {}", id);

		Optional<AdoptionEntity> current = adoptionRepository.findById(id);
		if (current.isEmpty())
			throw new EntityNotFoundException("Adoption not found");

		if (FINALIZADA.equals(current.get().getStatus())
				&& adoption.getStatus() != null && !adoption.getStatus().equals(current.get().getStatus()))
			throw new IllegalOperationException("Unable to update status of a finished Adoption");

		if (ACTIVA.equals(adoption.getStatus())) {
			List<FollowUpEntity> followUps = current.get().getFollowUps();
			if (followUps == null || followUps.isEmpty())
				throw new IllegalOperationException(
						"Unable to confirm Adoption, an initial FollowUp must be scheduled");
		}

		adoption.setId(id);
		log.info("Termina proceso de actualizar la adopción con id = {}", id);
		return adoptionRepository.save(adoption);
	}

	/**
	 * Elimina una adopción.
	 */
	@Transactional
	public void deleteAdoption(Long id) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de borrar la adopción con id = {}", id);

		Optional<AdoptionEntity> current = adoptionRepository.findById(id);
		if (current.isEmpty())
			throw new EntityNotFoundException("Adoption not found");

		List<FollowUpEntity> followUps = current.get().getFollowUps();
		if (followUps != null) {
			for (FollowUpEntity followUp : followUps) {
				if (followUp.getNotes() != null && !followUp.getNotes().isBlank())
					throw new IllegalOperationException(
							"Unable to delete Adoption, it already has FollowUps with registered notes");
			}
		}

		adoptionRepository.deleteById(id);
		log.info("Termina proceso de borrar la adopción con id = {}", id);
	}
}
