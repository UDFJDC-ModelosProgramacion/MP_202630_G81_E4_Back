package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private PetEntity validPet;

    @BeforeEach
    void setup() {
        validPet = new PetEntity();
        validPet.setId(1L);
        validPet.setName("Firulais");
        validPet.setSpecie("Perro");
        validPet.setBreed("Criollo");
        validPet.setAdoptionStatus("Disponible");
        validPet.setAge(2);
        validPet.setWeight(15.5);
        validPet.setAdmissionDate("2023-01-01");
        validPet.setHealthStatus("Sano");
    }

    // --- CREATE PET ---
    @Test
    void testCreatePetSuccess() {
        Mockito.when(petRepository.save(any(PetEntity.class))).thenReturn(validPet);
        PetEntity created = petService.createPet(validPet);
        assertNotNull(created);
        assertEquals("Firulais", created.getName());
    }

    @Test
    void testCreatePetMissingFieldsFails() {
        validPet.setName("");
        assertThrows(BusinessLogicException.class, () -> petService.createPet(validPet));
    }

    @Test
    void testCreatePetInvalidAgeFails() {
        validPet.setAge(-1);
        assertThrows(BusinessLogicException.class, () -> petService.createPet(validPet));
    }

    @Test
    void testCreatePetInvalidWeightFails() {
        validPet.setWeight(0.0);
        assertThrows(BusinessLogicException.class, () -> petService.createPet(validPet));
    }

    @Test
    void testCreatePetFutureAdmissionDateFails() {
        validPet.setAdmissionDate("2099-01-01");
        assertThrows(BusinessLogicException.class, () -> petService.createPet(validPet));
    }

    // --- GET PETS ---
    @Test
    void testGetPetsSuccess() {
        Mockito.when(petRepository.findAll()).thenReturn(List.of(validPet));
        List<PetEntity> pets = petService.getPets();
        assertFalse(pets.isEmpty());
        assertEquals(1, pets.size());
    }

    @Test
    void testGetPetSuccess() {
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        PetEntity found = petService.getPet(1L);
        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetPetNotFoundFails() {
        Mockito.when(petRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> petService.getPet(99L));
    }

    // --- UPDATE PET ---
    @Test
    void testUpdatePetSuccess() {
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        Mockito.when(petRepository.save(any(PetEntity.class))).thenReturn(validPet);
        validPet.setName("Firulais Actualizado");
        PetEntity updated = petService.updatePet(1L, validPet);
        assertEquals("Firulais Actualizado", updated.getName());
    }

    @Test
    void testUpdatePetSickToAvailableFails() {
        PetEntity existingPet = new PetEntity();
        existingPet.setHealthStatus("Enfermo");

        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(existingPet));
        validPet.setAdoptionStatus("Disponible");

        assertThrows(BusinessLogicException.class, () -> petService.updatePet(1L, validPet));
    }

    @Test
    void testUpdatePetNotFoundFails() {
        Mockito.when(petRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> petService.updatePet(99L, validPet));
    }

    // --- DELETE PET ---
    @Test
    void testDeletePetSuccess() {
        validPet.setAdoptionStatus("Disponible");
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        assertDoesNotThrow(() -> petService.deletePet(1L));
        Mockito.verify(petRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testDeletePetAdoptedFails() {
        validPet.setAdoptionStatus("Adoptado");
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        assertThrows(BusinessLogicException.class, () -> petService.deletePet(1L));
    }

    @Test
    void testDeletePetNotFoundFails() {
        Mockito.when(petRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> petService.deletePet(99L));
    }
}