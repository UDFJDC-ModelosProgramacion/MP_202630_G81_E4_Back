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
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.notification.EmailNotificationStrategy;
import co.edu.udistrital.mdp.pets.services.notification.NotificationService;
import co.edu.udistrital.mdp.pets.services.notification.PushNotificationStrategy;
import co.edu.udistrital.mdp.pets.services.notification.SMSNotificationStrategy;
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

    private static final String VALID_CHANNEL = "EMAIL";
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

    // ---------- createNotification ----------

    @Test
    void testCreateNotification() {
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
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
            newEntity.setUser(owner);
            newEntity.setChannel(VALID_CHANNEL);
            newEntity.setContent(null);
            notificationService.createNotification(newEntity);
        });
    }

    @Test
    void testCreateNotificationWithEmptyContent() {
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
            newEntity.setUser(owner);
            newEntity.setChannel(VALID_CHANNEL);
            newEntity.setContent("");
            notificationService.createNotification(newEntity);
        });
    }

    @Test
    void testCreateNotificationWithInvalidChannel() {
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
            newEntity.setUser(owner);
            newEntity.setChannel(INVALID_CHANNEL);
            notificationService.createNotification(newEntity);
        });
    }

    @Test
    void testCreateNotificationWithNullChannel() {
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
            newEntity.setUser(owner);
            newEntity.setChannel(null);
            notificationService.createNotification(newEntity);
        });
    }

    // ---------- updateNotification ----------

    @Test
    void testUpdateNotification() throws IllegalOperationException {
        NotificationEntity entity = notificationList.get(0);

        NotificationEntity result = notificationService.updateNotification(entity, owner.getId());

        assertNotNull(result);
        assertTrue(result.isRead());
        NotificationEntity stored = entityManager.find(NotificationEntity.class, entity.getId());
        assertTrue(stored.isRead());
    }

    @Test
    void testUpdateNotificationInvalid() {
        assertThrows(IllegalOperationException.class, () -> {
            NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
            newEntity.setId(0L);
            notificationService.updateNotification(newEntity, owner.getId());
        });
    }

    @Test
    void testUpdateNotificationNotAuthorized() {
        assertThrows(IllegalOperationException.class, () -> {
            NotificationEntity entity = notificationList.get(0);
            notificationService.updateNotification(entity, otherUser.getId());
        });
    }

    // ---------- deleteNotification ----------

    @Test
    void testDeleteNotification() throws IllegalOperationException {
        NotificationEntity entity = notificationList.get(0);
        entity.setRead(true);
        entityManager.persist(entity);

        notificationService.deleteNotification(entity, owner.getId());

        NotificationEntity deleted = entityManager.find(NotificationEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidNotification() {
        assertThrows(IllegalOperationException.class, () -> {
            NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
            newEntity.setId(0L);
            notificationService.deleteNotification(newEntity, owner.getId());
        });
    }

    @Test
    void testDeleteNotificationNotAuthorized() {
        assertThrows(IllegalOperationException.class, () -> {
            NotificationEntity entity = notificationList.get(0);
            notificationService.deleteNotification(entity, otherUser.getId());
        });
    }

    @Test
    void testDeleteNotificationNotRead() {
        assertThrows(IllegalOperationException.class, () -> {
            NotificationEntity entity = notificationList.get(0);
            entity.setRead(false);
            notificationService.deleteNotification(entity, owner.getId());
        });
    }
}