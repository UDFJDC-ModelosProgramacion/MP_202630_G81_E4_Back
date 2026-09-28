package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ShelterEventRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ShelterEventService {

    private static final Logger log = LoggerFactory.getLogger(ShelterEventService.class);

    private static final String EVENT_NOT_FOUND = "El evento con el id dado no fue encontrado";
    private static final String SHELTER_NOT_FOUND = "El shelter con el id dado no fue encontrado";

    private final ShelterEventRepository shelterEventRepository;
    private final ShelterRepository shelterRepository;

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ShelterEventEntity createShelterEvent(Long shelterId, ShelterEventEntity shelterEventEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación del evento");

        Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterId);
        if (shelterEntity.isEmpty()) {
            throw new EntityNotFoundException(SHELTER_NOT_FOUND);
        }

        if (shelterEventEntity.getDate() == null) {
            throw new IllegalOperationException("La fecha del evento es obligatoria");
        }

        if (shelterEventEntity.getName() == null || shelterEventEntity.getName().isEmpty()) {
            throw new IllegalOperationException("El nombre del evento es obligatorio");
        }

        shelterEventEntity.setShelter(shelterEntity.get());
        log.info("Termina proceso de creación del evento");
        return shelterEventRepository.save(shelterEventEntity);
    }

    @Transactional
    public List<ShelterEventEntity> getShelterEvents() {
        log.info("Inicia proceso de consultar todos los eventos");
        return shelterEventRepository.findAll();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public ShelterEventEntity getShelterEvent(Long shelterEventId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar el evento con id = {}", shelterEventId);
        Optional<ShelterEventEntity> shelterEventEntity = shelterEventRepository.findById(shelterEventId);
        if (shelterEventEntity.isEmpty()) {
            throw new EntityNotFoundException(EVENT_NOT_FOUND);
        }
        log.info("Termina proceso de consultar el evento con id = {}", shelterEventId);
        return shelterEventEntity.get();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ShelterEventEntity updateShelterEvent(Long shelterEventId, ShelterEventEntity shelterEvent)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar el evento con id = {}", shelterEventId);
        Optional<ShelterEventEntity> shelterEventEntity = shelterEventRepository.findById(shelterEventId);
        if (shelterEventEntity.isEmpty()) {
            throw new EntityNotFoundException(EVENT_NOT_FOUND);
        }

        if (shelterEvent.getShelter() != null) {
            Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterEvent.getShelter().getId());
            if (shelterEntity.isEmpty()) {
                throw new IllegalOperationException(SHELTER_NOT_FOUND);
            }
        }

        if (shelterEvent.getShelter() == null) {
            shelterEvent.setShelter(shelterEventEntity.get().getShelter());
        }
        
        shelterEvent.setId(shelterEventId);
        log.info("Termina proceso de actualizar el evento con id = {}", shelterEventId);
        return shelterEventRepository.save(shelterEvent);
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public void deleteShelterEvent(Long shelterEventId) throws EntityNotFoundException {
        log.info("Inicia proceso de borrar el evento con id = {}", shelterEventId);
        Optional<ShelterEventEntity> shelterEventEntity = shelterEventRepository.findById(shelterEventId);
        if (shelterEventEntity.isEmpty()) {
            throw new EntityNotFoundException(EVENT_NOT_FOUND);
        }
        shelterEventRepository.deleteById(shelterEventId);
        log.info("Termina proceso de borrar el evento con id = {}", shelterEventId);
    }
}