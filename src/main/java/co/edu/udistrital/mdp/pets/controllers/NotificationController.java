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
 * Class implementing the "notifications" resource, including its
 * association endpoints. No class-level @RequestMapping on purpose: the
 * association routes (/users/{id}/notifications, etc.) live outside
 * /notifications, so every method declares its own full path.
 */
@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    private final ModelMapper modelMapper;

    @GetMapping("/notifications")
    @ResponseStatus(code = HttpStatus.OK)
    public List<NotificationDetailDTO> findAll() {
        List<NotificationEntity> notifications = notificationService.getNotifications();
        return modelMapper.map(notifications, new TypeToken<List<NotificationDetailDTO>>() {
        }.getType());
    }

    @GetMapping("/notifications/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public NotificationDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        NotificationEntity notificationEntity = notificationService.getNotification(id);
        return modelMapper.map(notificationEntity, NotificationDetailDTO.class);
    }

    @PostMapping("/notifications")
    @ResponseStatus(code = HttpStatus.CREATED)
    public NotificationDTO create(@RequestBody NotificationDetailDTO notificationDTO)
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity notificationEntity = modelMapper.map(notificationDTO, NotificationEntity.class);

        UserEntity user = new UserEntity();
        user.setId(notificationDTO.getUser().getId());
        notificationEntity.setUser(user);

        AdoptionEntity adoption = new AdoptionEntity();
        adoption.setId(notificationDTO.getAdoption().getId());
        notificationEntity.setAdoption(adoption);

        if (notificationDTO.getPet() != null) {
            PetEntity pet = new PetEntity();
            pet.setId(notificationDTO.getPet().getId());
            notificationEntity.setPet(pet);
        }

        NotificationEntity saved = notificationService.createNotification(notificationEntity);
        return modelMapper.map(saved, NotificationDTO.class);
    }

    @PutMapping("/notifications/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public NotificationDTO update(@PathVariable Long id, @RequestParam Long userId)
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity existing = notificationService.getNotification(id);
        NotificationEntity updated = notificationService.updateNotification(existing, userId);
        return modelMapper.map(updated, NotificationDTO.class);
    }

    @DeleteMapping("/notifications/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @RequestParam Long userId)
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity existing = notificationService.getNotification(id);
        notificationService.deleteNotification(existing, userId);
    }

    @GetMapping("/users/{userId}/notifications")
    @ResponseStatus(code = HttpStatus.OK)
    public List<NotificationDetailDTO> findByUser(@PathVariable Long userId) throws EntityNotFoundException {
        List<NotificationEntity> notifications = notificationService.getNotificationsByUser(userId);
        return modelMapper.map(notifications, new TypeToken<List<NotificationDetailDTO>>() {
        }.getType());
    }

    @GetMapping("/adoptions/{adoptionId}/notifications")
    @ResponseStatus(code = HttpStatus.OK)
    public List<NotificationDetailDTO> findByAdoption(@PathVariable Long adoptionId) throws EntityNotFoundException {
        List<NotificationEntity> notifications = notificationService.getNotificationsByAdoption(adoptionId);
        return modelMapper.map(notifications, new TypeToken<List<NotificationDetailDTO>>() {
        }.getType());
    }

    @GetMapping("/pets/{petId}/notifications")
    @ResponseStatus(code = HttpStatus.OK)
    public List<NotificationDetailDTO> findByPet(@PathVariable Long petId) throws EntityNotFoundException {
        List<NotificationEntity> notifications = notificationService.getNotificationsByPet(petId);
        return modelMapper.map(notifications, new TypeToken<List<NotificationDetailDTO>>() {
        }.getType());
    }
}