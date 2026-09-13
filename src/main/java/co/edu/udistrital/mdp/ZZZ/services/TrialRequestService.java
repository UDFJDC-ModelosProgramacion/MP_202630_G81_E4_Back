package co.edu.udistrital.mdp.ZZZ.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.ZZZ.entities.AdopterEntity;
import co.edu.udistrital.mdp.ZZZ.entities.PetEntity;
import co.edu.udistrital.mdp.ZZZ.entities.ReturnEntity;
import co.edu.udistrital.mdp.ZZZ.entities.TrialRequestEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.ZZZ.repositories.AdopterRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.PetRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.ReturnRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.TrialRequestRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class TrialRequestService {

	public static final String ACTIVA = "ACTIVA";
	public static final String FINALIZADA = "FINALIZADA";
	public static final String CANCELADA = "CANCELADA";

	@Autowired
	private TrialRequestRepository trialRequestRepository;

	@Autowired
	private AdopterRepository adopterRepository;

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private ReturnRepository returnRepository;

	/**
	 * Crea una nueva TrialRequest.
	 */
	@Transactional
	public TrialRequestEntity createTrialRequest(TrialRequestEntity trialRequest) throws IllegalOperationException {
		log.info("Inicia proceso de creación de la solicitud de prueba");

		if (trialRequest.getAdopter() == null || trialRequest.getAdopter().getId() == null)
			throw new IllegalOperationException("Adopter is not valid");

		Optional<AdopterEntity> adopter = adopterRepository.findById(trialRequest.getAdopter().getId());
		if (adopter.isEmpty())
			throw new IllegalOperationException("Adopter is not valid");

		List<TrialRequestEntity> adopterTrials = trialRequestRepository.findByAdopterId(adopter.get().getId());
		for (TrialRequestEntity existing : adopterTrials) {
			if (ACTIVA.equals(existing.getStatus()))
				throw new IllegalOperationException(
						"Unable to create TrialRequest, the adopter already has an active TrialRequest");
		}

		if (trialRequest.getPet() != null && trialRequest.getPet().getId() != null) {
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

		if (trialRequest.getDate() == null)
			throw new IllegalOperationException("Date is not valid");

		log.info("Termina proceso de creación de la solicitud de prueba");
		return trialRequestRepository.save(trialRequest);
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
			throw new EntityNotFoundException("TrialRequest not found");
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
			throw new EntityNotFoundException("TrialRequest not found");

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
			throw new EntityNotFoundException("TrialRequest not found");

		List<ReturnEntity> returns = returnRepository.findByTrialRequestId(id);
		if (!returns.isEmpty())
			throw new IllegalOperationException(
					"Unable to delete TrialRequest, it already has an associated Return");

		trialRequestRepository.deleteById(id);
		log.info("Termina proceso de borrar la solicitud de prueba con id = {}", id);
	}
}
