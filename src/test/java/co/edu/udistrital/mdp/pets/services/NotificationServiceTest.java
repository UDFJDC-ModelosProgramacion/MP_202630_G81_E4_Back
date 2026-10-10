package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.entities.EmailNotificationStrategy;
import co.edu.udistrital.mdp.pets.entities.PushNotificationStrategy;
import co.edu.udistrital.mdp.pets.entities.SMSNotificationStrategy;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({
    NotificationService.class,
    EmailNotificationStrategy.class,
    SMSNotificationStrategy.class,
    PushNotificationStrategy.class
})
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<NotificationEntity> notificationList = new ArrayList<>();

    private UserEntity owner;
    private UserEntity otherUser;

    private static final String VALID_CHANNEL = "email";
    private static final String INVALID_CHANNEL = "CARRIER_PIGEON";

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from NotificationEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from UserEntity").executeUpdate();
    }

    private void insertData() {
        owner = factory.manufacturePojo(UserEntity.class);
        entityManager.persist(owner);

        otherUser = factory.manufacturePojo(UserEntity.class);
        entityManager.persist(otherUser);

        for (int i = 0; i < 3; i++) {
            NotificationEntity notification = factory.manufacturePojo(NotificationEntity.class);
            notification.setUser(owner);
            notification.setChannel(VALID_CHANNEL);
            notification.setRead(false);
            entityManager.persist(notification);
            notificationList.add(notification);
        }
    }

    // ---------- getNotifications / getNotification ----------

    @Test
    void testGetNotifications() {
        List<NotificationEntity> list = notificationService.getNotifications();

        assertEquals(notificationList.size(), list.size());
        for (NotificationEntity entity : list) {
            boolean found = notificationList.stream()
                .anyMatch(item -> item.getId().equals(entity.getId()));
            assertTrue(found);
        }
    }

    @Test
    void testGetNotification() throws EntityNotFoundException {
        NotificationEntity entity = notificationList.get(0);

        NotificationEntity result = notificationService.getNotification(entity.getId());

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getContent(), result.getContent());
        assertEquals(entity.getChannel(), result.getChannel());
    }

    @Test
    void testGetInvalidNotification() {
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.getNotification(0L));
    }

        // ---------- getNotificationsByUser / Adoption / Pet ----------

    @Test
    void testGetNotificationsByUser() throws EntityNotFoundException {
        List<NotificationEntity> list = notificationService.getNotificationsByUser(owner.getId());

        assertEquals(notificationList.size(), list.size());
        for (NotificationEntity entity : list) {
            assertEquals(owner.getId(), entity.getUser().getId());
        }
    }

    @Test
    void testGetNotificationsByInvalidUser() {
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.getNotificationsByUser(0L));
    }

    @Test
    void testGetNotificationsByAdoption() throws EntityNotFoundException {
        AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
        entityManager.persist(adoption);
        NotificationEntity notification = notificationList.get(0);
        notification.setAdoption(adoption);
        entityManager.persist(notification);

        List<NotificationEntity> list = notificationService.getNotificationsByAdoption(adoption.getId());

        assertEquals(1, list.size());
        assertEquals(notification.getId(), list.get(0).getId());
    }

    @Test
    void testGetNotificationsByInvalidAdoption() {
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.getNotificationsByAdoption(0L));
    }

    @Test
    void testGetNotificationsByPet() throws EntityNotFoundException {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        NotificationEntity notification = notificationList.get(0);
        notification.setPet(pet);
        entityManager.persist(notification);

        List<NotificationEntity> list = notificationService.getNotificationsByPet(pet.getId());

        assertEquals(1, list.size());
        assertEquals(notification.getId(), list.get(0).getId());
    }

    @Test
    void testGetNotificationsByInvalidPet() {
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.getNotificationsByPet(0L));
    }

    // ---------- updateNotification con id null ----------

    @Test
    void testUpdateNotificationWithNullId() {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setId(null);
        Long ownerId = owner.getId();

        assertThrows(EntityNotFoundException.class,
            () -> notificationService.updateNotification(newEntity, ownerId));
    }

    // ---------- createNotification ----------

    @Test
    void testCreateNotification() throws IllegalOperationException {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setUser(owner);
        newEntity.setChannel(VALID_CHANNEL);

        NotificationEntity result = notificationService.createNotification(newEntity);

        assertNotNull(result);
        NotificationEntity stored = entityManager.find(NotificationEntity.class, result.getId());
        assertEquals(newEntity.getContent(), stored.getContent());
        assertEquals(newEntity.getChannel(), stored.getChannel());
        assertFalse(stored.isRead());
    }

    @Test
    void testCreateNotificationWithNullContent() {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setUser(owner);
        newEntity.setChannel(VALID_CHANNEL);
        newEntity.setContent(null);

        assertThrows(IllegalOperationException.class,
            () -> notificationService.createNotification(newEntity));
    }

    @Test
    void testCreateNotificationWithEmptyContent() {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setUser(owner);
        newEntity.setChannel(VALID_CHANNEL);
        newEntity.setContent("");

        assertThrows(IllegalOperationException.class,
            () -> notificationService.createNotification(newEntity));
    }

    @Test
    void testCreateNotificationWithInvalidChannel() {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setUser(owner);
        newEntity.setChannel(INVALID_CHANNEL);


        assertThrows(IllegalOperationException.class,
            () -> notificationService.createNotification(newEntity));
    }

    @Test
    void testCreateNotificationWithNullChannel() {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setUser(owner);
        newEntity.setChannel(null);

        assertThrows(IllegalOperationException.class,
            () -> notificationService.createNotification(newEntity));
    }

    // ---------- updateNotification ----------

    @Test
    void testUpdateNotification() throws IllegalOperationException, EntityNotFoundException {
        NotificationEntity entity = notificationList.get(0);

        NotificationEntity result = notificationService.updateNotification(entity, owner.getId());

        assertNotNull(result);
        assertTrue(result.isRead());
        NotificationEntity stored = entityManager.find(NotificationEntity.class, entity.getId());
        assertTrue(stored.isRead());
    }

    @Test
    void testUpdateNotificationInvalid() {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setId(0L);
        Long ownerId = owner.getId();

        assertThrows(EntityNotFoundException.class,
            () -> notificationService.updateNotification(newEntity, ownerId));
    }

    @Test
    void testUpdateNotificationNotAuthorized() {
        NotificationEntity entity = notificationList.get(0);
        Long otherUserId = otherUser.getId();

        assertThrows(IllegalOperationException.class,
            () -> notificationService.updateNotification(entity, otherUserId));
    }

    // ---------- deleteNotification ----------

    @Test
    void testDeleteNotification() throws IllegalOperationException, EntityNotFoundException {
        NotificationEntity entity = notificationList.get(0);
        entity.setRead(true);
        entityManager.persist(entity);

        notificationService.deleteNotification(entity, owner.getId());

        NotificationEntity deleted = entityManager.find(NotificationEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidNotification() {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setId(0L);
        Long ownerId = owner.getId();

        assertThrows(EntityNotFoundException.class,
            () -> notificationService.deleteNotification(newEntity, ownerId));
    }

    @Test
    void testDeleteNotificationNotAuthorized() {
        NotificationEntity entity = notificationList.get(0);
        Long otherUserId = otherUser.getId();

        assertThrows(IllegalOperationException.class,
            () -> notificationService.deleteNotification(entity, otherUserId));
    }

    @Test
    void testDeleteNotificationNotRead() {
        NotificationEntity entity = notificationList.get(0);
        entity.setRead(false);
        Long ownerId = owner.getId();

        assertThrows(IllegalOperationException.class,
            () -> notificationService.deleteNotification(entity, ownerId));
    }
}