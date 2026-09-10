package co.edu.udistrital.mdp.ZZZ.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.ZZZ.entities.ShelterEntity;
import co.edu.udistrital.mdp.ZZZ.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.ZZZ.repositories.ShelterEventRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.ShelterRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ShelterEventService {

    private static final Logger log = LoggerFactory.getLogger(ShelterEventService.class);

    private final ShelterEventRepository shelterEventRepository;
    private final ShelterRepository shelterRepository;

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ShelterEventEntity createShelterEvent(Long shelterId, ShelterEventEntity shelterEventEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación del evento");

        Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterId);
        if (shelterEntity.isEmpty()) {
            throw new EntityNotFoundException("El shelter con el id dado no fue encontrado");
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
        log.info("Inicia proceso de consultar el evento con id = " + shelterEventId);
        Optional<ShelterEventEntity> shelterEventEntity = shelterEventRepository.findById(shelterEventId);
        if (shelterEventEntity.isEmpty()) {
            throw new EntityNotFoundException("El evento con el id dado no fue encontrado");
        }
        log.info("Termina proceso de consultar el evento con id = " + shelterEventId);
        return shelterEventEntity.get();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ShelterEventEntity updateShelterEvent(Long shelterEventId, ShelterEventEntity shelterEvent)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar el evento con id = " + shelterEventId);
        Optional<ShelterEventEntity> shelterEventEntity = shelterEventRepository.findById(shelterEventId);
        if (shelterEventEntity.isEmpty()) {
            throw new EntityNotFoundException("El evento con el id dado no fue encontrado");
        }

        if (shelterEvent.getShelter() != null) {
            Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterEvent.getShelter().getId());
            if (shelterEntity.isEmpty()) {
                throw new IllegalOperationException("El shelter dado no fue encontrado");
            }
        }

        shelterEvent.setId(shelterEventId);
        log.info("Termina proceso de actualizar el evento con id = " + shelterEventId);
        return shelterEventRepository.save(shelterEvent);
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public void deleteShelterEvent(Long shelterEventId) throws EntityNotFoundException {
        log.info("Inicia proceso de borrar el evento con id = " + shelterEventId);
        Optional<ShelterEventEntity> shelterEventEntity = shelterEventRepository.findById(shelterEventId);
        if (shelterEventEntity.isEmpty()) {
            throw new EntityNotFoundException("El evento con el id dado no fue encontrado");
        }
        shelterEventRepository.deleteById(shelterEventId);
        log.info("Termina proceso de borrar el evento con id = " + shelterEventId);
    }
}