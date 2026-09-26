package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
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

    @BeforeEach
    void setUp() {
        validHistory = new MedicalHistoryEntity();
        validHistory.setId(1L);
        validHistory.setSterilized(true);
        validHistory.setDiseases("Gastroenteritis");
        validHistory.setTreatment("Antibióticos y dieta blanda");
    }

    // Prueba con datos correctos para verificar la creación exitosa[cite: 1]
    @Test
    void testCreateMedicalHistorySuccess() {
        Mockito.when(petRepository.existsById(1L)).thenReturn(true);
        Mockito.when(historyRepository.save(any(MedicalHistoryEntity.class))).thenReturn(validHistory);
        
        MedicalHistoryEntity created = historyService.createMedicalHistory(1L, validHistory);
        assertNotNull(created);
        assertTrue(created.getSterilized());
    }

    // Prueba de caso especial: crear con una mascota que no existe[cite: 1]
    @Test
    void testCreateMedicalHistoryPetNotFoundFails() {
        Mockito.when(petRepository.existsById(99L)).thenReturn(false);
        
        assertThrows(EntityNotFoundException.class, () -> {
            historyService.createMedicalHistory(99L, validHistory);
        });
    }

    // Prueba con datos incorrectos: esterilización nula[cite: 1]
    @Test
    void testCreateMedicalHistoryNullSterilizedFails() {
        Mockito.when(petRepository.existsById(1L)).thenReturn(true);
        validHistory.setSterilized(null);
        
        assertThrows(BusinessLogicException.class, () -> {
            historyService.createMedicalHistory(1L, validHistory);
        });
    }

    // Prueba con datos correctos para verificar la modificación[cite: 1]
    @Test
    void testUpdateMedicalHistorySuccess() {
        Mockito.when(historyRepository.findById(1L)).thenReturn(Optional.of(validHistory));
        Mockito.when(historyRepository.save(any(MedicalHistoryEntity.class))).thenReturn(validHistory);
        
        validHistory.setTreatment("Tratamiento finalizado");
        MedicalHistoryEntity updated = historyService.updateMedicalHistory(1L, validHistory);
        
        assertEquals("Tratamiento finalizado", updated.getTreatment());
    }

    // Prueba con datos incorrectos: transición inválida de esterilizado a no esterilizado[cite: 1]
    @Test
    void testUpdateMedicalHistorySterilizedTransitionFails() {
        Mockito.when(historyRepository.findById(1L)).thenReturn(Optional.of(validHistory));
        
        MedicalHistoryEntity updatedHistory = new MedicalHistoryEntity();
        updatedHistory.setSterilized(false); // Intenta cambiar de true (existente) a false
        
        assertThrows(BusinessLogicException.class, () -> {
            historyService.updateMedicalHistory(1L, updatedHistory);
        });
    }

    // Prueba con datos incorrectos: enfermedad sin tratamiento[cite: 1]
    @Test
    void testUpdateMedicalHistoryDiseaseWithoutTreatmentFails() {
        Mockito.when(historyRepository.findById(1L)).thenReturn(Optional.of(validHistory));
        
        validHistory.setDiseases("Otitis");
        validHistory.setTreatment(""); // Regla rota
        
        assertThrows(BusinessLogicException.class, () -> {
            historyService.updateMedicalHistory(1L, validHistory);
        });
    }

    // Prueba para validar la excepción al buscar una entidad que no existe[cite: 1]
    @Test
    void testGetMedicalHistoryNotFound() {
        Mockito.when(historyRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            historyService.getMedicalHistory(99L);
        });
    }
}