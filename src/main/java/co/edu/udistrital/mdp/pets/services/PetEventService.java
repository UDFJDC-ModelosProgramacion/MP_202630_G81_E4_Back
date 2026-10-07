package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.PetEventRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetEventService {

    private final PetEventRepository eventRepository;

    private final PetRepository petRepository;

    @Transactional
    public PetEventEntity createPetEvent(Long petId, PetEventEntity event) {
        PetEntity pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + petId + " no existe."));

        if ("Fallecido".equalsIgnoreCase(pet.getHealthStatus())) {
            throw new BusinessLogicException("No se pueden asociar eventos a una mascota fallecida.");
        }

        validateEventData(event);
        event.setPet(pet);
        return eventRepository.save(event);
    }

    public List<PetEventEntity> getPetEvents() {
        return eventRepository.findAll();
    }

    public PetEventEntity getPetEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El evento con ID " + id + " no existe."));
    }

    @Transactional
    public PetEventEntity updatePetEvent(Long id, PetEventEntity event) {
        PetEventEntity existingEvent = getPetEvent(id);
        validateEventData(event);
        event.setId(id);
        event.setPet(existingEvent.getPet());
        return eventRepository.save(event);
    }

    @Transactional
    public void deletePetEvent(Long id) {
        getPetEvent(id);
        eventRepository.deleteById(id);
    }

    private void validateEventData(PetEventEntity event) {
        if (event.getType() == null || event.getType().trim().isEmpty() ||
            event.getDate() == null || event.getDate().trim().isEmpty() ||
            event.getDescription() == null || event.getDescription().trim().isEmpty()) {
            throw new BusinessLogicException("El tipo de evento, la fecha y la descripción son obligatorios.");
        }

        if ("Reporte Médico".equalsIgnoreCase(event.getType()) || "Incidente".equalsIgnoreCase(event.getType())) {
            LocalDate date;
            try {
                date = LocalDate.parse(event.getDate());
            } catch (DateTimeParseException e) {
                throw new BusinessLogicException("Formato de fecha inválido. Use YYYY-MM-DD.");
            }

            if (date.isAfter(LocalDate.now())) {
                throw new BusinessLogicException("La fecha de un reporte médico o incidente no puede ser futura.");
            }
        }
    }
}