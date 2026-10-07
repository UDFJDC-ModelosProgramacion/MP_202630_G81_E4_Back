package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.MedicalHistoryRepository;
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
public class MedicalHistoryServiceTest {

    @Mock
    private MedicalHistoryRepository historyRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private MedicalHistoryService historyService;

    private MedicalHistoryEntity validHistory;
    private PetEntity validPet;

    @BeforeEach
    void setup() {
        validPet = new PetEntity();
        validPet.setId(1L);

        validHistory = new MedicalHistoryEntity();
        validHistory.setId(1L);
        validHistory.setSterilized(true);
        validHistory.setDiseases("Gastroenteritis");
        validHistory.setTreatment("Antibióticos");
        validHistory.setPet(validPet);
    }

    // --- CREATE ---
    @Test
    void testCreateMedicalHistorySuccess() {
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        Mockito.when(historyRepository.save(any(MedicalHistoryEntity.class))).thenReturn(validHistory);

        MedicalHistoryEntity created = historyService.createMedicalHistory(1L, validHistory);
        assertNotNull(created);
        assertTrue(created.getSterilized());
    }

    @Test
    void testCreateMedicalHistoryPetNotFoundFails() {
        Mockito.when(petRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> historyService.createMedicalHistory(99L, validHistory));
    }

    @Test
    void testCreateMedicalHistoryNullSterilizedFails() {
        Mockito.when(petRepository.findById(1L)).thenReturn(Optional.of(validPet));
        validHistory.setSterilized(null);
        assertThrows(BusinessLogicException.class, () -> historyService.createMedicalHistory(1L, validHistory));
    }

    // --- GET ---
    @Test
    void testGetMedicalHistoriesSuccess() {
        Mockito.when(historyRepository.findAll()).thenReturn(List.of(validHistory));
        List<MedicalHistoryEntity> list = historyService.getMedicalHistories();
        assertEquals(1, list.size());
    }

    @Test
    void testGetMedicalHistoryNotFoundFails() {
        Mockito.when(historyRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> historyService.getMedicalHistory(99L));
    }

    // --- UPDATE ---
    @Test
    void testUpdateMedicalHistorySuccess() {
        Mockito.when(historyRepository.findById(1L)).thenReturn(Optional.of(validHistory));
        Mockito.when(historyRepository.save(any(MedicalHistoryEntity.class))).thenReturn(validHistory);

        validHistory.setTreatment("Tratamiento finalizado");
        MedicalHistoryEntity updated = historyService.updateMedicalHistory(1L, validHistory);
        assertEquals("Tratamiento finalizado", updated.getTreatment());
    }

    @Test
    void testUpdateMedicalHistorySterilizedTransitionFails() {
        Mockito.when(historyRepository.findById(1L)).thenReturn(Optional.of(validHistory));

        MedicalHistoryEntity updatedHistory = new MedicalHistoryEntity();
        updatedHistory.setSterilized(false);

        assertThrows(BusinessLogicException.class, () -> historyService.updateMedicalHistory(1L, updatedHistory));
    }

    @Test
    void testUpdateMedicalHistoryDiseaseWithoutTreatmentFails() {
        Mockito.when(historyRepository.findById(1L)).thenReturn(Optional.of(validHistory));

        validHistory.setDiseases("Otitis");
        validHistory.setTreatment("");

        assertThrows(BusinessLogicException.class, () -> historyService.updateMedicalHistory(1L, validHistory));
    }

    // --- DELETE ---
    @Test
    void testDeleteMedicalHistorySuccess() {
        Mockito.when(historyRepository.findById(1L)).thenReturn(Optional.of(validHistory));
        assertDoesNotThrow(() -> historyService.deleteMedicalHistory(1L));
        Mockito.verify(historyRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMedicalHistoryNotFoundFails() {
        Mockito.when(historyRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> historyService.deleteMedicalHistory(99L));
    }
}