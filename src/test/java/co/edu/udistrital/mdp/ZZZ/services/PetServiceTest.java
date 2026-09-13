package co.edu.udistrital.mdp.ZZZ.services;

import co.edu.udistrital.mdp.ZZZ.entities.PetEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.ZZZ.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private PetEntity validPet;

    @BeforeEach
    void setUp() {
        validPet = new PetEntity();
        validPet.setId(1L);
        validPet.setName("Firulais");
        validPet.setSpecie("Perro");
        validPet.setBreed("Criollo");
        validPet.setAdoptionStatus("Disponible");
        validPet.setAge(2);
        validPet.setWeight(15.5);
        validPet.setAdmissionDate("2023-01-01"); // Fecha pasada
        validPet.setHealthStatus("Sano");
    }

    // --- PRUEBAS DE CREACIÓN ---
    @Test
    void testCreatePetSuccess() {
        Mockito.when(petRepository.save(any(PetEntity.class))).thenReturn(validPet);
        PetEntity created = petService.createPet(validPet);
        assertNotNull(created);
        assertEquals("Firulais", created.getName());
    }

    @Test
    void testCreatePetInvalidAgeFails() {
        validPet.setAge(-1); // Regla rota
        assertThrows(BusinessLogicException.class, () -> {
            petService.createPet(validPet);
        });
    }

    // --- PRUEBAS DE ACTUALIZACIÓN ---
    @Test
    void testUpdatePetSuccess() {
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        Mockito.when(petRepository.save(any(PetEntity.class))).thenReturn(validPet);
        
        validPet.setName("Firulais Actualizado");
        PetEntity updated = petService.updatePet(1L, validPet);
        
        assertEquals("Firulais Actualizado", updated.getName());
    }

    @Test
    void testUpdatePetStatusFails() {
        // Configuramos la mascota existente como Enferma
        PetEntity existingPet = new PetEntity();
        existingPet.setHealthStatus("Enfermo");
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(existingPet));

        validPet.setAdoptionStatus("Disponible"); // Regla rota: intenta ponerla disponible estando enferma

        assertThrows(BusinessLogicException.class, () -> {
            petService.updatePet(1L, validPet);
        });
    }

    // --- PRUEBAS DE ELIMINACIÓN ---
    @Test
    void testDeletePetSuccess() {
        validPet.setAdoptionStatus("Disponible");
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        
        assertDoesNotThrow(() -> petService.deletePet(1L));
        Mockito.verify(petRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testDeletePetAdoptedFails() {
        validPet.setAdoptionStatus("Adoptado"); // Regla rota
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));

        assertThrows(BusinessLogicException.class, () -> {
            petService.deletePet(1L);
        });
    }

    // --- PRUEBAS CASOS ESPECIALES (NO EXISTE) ---
    @Test
    void testGetPetNotFound() {
        Mockito.when(petRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            petService.getPet(99L);
        });
    }
    
    @Test
    void testUpdatePetNotFound() {
        Mockito.when(petRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            petService.updatePet(99L, validPet);
        });
    }
}