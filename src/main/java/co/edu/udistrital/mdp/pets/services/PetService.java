package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

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
        return petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + id + " no existe."));
    }

    @Transactional
    public PetEntity updatePet(Long id, PetEntity pet) {
        PetEntity existingPet = getPet(id);
        validatePetIntegrity(pet);
        validateAdmissionDate(pet.getAdmissionDate());

        if ("Disponible".equalsIgnoreCase(pet.getAdoptionStatus()) &&
            ("Enfermo".equalsIgnoreCase(existingPet.getHealthStatus()) || "En tratamiento".equalsIgnoreCase(existingPet.getHealthStatus()))) {
            throw new BusinessLogicException("No se puede marcar como Disponible a una mascota enferma o en tratamiento.");
        }

        pet.setId(id);
        return petRepository.save(pet);
    }

    @Transactional
    public void deletePet(Long id) {
        PetEntity pet = getPet(id);
        if ("Adoptado".equalsIgnoreCase(pet.getAdoptionStatus())) {
            throw new BusinessLogicException("No se puede eliminar una mascota que ya ha sido adoptada.");
        }
        petRepository.deleteById(id);
    }

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
        if (admissionDateStr == null || admissionDateStr.isEmpty()) {
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(admissionDateStr);
        } catch (DateTimeParseException e) {
            throw new BusinessLogicException("Formato de fecha inválido. Use YYYY-MM-DD.");
        }

        if (date.isAfter(LocalDate.now())) {
            throw new BusinessLogicException("La fecha de ingreso no puede ser una fecha futura.");
        }
    }
}