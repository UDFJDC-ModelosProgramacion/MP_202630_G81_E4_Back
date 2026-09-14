package co.edu.udistrital.mdp.ZZZ.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.ZZZ.entities.AdopterEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.ZZZ.repositories.AdopterRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.AdoptionRequestRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AdopterService {

    @Autowired
    private AdopterRepository adopterRepository;

    @Autowired
    private AdoptionRepository adoptionRepository;

    @Autowired
    private AdoptionRequestRepository adoptionRequestRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional
    public AdopterEntity createAdopter(AdopterEntity adopter) {
        validateAdopterIntegrity(adopter);
        return adopterRepository.save(adopter);
    }

    public List<AdopterEntity> getAdopters() {
        return adopterRepository.findAll();
    }

    public AdopterEntity getAdopter(Long id) {
        Optional<AdopterEntity> adopter = adopterRepository.findById(id);
        if (adopter.isEmpty()) {
            throw new EntityNotFoundException("El adoptante con ID " + id + " no existe.");
        }
        return adopter.get();
    }

    @Transactional
    public AdopterEntity updateAdopter(Long id, AdopterEntity adopter) {
        getAdopter(id); // valida que exista
        validateAdopterIntegrity(adopter);

        if (!adoptionRequestRepository.findByAdopterId(id).isEmpty()) {
            throw new BusinessLogicException("No se puede modificar la dirección de un adoptante con solicitudes de adopción en curso.");
        }

        adopter.setId(id);
        return adopterRepository.save(adopter);
    }

    @Transactional
    public void deleteAdopter(Long id) {
        getAdopter(id); // valida que exista

        if (!adoptionRepository.findByAdopterId(id).isEmpty()) {
            throw new BusinessLogicException("No se puede eliminar un adoptante que tiene adopciones registradas.");
        }
        if (!adoptionRequestRepository.findByAdopterId(id).isEmpty()) {
            throw new BusinessLogicException("No se puede eliminar un adoptante que tiene solicitudes de adopción pendientes.");
        }
        if (!reviewRepository.findByAdopterId(id).isEmpty()) {
            throw new BusinessLogicException("No se puede eliminar un adoptante que tiene reseñas registradas.");
        }

        adopterRepository.deleteById(id);
    }

    private void validateAdopterIntegrity(AdopterEntity adopter) {
        if (adopter.getAddress() == null || adopter.getAddress().trim().isEmpty()) {
            throw new BusinessLogicException("La dirección del adoptante es obligatoria.");
        }
        if (adopter.getPhone() == null || adopter.getPhone().trim().isEmpty()) {
            throw new BusinessLogicException("El teléfono del adoptante es obligatorio.");
        }
    }
}