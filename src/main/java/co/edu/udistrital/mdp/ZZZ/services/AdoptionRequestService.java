package co.edu.udistrital.mdp.ZZZ.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.ZZZ.entities.AdopterEntity;
import co.edu.udistrital.mdp.ZZZ.entities.AdoptionEntity;
import co.edu.udistrital.mdp.ZZZ.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.ZZZ.entities.PetEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.ZZZ.repositories.AdopterRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.AdoptionRequestRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.PetRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class AdoptionRequestService {

	public static final String PENDIENTE = "PENDIENTE";
	public static final String APROBADO = "APROBADO";
	public static final String CANCELADO = "CANCELADO";

	@Autowired
	private AdoptionRequestRepository adoptionRequestRepository;

	@Autowired
	private AdopterRepository adopterRepository;

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private AdoptionRepository adoptionRepository;

	/**
	 * Crea un nuevo AdoptionRequest.
	 */
	@Transactional
	public AdoptionRequestEntity createAdoptionRequest(AdoptionRequestEntity adoptionRequest)
			throws IllegalOperationException {
		log.info("Inicia proceso de creación de la solicitud de adopción");

		if (adoptionRequest.getAdopter() == null || adoptionRequest.getAdopter().getId() == null)
			throw new IllegalOperationException("Adopter is not valid");

		Optional<AdopterEntity> adopter = adopterRepository.findById(adoptionRequest.getAdopter().getId());
		if (adopter.isEmpty())
			throw new IllegalOperationException("Adopter is not valid");

		if (adoptionRequest.getPet() == null || adoptionRequest.getPet().getId() == null)
			throw new IllegalOperationException("Pet is not valid");

		Optional<PetEntity> pet = petRepository.findById(adoptionRequest.getPet().getId());
		if (pet.isEmpty())
			throw new IllegalOperationException("Pet is not valid");

		if ("ADOPTADA".equals(pet.get().getAdoptionStatus()))
			throw new IllegalOperationException("Unable to create AdoptionRequest, the pet is already adopted");

		if (adoptionRequest.getDateRequest() == null)
			throw new IllegalOperationException("Date request is not valid");

		List<AdoptionRequestEntity> existing = adoptionRequestRepository
				.findByAdopterIdAndPetId(adopter.get().getId(), pet.get().getId());
		for (AdoptionRequestEntity req : existing) {
			if (PENDIENTE.equals(req.getStatus()))
				throw new IllegalOperationException(
						"The adopter already has a pending AdoptionRequest for this pet");
		}

		log.info("Termina proceso de creación de la solicitud de adopción");
		return adoptionRequestRepository.save(adoptionRequest);
	}

	/**
	 * Obtiene todas las solicitudes de adopción.
	 */
	@Transactional
	public List<AdoptionRequestEntity> getAdoptionRequests() {
		log.info("Inicia proceso de consultar todas las solicitudes de adopción");
		return adoptionRequestRepository.findAll();
	}

	/**
	 * Obtiene una solicitud de adopción por id.
	 */
	@Transactional
	public AdoptionRequestEntity getAdoptionRequest(Long id) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar la solicitud de adopción con id = {}", id);
		Optional<AdoptionRequestEntity> adoptionRequest = adoptionRequestRepository.findById(id);
		if (adoptionRequest.isEmpty())
			throw new EntityNotFoundException("AdoptionRequest not found");
		return adoptionRequest.get();
	}

	/**
	 * Actualiza una solicitud de adopción.
	 */
	@Transactional
	public AdoptionRequestEntity updateAdoptionRequest(Long id, AdoptionRequestEntity adoptionRequest)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de actualizar la solicitud de adopción con id = {}", id);

		Optional<AdoptionRequestEntity> current = adoptionRequestRepository.findById(id);
		if (current.isEmpty())
			throw new EntityNotFoundException("AdoptionRequest not found");

		if (CANCELADO.equals(current.get().getStatus()))
			throw new IllegalOperationException("Unable to update a cancelled AdoptionRequest");

		if (APROBADO.equals(adoptionRequest.getStatus()) && current.get().getAdopter() != null) {
			List<AdoptionEntity> adoptions = adoptionRepository
					.findByAdopterId(current.get().getAdopter().getId());
			for (AdoptionEntity adoption : adoptions) {
				if ("ACTIVA".equals(adoption.getStatus()))
					throw new IllegalOperationException(
							"Unable to approve AdoptionRequest, adopter already has an active Adoption");
			}
		}

		adoptionRequest.setId(id);
		log.info("Termina proceso de actualizar la solicitud de adopción con id = {}", id);
		return adoptionRequestRepository.save(adoptionRequest);
	}

	/**
	 * Elimina una solicitud de adopción.
	 */
	@Transactional
	public void deleteAdoptionRequest(Long id) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de borrar la solicitud de adopción con id = {}", id);

		Optional<AdoptionRequestEntity> current = adoptionRequestRepository.findById(id);
		if (current.isEmpty())
			throw new EntityNotFoundException("AdoptionRequest not found");

		if (APROBADO.equals(current.get().getStatus()))
			throw new IllegalOperationException(
					"Unable to delete AdoptionRequest, it was already approved and generated an Adoption");

		adoptionRequestRepository.deleteById(id);
		log.info("Termina proceso de borrar la solicitud de adopción con id = {}", id);
	}
}
