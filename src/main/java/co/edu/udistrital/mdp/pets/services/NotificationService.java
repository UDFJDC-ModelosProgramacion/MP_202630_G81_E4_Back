package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationStrategy;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.NotificationRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor 
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    private final UserRepository userRepository;
   
    private final AdoptionRepository adoptionRepository;
    
    private final PetRepository petRepository;

    private final Map<String, NotificationStrategy> strategies;

    private String inexistentNotificationMessage = "Notification does not exist";


    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotifications() {
        log.info("Inicia proceso de consulta de todas las notificaciones");
        return notificationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public NotificationEntity getNotification(Long notificationId) throws EntityNotFoundException {
        log.info("Inicia proceso de consulta de la notificación con id = {}", notificationId);
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(inexistentNotificationMessage));
    }

    @Transactional(readOnly = true)
public List<NotificationEntity> getNotificationsByUser(Long userId) throws EntityNotFoundException {
    log.info("Inicia proceso de consulta de las notificaciones del usuario con id = {}", userId);
    if (!userRepository.existsById(userId))
        throw new EntityNotFoundException("User does not exist");
    return notificationRepository.findByUserId(userId);
}

@Transactional(readOnly = true)
public List<NotificationEntity> getNotificationsByAdoption(Long adoptionId) throws EntityNotFoundException {
    log.info("Inicia proceso de consulta de las notificaciones de la adopción con id = {}", adoptionId);
    if (!adoptionRepository.existsById(adoptionId))
        throw new EntityNotFoundException("Adoption does not exist");
    return notificationRepository.findByAdoptionId(adoptionId);
}

@Transactional(readOnly = true)
public List<NotificationEntity> getNotificationsByPet(Long petId) throws EntityNotFoundException {
    log.info("Inicia proceso de consulta de las notificaciones de la mascota con id = {}", petId);
    if (!petRepository.existsById(petId))
        throw new EntityNotFoundException("Pet does not exist");
    return notificationRepository.findByPetId(petId);
}
    
    @Transactional
    public NotificationEntity createNotification(NotificationEntity notification) throws IllegalOperationException {
        log.info("Inicia proceso de creación de la notificación");
        if(notification.getContent() == null || notification.getContent().isEmpty())
            throw new IllegalOperationException("Notification content cannot be null or empty");

        String channel = notification.getChannel();

        if (channel == null || !strategies.containsKey(channel))
            throw new IllegalOperationException("Notification channel is not valid or not supported");

        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Transactional
    public NotificationEntity updateNotification(NotificationEntity notification, Long requestingUserId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Inicia proceso de actualización de la notificación");
        if (notification.getId() == null || !notificationRepository.existsById(notification.getId()))
            throw new EntityNotFoundException(inexistentNotificationMessage);

        if(!requestingUserId.equals(notification.getUser().getId()))
            throw new IllegalOperationException("User is not authorized to update this notification");

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(NotificationEntity notification, Long requestingUserId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Inicia proceso de eliminación de la notificación");
        
        if (!notificationRepository.existsById(notification.getId()))
            throw new EntityNotFoundException(inexistentNotificationMessage);

        if(!requestingUserId.equals(notification.getUser().getId()))
            throw new IllegalOperationException("User is not authorized to delete this notification");

        if(!notification.isRead())
            throw new IllegalOperationException("Notification must be read before deletion");

        notificationRepository.delete(notification);
    }
}