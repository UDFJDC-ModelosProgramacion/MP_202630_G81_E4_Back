package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.PetEventRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PetEventService {

    @Autowired
    private PetEventRepository eventRepository;

    @Autowired
    private PetRepository petRepository;

    @Transactional
    public PetEventEntity createPetEvent(Long petId, PetEventEntity event) {
        Optional<PetEntity> petOpt = petRepository.findById(petId);
        if (petOpt.isEmpty()) {
            throw new EntityNotFoundException("La mascota con ID " + petId + " no existe.");
        }
        
        PetEntity pet = petOpt.get();
        if ("Fallecido".equalsIgnoreCase(pet.getHealthStatus())) {
            throw new BusinessLogicException("No se pueden asociar eventos a una mascota fallecida.");
        }

        validateEventData(event);
        return eventRepository.save(event);
    }

    public List<PetEventEntity> getPetEvents() {
        return eventRepository.findAll();
    }

    public PetEventEntity getPetEvent(Long id) {
        Optional<PetEventEntity> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("El evento con ID " + id + " no existe.");
        }
        return event.get();
    }

    @Transactional
    public PetEventEntity updatePetEvent(Long id, PetEventEntity event) {
        getPetEvent(id); // Valida que exista
        validateEventData(event);
        
        event.setId(id);
        return eventRepository.save(event);
    }

    @Transactional
    public void deletePetEvent(Long id) {
        getPetEvent(id); // Valida que exista
        eventRepository.deleteById(id);
    }

    private void validateEventData(PetEventEntity event) {
        if (event.getType() == null || event.getType().trim().isEmpty() ||
            event.getDate() == null || event.getDate().trim().isEmpty() ||
            event.getDescription() == null || event.getDescription().trim().isEmpty()) {
            throw new BusinessLogicException("El tipo de evento, la fecha y la descripción son obligatorios.");
        }

        if ("Reporte Médico".equalsIgnoreCase(event.getType()) || "Incidente".equalsIgnoreCase(event.getType())) {
            try {
                LocalDate date = LocalDate.parse(event.getDate());
                if (date.isAfter(LocalDate.now())) {
                    throw new BusinessLogicException("La fecha de un reporte médico o incidente no puede ser futura.");
                }
            } catch (Exception e) {
                if (e instanceof BusinessLogicException) throw e;
                throw new BusinessLogicException("Formato de fecha inválido. Use YYYY-MM-DD.");
            }
        }
    }
}