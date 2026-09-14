package co.edu.udistrital.mdp.ZZZ.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.ZZZ.entities.ShelterEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.ZZZ.repositories.ShelterRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ShelterService {

    private static final Logger log = LoggerFactory.getLogger(ShelterService.class);

    private final ShelterRepository shelterRepository;

    @Transactional(rollbackFor = { IllegalOperationException.class })
    public ShelterEntity createShelter(ShelterEntity shelterEntity) throws IllegalOperationException {
        log.info("Inicia proceso de creación del shelter");

        if (shelterEntity.getNit() == null) {
            throw new IllegalOperationException("El NIT del shelter es obligatorio");
        }

        if (!shelterRepository.findByNit(shelterEntity.getNit()).isEmpty()) {
            throw new IllegalOperationException("Ya existe un shelter con ese NIT");
        }

        if (shelterEntity.getCity() == null || shelterEntity.getCity().isEmpty()) {
            throw new IllegalOperationException("La ciudad del shelter es obligatoria");
        }

        log.info("Termina proceso de creación del shelter");
        return shelterRepository.save(shelterEntity);
    }

    @Transactional
    public List<ShelterEntity> getShelters() {
        log.info("Inicia proceso de consultar todos los shelters");
        return shelterRepository.findAll();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public ShelterEntity getShelter(Long shelterId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar el shelter con id = " + shelterId);
        Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterId);
        if (shelterEntity.isEmpty()) {
            throw new EntityNotFoundException("El shelter con el id dado no fue encontrado");
        }
        log.info("Termina proceso de consultar el shelter con id = " + shelterId);
        return shelterEntity.get();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ShelterEntity updateShelter(Long shelterId, ShelterEntity shelter)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar el shelter con id = " + shelterId);
        Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterId);
        if (shelterEntity.isEmpty()) {
            throw new EntityNotFoundException("El shelter con el id dado no fue encontrado");
        }

        Optional<ShelterEntity> shelterWithSameNit = shelterRepository.findByNit(shelter.getNit());
        if (!shelterWithSameNit.isEmpty() && !shelterWithSameNit.get().getId().equals(shelterId)) {
            throw new IllegalOperationException("Ya existe otro shelter con ese NIT");
        }

        shelter.setId(shelterId);
        log.info("Termina proceso de actualizar el shelter con id = " + shelterId);
        return shelterRepository.save(shelter);
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public void deleteShelter(Long shelterId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar el shelter con id = " + shelterId);
        Optional<ShelterEntity> shelterEntityOptional = shelterRepository.findById(shelterId);
        if (shelterEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("El shelter con el id dado no fue encontrado");
        }

        ShelterEntity shelterEntity = shelterEntityOptional.get();

        if (!shelterEntity.getVeterinarians().isEmpty()) {
            throw new IllegalOperationException("No se puede eliminar el shelter porque tiene veterinarios asociados");
        }

        if (!shelterEntity.getShelterEvents().isEmpty()) {
            throw new IllegalOperationException("No se puede eliminar el shelter porque tiene eventos asociados");
        }

        if (!shelterEntity.getPets().isEmpty()) {
            throw new IllegalOperationException("No se puede eliminar el shelter porque tiene mascotas registradas");
        }

        shelterRepository.deleteById(shelterId);
        log.info("Termina proceso de borrar el shelter con id = " + shelterId);
    }
}