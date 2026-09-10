package co.edu.udistrital.mdp.ZZZ.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.ZZZ.entities.ShelterEntity;
import co.edu.udistrital.mdp.ZZZ.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.ZZZ.repositories.ShelterRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.VeterinarianRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VeterinarianService {

    private static final Logger log = LoggerFactory.getLogger(VeterinarianService.class);

    private final VeterinarianRepository veterinarianRepository;
    private final ShelterRepository shelterRepository;

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public VeterinarianEntity createVeterinarian(Long shelterId, VeterinarianEntity veterinarianEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación del veterinario");

        Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterId);
        if (shelterEntity.isEmpty()) {
            throw new EntityNotFoundException("El shelter con el id dado no fue encontrado");
        }

        if (veterinarianEntity.getVeterinarianId() == null) {
            throw new IllegalOperationException("El id del veterinario es obligatorio");
        }

        if (!veterinarianRepository.findByVeterinarianId(veterinarianEntity.getVeterinarianId()).isEmpty()) {
            throw new IllegalOperationException("Ya existe un veterinario con ese veterinarianId");
        }

        if (veterinarianEntity.getSpecialty() == null || veterinarianEntity.getSpecialty().isEmpty()) {
            throw new IllegalOperationException("La especialidad del veterinario es obligatoria");
        }

        veterinarianEntity.setShelter(shelterEntity.get());
        log.info("Termina proceso de creación del veterinario");
        return veterinarianRepository.save(veterinarianEntity);
    }

    @Transactional
    public List<VeterinarianEntity> getVeterinarians() {
        log.info("Inicia proceso de consultar todos los veterinarios");
        return veterinarianRepository.findAll();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public VeterinarianEntity getVeterinarian(Long veterinarianId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar el veterinario con id = " + veterinarianId);
        Optional<VeterinarianEntity> veterinarianEntity = veterinarianRepository.findById(veterinarianId);
        if (veterinarianEntity.isEmpty()) {
            throw new EntityNotFoundException("El veterinario con el id dado no fue encontrado");
        }
        log.info("Termina proceso de consultar el veterinario con id = " + veterinarianId);
        return veterinarianEntity.get();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public VeterinarianEntity updateVeterinarian(Long veterinarianId, VeterinarianEntity veterinarian)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar el veterinario con id = " + veterinarianId);
        Optional<VeterinarianEntity> veterinarianEntity = veterinarianRepository.findById(veterinarianId);
        if (veterinarianEntity.isEmpty()) {
            throw new EntityNotFoundException("El veterinario con el id dado no fue encontrado");
        }

        if (veterinarian.getShelter() != null) {
            Optional<ShelterEntity> shelterEntity = shelterRepository.findById(veterinarian.getShelter().getId());
            if (shelterEntity.isEmpty()) {
                throw new IllegalOperationException("El shelter dado no fue encontrado");
            }
        }

        veterinarian.setId(veterinarianId);
        log.info("Termina proceso de actualizar el veterinario con id = " + veterinarianId);
        return veterinarianRepository.save(veterinarian);
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public void deleteVeterinarian(Long veterinarianId) throws EntityNotFoundException {
        log.info("Inicia proceso de borrar el veterinario con id = " + veterinarianId);
        Optional<VeterinarianEntity> veterinarianEntity = veterinarianRepository.findById(veterinarianId);
        if (veterinarianEntity.isEmpty()) {
            throw new EntityNotFoundException("El veterinario con el id dado no fue encontrado");
        }
        veterinarianRepository.deleteById(veterinarianId);
        log.info("Termina proceso de borrar el veterinario con id = " + veterinarianId);
    }
}