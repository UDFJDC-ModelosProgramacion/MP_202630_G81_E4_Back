package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.MedicalHistoryRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicalHistoryService {

    private final MedicalHistoryRepository historyRepository;

    private final PetRepository petRepository;

    @Transactional
    public MedicalHistoryEntity createMedicalHistory(Long petId, MedicalHistoryEntity history) {
        PetEntity pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + petId + " no existe."));

        if (history.getSterilized() == null) {
            throw new BusinessLogicException("El campo de esterilización es obligatorio.");
        }

        history.setPet(pet);
        return historyRepository.save(history);
    }

    public List<MedicalHistoryEntity> getMedicalHistories() {
        return historyRepository.findAll();
    }

    public MedicalHistoryEntity getMedicalHistory(Long id) {
        return historyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El historial médico con ID " + id + " no existe."));
    }

    @Transactional
    public MedicalHistoryEntity updateMedicalHistory(Long id, MedicalHistoryEntity history) {
        MedicalHistoryEntity existingHistory = getMedicalHistory(id);

        if (history.getSterilized() == null) {
            throw new BusinessLogicException("El campo de esterilización es obligatorio.");
        }

        if (Boolean.TRUE.equals(existingHistory.getSterilized()) && Boolean.FALSE.equals(history.getSterilized())) {
            throw new BusinessLogicException("Una mascota esterilizada no puede cambiar su estado a no esterilizada.");
        }

        if (history.getDiseases() != null && !history.getDiseases().trim().isEmpty() &&
            (history.getTreatment() == null || history.getTreatment().trim().isEmpty())) {
            throw new BusinessLogicException("Si se registra una enfermedad, se debe registrar un tratamiento.");
        }

        history.setId(id);
        history.setPet(existingHistory.getPet());
        return historyRepository.save(history);
    }

    @Transactional
    public void deleteMedicalHistory(Long id) {
        getMedicalHistory(id);
        historyRepository.deleteById(id);
    }
}