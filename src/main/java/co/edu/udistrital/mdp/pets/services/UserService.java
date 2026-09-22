package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.NotificationRepository;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public UserEntity createUser(UserEntity user) {
        validateUserIntegrity(user);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessLogicException("Ya existe un usuario registrado con ese email.");
        }
        return userRepository.save(user);
    }

    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    public UserEntity getUser(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("El usuario con ID " + id + " no existe.");
        }
        return user.get();
    }

    @Transactional
    public UserEntity updateUser(Long id, UserEntity user) {
        getUser(id); // valida que exista
        validateUserIntegrity(user);

        Optional<UserEntity> userWithSameEmail = userRepository.findByEmail(user.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
            throw new BusinessLogicException("Ya existe otro usuario registrado con ese email.");
        }

        user.setId(id);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        getUser(id); // valida que exista

        if (!notificationRepository.findByUserIdAndReadFalse(id).isEmpty()) {
            throw new BusinessLogicException("No se puede eliminar un usuario que tiene notificaciones sin leer.");
        }

        userRepository.deleteById(id);
    }

    private void validateUserIntegrity(UserEntity user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new BusinessLogicException("El nombre del usuario es obligatorio.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BusinessLogicException("El email del usuario es obligatorio.");
        }
        if (!user.getEmail().matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new BusinessLogicException("El email no tiene un formato válido.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BusinessLogicException("La contraseña del usuario es obligatoria.");
        }
    }
}