package co.edu.udistrital.mdp.pets.services.notification;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.services.notification.NotificationStrategy;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.NotificationRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdoptionRepository adoptionRepository;
    @Autowired
    private PetRepository petRepository;


    private final Map<String, NotificationStrategy> strategies;

    public NotificationService(Map<String, NotificationStrategy> strategies) {
        this.strategies = strategies;
    }

    
    @Transactional
    public NotificationEntity createNotification(NotificationEntity notification) {
        log.info("Inicia proceso de creación de la notificación");
        if(notification.getContent() == null || notification.getContent().isEmpty())
            throw new IllegalArgumentException("Notification content cannot be null or empty");

        String channel = notification.getChannel();

        if (channel == null || !strategies.containsKey(channel))
            throw new IllegalArgumentException("Notification channel is not valid or not supported");

        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Transactional
    public NotificationEntity updateNotification(NotificationEntity notification, Long requestingUserId) throws IllegalOperationException {
        log.info("Inicia proceso de actualización de la notificación");
        if (notification.getId() == null || !notificationRepository.existsById(notification.getId()))
            throw new IllegalOperationException("Notification does not exist");

        if(!requestingUserId.equals(notification.getUser().getId()))
            throw new IllegalOperationException("User is not authorized to update this notification");

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(NotificationEntity notification, Long requestingUserId) throws IllegalOperationException {
        log.info("Inicia proceso de eliminación de la notificación");
        
        if (!notificationRepository.existsById(notification.getId()))
            throw new IllegalOperationException("Notification does not exist");

        if(!requestingUserId.equals(notification.getUser().getId()))
            throw new IllegalOperationException("User is not authorized to delete this notification");

        if(!notification.isRead())
            throw new IllegalOperationException("Notification must be read before deletion");

        notificationRepository.delete(notification);
    }
}
