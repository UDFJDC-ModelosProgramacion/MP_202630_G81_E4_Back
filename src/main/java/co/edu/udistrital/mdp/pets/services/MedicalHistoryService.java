package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.MedicalHistoryRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository historyRepository;

    @Autowired
    private PetRepository petRepository;

    @Transactional
    public MedicalHistoryEntity createMedicalHistory(Long petId, MedicalHistoryEntity history) {
        if (!petRepository.existsById(petId)) {
            throw new EntityNotFoundException("La mascota con ID " + petId + " no existe.");
        }
        
        if (history.getSterilized() == null) {
            throw new BusinessLogicException("El campo de esterilización es obligatorio.");
        }
        
        return historyRepository.save(history);
    }

    public List<MedicalHistoryEntity> getMedicalHistories() {
        return historyRepository.findAll();
    }

    public MedicalHistoryEntity getMedicalHistory(Long id) {
        Optional<MedicalHistoryEntity> history = historyRepository.findById(id);
        if (history.isEmpty()) {
            throw new EntityNotFoundException("El historial médico con ID " + id + " no existe.");
        }
        return history.get();
    }

    @Transactional
    public MedicalHistoryEntity updateMedicalHistory(Long id, MedicalHistoryEntity history) {
        MedicalHistoryEntity existingHistory = getMedicalHistory(id);

        if (history.getSterilized() == null) {
            throw new BusinessLogicException("El campo de esterilización es obligatorio.");
        }

        // Regla de Negocio: No se puede des-esterilizar
        if (Boolean.TRUE.equals(existingHistory.getSterilized()) && Boolean.FALSE.equals(history.getSterilized())) {
            throw new BusinessLogicException("Una mascota esterilizada no puede cambiar su estado a no esterilizada.");
        }

        // Regla de Negocio: Si hay enfermedad, el tratamiento es obligatorio
        if (history.getDiseases() != null && !history.getDiseases().trim().isEmpty() && 
           (history.getTreatment() == null || history.getTreatment().trim().isEmpty())) {
            throw new BusinessLogicException("Si se registra una enfermedad, se debe registrar un tratamiento.");
        }

        history.setId(id);
        return historyRepository.save(history);
    }

    @Transactional
    public void deleteMedicalHistory(Long id) {
        MedicalHistoryEntity history = getMedicalHistory(id);
        historyRepository.deleteById(id);
    }
}