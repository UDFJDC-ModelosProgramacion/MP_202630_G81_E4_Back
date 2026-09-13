package co.edu.udistrital.mdp.ZZZ.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.ZZZ.entities.AdoptionEntity;
import co.edu.udistrital.mdp.ZZZ.entities.FollowUpEntity;
import co.edu.udistrital.mdp.ZZZ.entities.PetEntity;
import co.edu.udistrital.mdp.ZZZ.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.ZZZ.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.FollowUpRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.PetRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.VeterinarianRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class FollowUpService {

	@Autowired
	private FollowUpRepository followUpRepository;

	@Autowired
	private AdoptionRepository adoptionRepository;

	@Autowired
	private VeterinarianRepository veterinarianRepository;

	@Autowired
	private PetRepository petRepository;

	/**
	 * Crea un nuevo FollowUp.
	 */
	@Transactional
	public FollowUpEntity createFollowUp(FollowUpEntity followUp) throws IllegalOperationException {
		log.info("Inicia proceso de creación del seguimiento");

		if (followUp.getAdoption() == null || followUp.getAdoption().getId() == null)
			throw new IllegalOperationException("Adoption is not valid");

		Optional<AdoptionEntity> adoption = adoptionRepository.findById(followUp.getAdoption().getId());
		if (adoption.isEmpty())
			throw new IllegalOperationException("Adoption is not valid");

		if (!"ACTIVA".equals(adoption.get().getStatus()))
			throw new IllegalOperationException(
					"Unable to create FollowUp, the Adoption is not confirmed/active");

		if (followUp.getVeterinarian() == null || followUp.getVeterinarian().getId() == null)
			throw new IllegalOperationException("Veterinarian is not valid");

		Optional<VeterinarianEntity> veterinarian = veterinarianRepository
				.findById(followUp.getVeterinarian().getId());
		if (veterinarian.isEmpty())
			throw new IllegalOperationException("Veterinarian is not valid");

		if (followUp.getDate() == null)
			throw new IllegalOperationException("Date is not valid");

		if (followUp.getPet() == null || followUp.getPet().getId() == null)
			throw new IllegalOperationException("Pet is not valid");

		Optional<PetEntity> pet = petRepository.findById(followUp.getPet().getId());
		if (pet.isEmpty())
			throw new IllegalOperationException("Pet is not valid");

		List<FollowUpEntity> existing = followUpRepository.findByAdoptionId(adoption.get().getId());
		for (FollowUpEntity other : existing) {
			if (followUp.getDate().equals(other.getDate()))
				throw new IllegalOperationException(
						"Unable to create FollowUp, there is already a FollowUp with the same date for this Adoption");
		}

		log.info("Termina proceso de creación del seguimiento");
		return followUpRepository.save(followUp);
	}

	/**
	 * Obtiene todos los seguimientos.
	 */
	@Transactional
	public List<FollowUpEntity> getFollowUps() {
		log.info("Inicia proceso de consultar todos los seguimientos");
		return followUpRepository.findAll();
	}

	/**
	 * Obtiene un seguimiento por id.
	 */
	@Transactional
	public FollowUpEntity getFollowUp(Long id) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar el seguimiento con id = {}", id);
		Optional<FollowUpEntity> followUp = followUpRepository.findById(id);
		if (followUp.isEmpty())
			throw new EntityNotFoundException("FollowUp not found");
		return followUp.get();
	}

	/**
	 * Actualiza un seguimiento.
	 */
	@Transactional
	public FollowUpEntity updateFollowUp(Long id, FollowUpEntity followUp)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de actualizar el seguimiento con id = {}", id);

		Optional<FollowUpEntity> current = followUpRepository.findById(id);
		if (current.isEmpty())
			throw new EntityNotFoundException("FollowUp not found");

		boolean alreadyRegistered = current.get().getNotes() != null && !current.get().getNotes().isBlank();
		boolean dateChanged = followUp.getDate() != null && !followUp.getDate().equals(current.get().getDate());

		if (alreadyRegistered && dateChanged)
			throw new IllegalOperationException(
					"Unable to reschedule a FollowUp whose checkup was already registered");

		followUp.setId(id);
		log.info("Termina proceso de actualizar el seguimiento con id = {}", id);
		return followUpRepository.save(followUp);
	}

	/**
	 * Elimina un seguimiento.
	 */
	@Transactional
	public void deleteFollowUp(Long id) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de borrar el seguimiento con id = {}", id);

		Optional<FollowUpEntity> current = followUpRepository.findById(id);
		if (current.isEmpty())
			throw new EntityNotFoundException("FollowUp not found");

		if (current.get().getNotes() != null && !current.get().getNotes().isBlank())
			throw new IllegalOperationException(
					"Unable to delete FollowUp, it already has registered notes");

		followUpRepository.deleteById(id);
		log.info("Termina proceso de borrar el seguimiento con id = {}", id);
	}
}
