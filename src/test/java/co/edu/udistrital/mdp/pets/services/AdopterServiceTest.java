package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRequestRepository;
import co.edu.udistrital.mdp.pets.repositories.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdopterServiceTest {

    @Mock
    private AdopterRepository adopterRepository;

    @Mock
    private AdoptionRepository adoptionRepository;

    @Mock
    private AdoptionRequestRepository adoptionRequestRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private AdopterService adopterService;

    private AdopterEntity validAdopter;

    @BeforeEach
    void setup() {
        validAdopter = new AdopterEntity();
        validAdopter.setId(1L);
        validAdopter.setAddress("Calle 1 # 2-3");
        validAdopter.setPhone("3001234567");
        validAdopter.setOccupation("Ingeniero");
    }

    // --- CREATE ADOPTER ---
    @Test
    void testCreateAdopterSuccess() {
        Mockito.when(adopterRepository.save(any(AdopterEntity.class))).thenReturn(validAdopter);
        AdopterEntity created = adopterService.createAdopter(validAdopter);
        assertNotNull(created);
    }

    @Test
    void testCreateAdopterMissingAddressFails() {
        validAdopter.setAddress("");
        assertThrows(BusinessLogicException.class, () -> adopterService.createAdopter(validAdopter));
    }

    @Test
    void testCreateAdopterMissingPhoneFails() {
        validAdopter.setPhone(null);
        assertThrows(BusinessLogicException.class, () -> adopterService.createAdopter(validAdopter));
    }

    // --- GET ADOPTERS ---
    @Test
    void testGetAdoptersSuccess() {
        Mockito.when(adopterRepository.findAll()).thenReturn(List.of(validAdopter));
        assertFalse(adopterService.getAdopters().isEmpty());
    }

    @Test
    void testGetAdopterSuccess() {
        Mockito.when(adopterRepository.findById(1L)).thenReturn(Optional.of(validAdopter));
        assertNotNull(adopterService.getAdopter(1L));
    }

    @Test
    void testGetAdopterNotFoundFails() {
        Mockito.when(adopterRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> adopterService.getAdopter(99L));
    }

    // --- UPDATE ADOPTER ---
    @Test
    void testUpdateAdopterSuccess() {
        Mockito.when(adopterRepository.findById(1L)).thenReturn(Optional.of(validAdopter));
        Mockito.when(adoptionRequestRepository.findByAdopterId(1L)).thenReturn(List.of());
        Mockito.when(adopterRepository.save(any(AdopterEntity.class))).thenReturn(validAdopter);
        validAdopter.setOccupation("Médico");
        AdopterEntity updated = adopterService.updateAdopter(1L, validAdopter);
        assertEquals("Médico", updated.getOccupation());
    }

    @Test
    void testUpdateAdopterNotFoundFails() {
        Mockito.when(adopterRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> adopterService.updateAdopter(99L, validAdopter));
    }

    @Test
    void testUpdateAdopterWithPendingRequestFails() {
        Mockito.when(adopterRepository.findById(1L)).thenReturn(Optional.of(validAdopter));
        Mockito.when(adoptionRequestRepository.findByAdopterId(1L))
                .thenReturn(List.of(new AdoptionRequestEntity()));
        assertThrows(BusinessLogicException.class, () -> adopterService.updateAdopter(1L, validAdopter));
    }

    // --- DELETE ADOPTER ---
    @Test
    void testDeleteAdopterSuccess() {
        Mockito.when(adopterRepository.findById(1L)).thenReturn(Optional.of(validAdopter));
        Mockito.when(adoptionRepository.findByAdopterId(1L)).thenReturn(List.of());
        Mockito.when(adoptionRequestRepository.findByAdopterId(1L)).thenReturn(List.of());
        Mockito.when(reviewRepository.findByAdopterId(1L)).thenReturn(List.of());
        assertDoesNotThrow(() -> adopterService.deleteAdopter(1L));
        Mockito.verify(adopterRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAdopterNotFoundFails() {
        Mockito.when(adopterRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> adopterService.deleteAdopter(99L));
    }

    @Test
    void testDeleteAdopterWithAdoptionsFails() {
        Mockito.when(adopterRepository.findById(1L)).thenReturn(Optional.of(validAdopter));
        Mockito.when(adoptionRepository.findByAdopterId(1L)).thenReturn(List.of(new AdoptionEntity()));
        assertThrows(BusinessLogicException.class, () -> adopterService.deleteAdopter(1L));
    }

    @Test
    void testDeleteAdopterWithPendingRequestsFails() {
        Mockito.when(adopterRepository.findById(1L)).thenReturn(Optional.of(validAdopter));
        Mockito.when(adoptionRepository.findByAdopterId(1L)).thenReturn(List.of());
        Mockito.when(adoptionRequestRepository.findByAdopterId(1L))
                .thenReturn(List.of(new AdoptionRequestEntity()));
        assertThrows(BusinessLogicException.class, () -> adopterService.deleteAdopter(1L));
    }

    @Test
    void testDeleteAdopterWithReviewsFails() {
        Mockito.when(adopterRepository.findById(1L)).thenReturn(Optional.of(validAdopter));
        Mockito.when(adoptionRepository.findByAdopterId(1L)).thenReturn(List.of());
        Mockito.when(adoptionRequestRepository.findByAdopterId(1L)).thenReturn(List.of());
        Mockito.when(reviewRepository.findByAdopterId(1L)).thenReturn(List.of(new ReviewEntity()));
        assertThrows(BusinessLogicException.class, () -> adopterService.deleteAdopter(1L));
    }
}