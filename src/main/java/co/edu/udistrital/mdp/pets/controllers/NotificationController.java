package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.NotificationDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.NotificationService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "notifications" resource.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    private final ModelMapper modelMapper;

    /**
     * Returns all notifications existing in the application.
     *
     * @return JSONArray {@link NotificationDetailDTO}. Empty list if none exist.
     */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<NotificationDetailDTO> findAll() {
        List<NotificationEntity> notifications = notificationService.getNotifications();
        return modelMapper.map(notifications, new TypeToken<List<NotificationDetailDTO>>() {
        }.getType());
    }

    /**
     * Returns the notification with the ID received in the URL.
     *
     * @param id Identifier of the notification.
     * @return JSON {@link NotificationDetailDTO}
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public NotificationDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        NotificationEntity notificationEntity = notificationService.getNotification(id);
        return modelMapper.map(notificationEntity, NotificationDetailDTO.class);
    }

    /**
     * Creates a new notification with the information received in the body.
     * The nested user/adoption/pet are built explicitly from
     * userId/adoptionId/petId instead of relying on ModelMapper's implicit
     * flat-to-nested mapping, which does not reliably populate them.
     *
     * @param notificationDTO {@link NotificationDTO} The notification to be saved.
     * @return JSON {@link NotificationDTO} The saved notification with its ID.
     */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public NotificationDTO create(@RequestBody NotificationDTO notificationDTO)
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity notificationEntity = modelMapper.map(notificationDTO, NotificationEntity.class);

        UserEntity user = new UserEntity();
        user.setId(notificationDTO.getUserId());
        notificationEntity.setUser(user);

        AdoptionEntity adoption = new AdoptionEntity();
        adoption.setId(notificationDTO.getAdoptionId());
        notificationEntity.setAdoption(adoption);

        if (notificationDTO.getPetId() != null) {
            PetEntity pet = new PetEntity();
            pet.setId(notificationDTO.getPetId());
            notificationEntity.setPet(pet);
        }

        NotificationEntity saved = notificationService.createNotification(notificationEntity);
        return modelMapper.map(saved, NotificationDTO.class);
    }

    /**
     * Marks as read the notification with the ID received in the URL.
     * Only the recipient user can do it.
     *
     * @param id     Identifier of the notification.
     * @param userId Identifier of the user making the request.
     * @return JSON {@link NotificationDTO} The updated notification.
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public NotificationDTO update(@PathVariable Long id, @RequestParam Long userId)
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity existing = notificationService.getNotification(id);
        NotificationEntity updated = notificationService.updateNotification(existing, userId);
        return modelMapper.map(updated, NotificationDTO.class);
    }

    /**
     * Deletes the notification with the ID received in the URL.
     * Only the recipient user can do it, and only if it was already read.
     *
     * @param id     Identifier of the notification.
     * @param userId Identifier of the user making the request.
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @RequestParam Long userId)
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity existing = notificationService.getNotification(id);
        notificationService.deleteNotification(existing, userId);
    }
}