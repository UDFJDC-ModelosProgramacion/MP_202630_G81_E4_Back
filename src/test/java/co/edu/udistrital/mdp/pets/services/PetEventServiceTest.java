package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.PetEventRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PetEventServiceTest {

    @Mock
    private PetEventRepository eventRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetEventService eventService;

    private PetEventEntity validEvent;
    private PetEntity validPet;

    @BeforeEach
    void setUp() {
        validPet = new PetEntity();
        validPet.setId(1L);
        validPet.setHealthStatus("Sano");

        validEvent = new PetEventEntity();
        validEvent.setId(1L);
        validEvent.setType("Reporte Médico");
        validEvent.setDate(LocalDate.now().toString());
        validEvent.setDescription("Control general, todo en orden.");
    }

    // Prueba con datos correctos para verificar la creación[cite: 1]
    @Test
    void testCreatePetEventSuccess() {
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        Mockito.when(eventRepository.save(any(PetEventEntity.class))).thenReturn(validEvent);
        
        PetEventEntity created = eventService.createPetEvent(1L, validEvent);
        assertNotNull(created);
        assertEquals("Reporte Médico", created.getType());
    }

    // Prueba con datos incorrectos: crear evento para mascota fallecida[cite: 1]
    @Test
    void testCreatePetEventDeadPetFails() {
        validPet.setHealthStatus("Fallecido");
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        
        assertThrows(BusinessLogicException.class, () -> {
            eventService.createPetEvent(1L, validEvent);
        });
    }

    // Prueba con datos incorrectos: fecha futura en reporte médico[cite: 1]
    @Test
    void testCreatePetEventFutureDateFails() {
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        validEvent.setDate(LocalDate.now().plusDays(5).toString()); // Fecha futura
        
        assertThrows(BusinessLogicException.class, () -> {
            eventService.createPetEvent(1L, validEvent);
        });
    }

    // Prueba con datos correctos para verificar la actualización[cite: 1]
    @Test
    void testUpdatePetEventSuccess() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));
        Mockito.when(eventRepository.save(any(PetEventEntity.class))).thenReturn(validEvent);
        
        validEvent.setDescription("Control actualizado.");
        PetEventEntity updated = eventService.updatePetEvent(1L, validEvent);
        
        assertEquals("Control actualizado.", updated.getDescription());
    }

    // Prueba con datos incorrectos: descripción vacía[cite: 1]
    @Test
    void testUpdatePetEventEmptyDescriptionFails() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));
        validEvent.setDescription(""); 
        
        assertThrows(BusinessLogicException.class, () -> {
            eventService.updatePetEvent(1L, validEvent);
        });
    }

    // Prueba para la eliminación correcta[cite: 1]
    @Test
    void testDeletePetEventSuccess() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));
        assertDoesNotThrow(() -> eventService.deletePetEvent(1L));
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(1L);
    }

    // Prueba de caso especial: obtener evento que no existe[cite: 1]
    @Test
    void testGetPetEventNotFound() {
        Mockito.when(eventRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            eventService.getPetEvent(99L);
        });
    }
}