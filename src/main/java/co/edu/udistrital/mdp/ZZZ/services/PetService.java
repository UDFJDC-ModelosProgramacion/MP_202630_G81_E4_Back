package co.edu.udistrital.mdp.ZZZ.services;

import co.edu.udistrital.mdp.ZZZ.entities.PetEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.ZZZ.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Transactional
    public PetEntity createPet(PetEntity pet) {
        validatePetIntegrity(pet);
        validateAdmissionDate(pet.getAdmissionDate());
        return petRepository.save(pet);
    }

    public List<PetEntity> getPets() {
        return petRepository.findAll();
    }

    public PetEntity getPet(Long id) {
        Optional<PetEntity> pet = petRepository.findById(id);
        if (pet.isEmpty()) {
            throw new EntityNotFoundException("La mascota con ID " + id + " no existe.");
        }
        return pet.get();
    }

    @Transactional
    public PetEntity updatePet(Long id, PetEntity pet) {
        PetEntity existingPet = getPet(id); // Valida si existe, si no lanza excepción
        
        validatePetIntegrity(pet);
        validateAdmissionDate(pet.getAdmissionDate());

        // Regla de Negocio: No pasar a "Disponible" si está "Enfermo"
        if ("Disponible".equalsIgnoreCase(pet.getAdoptionStatus()) && 
            ("Enfermo".equalsIgnoreCase(existingPet.getHealthStatus()) || "En tratamiento".equalsIgnoreCase(existingPet.getHealthStatus()))) {
            throw new BusinessLogicException("No se puede marcar como Disponible a una mascota enferma o en tratamiento.");
        }

        pet.setId(id);
        return petRepository.save(pet);
    }

    @Transactional
    public void deletePet(Long id) {
        PetEntity pet = getPet(id); // Valida si existe
        
        // Regla de Negocio: No eliminar si está Adoptado
        if ("Adoptado".equalsIgnoreCase(pet.getAdoptionStatus())) {
            throw new BusinessLogicException("No se puede eliminar una mascota que ya ha sido adoptada.");
        }
        
        petRepository.deleteById(id);
    }

    // --- Métodos Privados de Validación ---

    private void validatePetIntegrity(PetEntity pet) {
        if (pet.getName() == null || pet.getName().trim().isEmpty() ||
            pet.getSpecie() == null || pet.getSpecie().trim().isEmpty() ||
            pet.getBreed() == null || pet.getBreed().trim().isEmpty() ||
            pet.getAdoptionStatus() == null || pet.getAdoptionStatus().trim().isEmpty()) {
            throw new BusinessLogicException("Los campos nombre, especie, raza y estado de adopción son obligatorios.");
        }
        
        if (pet.getAge() == null || pet.getAge() < 0) {
            throw new BusinessLogicException("La edad debe ser un número entero mayor o igual a 0.");
        }
        
        if (pet.getWeight() == null || pet.getWeight() <= 0) {
            throw new BusinessLogicException("El peso debe ser mayor a 0.");
        }
    }

    private void validateAdmissionDate(String admissionDateStr) {
        if (admissionDateStr != null && !admissionDateStr.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(admissionDateStr);
                if (date.isAfter(LocalDate.now())) {
                    throw new BusinessLogicException("La fecha de ingreso no puede ser una fecha futura.");
                }
            } catch (Exception e) {
                if (e instanceof BusinessLogicException) throw e;
                throw new BusinessLogicException("Formato de fecha inválido. Use YYYY-MM-DD.");
            }
        }
    }
}