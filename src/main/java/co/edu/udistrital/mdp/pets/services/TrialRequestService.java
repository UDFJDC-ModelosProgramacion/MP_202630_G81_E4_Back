package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ReturnEntity;
import co.edu.udistrital.mdp.pets.entities.TrialRequestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.ReturnRepository;
import co.edu.udistrital.mdp.pets.repositories.TrialRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class TrialRequestService {

	public static final String ACTIVA = "ACTIVA";
	public static final String FINALIZADA = "FINALIZADA";
	public static final String CANCELADA = "CANCELADA";
	public static final String TRIAL_REQUEST_NOT_FOUND = "TrialRequest not found";

	private final TrialRequestRepository trialRequestRepository;

	private final AdopterRepository adopterRepository;

	private final PetRepository petRepository;

	private final ReturnRepository returnRepository;

	/**
	 * Crea una nueva TrialRequest.
	 */
	@Transactional
	public TrialRequestEntity createTrialRequest(TrialRequestEntity trialRequest) throws IllegalOperationException {
		log.info("Inicia proceso de creación de la solicitud de prueba");

		AdopterEntity adopter = validateAdopter(trialRequest);
		validateAdopterHasNoActiveTrial(adopter.getId());
		validatePet(trialRequest);

		if (trialRequest.getDate() == null)
			throw new IllegalOperationException("Date is not valid");

		log.info("Termina proceso de creación de la solicitud de prueba");
		return trialRequestRepository.save(trialRequest);
	}

	private AdopterEntity validateAdopter(TrialRequestEntity trialRequest) throws IllegalOperationException {
		if (trialRequest.getAdopter() == null || trialRequest.getAdopter().getId() == null)
			throw new IllegalOperationException("Adopter is not valid");

		Optional<AdopterEntity> adopter = adopterRepository.findById(trialRequest.getAdopter().getId());
		if (adopter.isEmpty())
			throw new IllegalOperationException("Adopter is not valid");

		return adopter.get();
	}

	private void validateAdopterHasNoActiveTrial(Long adopterId) throws IllegalOperationException {
		List<TrialRequestEntity> adopterTrials = trialRequestRepository.findByAdopterId(adopterId);
		for (TrialRequestEntity existing : adopterTrials) {
			if (ACTIVA.equals(existing.getStatus()))
				throw new IllegalOperationException(
						"Unable to create TrialRequest, the adopter already has an active TrialRequest");
		}
	}

	private void validatePet(TrialRequestEntity trialRequest) throws IllegalOperationException {
		if (trialRequest.getPet() == null || trialRequest.getPet().getId() == null)
			return;

		Optional<PetEntity> pet = petRepository.findById(trialRequest.getPet().getId());
		if (pet.isEmpty())
			throw new IllegalOperationException("Pet is not valid");

		if ("ADOPTADA".equals(pet.get().getAdoptionStatus()))
			throw new IllegalOperationException("Unable to create TrialRequest, the pet is already adopted");

		List<TrialRequestEntity> petTrials = trialRequestRepository.findByPetId(pet.get().getId());
		for (TrialRequestEntity existing : petTrials) {
			if (ACTIVA.equals(existing.getStatus()))
				throw new IllegalOperationException(
						"Unable to create TrialRequest, the pet is already in trial period with another adopter");
		}
	}

	/**
	 * Obtiene todas las solicitudes de prueba.
	 */
	@Transactional
	public List<TrialRequestEntity> getTrialRequests() {
		log.info("Inicia proceso de consultar todas las solicitudes de prueba");
		return trialRequestRepository.findAll();
	}

	/**
	 * Obtiene una solicitud de prueba por id.
	 */
	@Transactional
	public TrialRequestEntity getTrialRequest(Long id) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar la solicitud de prueba con id = {}", id);
		Optional<TrialRequestEntity> trialRequest = trialRequestRepository.findById(id);
		if (trialRequest.isEmpty())
			throw new EntityNotFoundException(TRIAL_REQUEST_NOT_FOUND);
		return trialRequest.get();
	}

	/**
	 * Actualiza una solicitud de prueba.
	 */
	@Transactional
	public TrialRequestEntity updateTrialRequest(Long id, TrialRequestEntity trialRequest)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de actualizar la solicitud de prueba con id = {}", id);

		Optional<TrialRequestEntity> current = trialRequestRepository.findById(id);
		if (current.isEmpty())
			throw new EntityNotFoundException(TRIAL_REQUEST_NOT_FOUND);

		if (FINALIZADA.equals(current.get().getStatus()) || CANCELADA.equals(current.get().getStatus()))
			throw new IllegalOperationException("Unable to update a finished or cancelled TrialRequest");

		trialRequest.setId(id);
		log.info("Termina proceso de actualizar la solicitud de prueba con id = {}", id);
		return trialRequestRepository.save(trialRequest);
	}

	/**
	 * Elimina una solicitud de prueba.
	 */
	@Transactional
	public void deleteTrialRequest(Long id) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de borrar la solicitud de prueba con id = {}", id);

		Optional<TrialRequestEntity> current = trialRequestRepository.findById(id);
		if (current.isEmpty())
			throw new EntityNotFoundException(TRIAL_REQUEST_NOT_FOUND);

		List<ReturnEntity> returns = returnRepository.findByTrialRequestId(id);
		if (!returns.isEmpty())
			throw new IllegalOperationException(
					"Unable to delete TrialRequest, it already has an associated Return");

		trialRequestRepository.deleteById(id);
		log.info("Termina proceso de borrar la solicitud de prueba con id = {}", id);
	}
}