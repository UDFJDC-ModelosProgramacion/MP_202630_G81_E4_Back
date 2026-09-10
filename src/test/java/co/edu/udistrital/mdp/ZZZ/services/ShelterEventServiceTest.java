package co.edu.udistrital.mdp.ZZZ.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.ZZZ.entities.ShelterEntity;
import co.edu.udistrital.mdp.ZZZ.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ShelterEventService.class)
class ShelterEventServiceTest {

    @Autowired
    private ShelterEventService shelterEventService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private ShelterEntity shelter = new ShelterEntity();
    private ShelterEventEntity shelterEvent = new ShelterEventEntity();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from ShelterEventEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);

        shelterEvent = factory.manufacturePojo(ShelterEventEntity.class);
        shelterEvent.setShelter(shelter);
        entityManager.persist(shelterEvent);
    }

    // ---------- createShelterEvent ----------

    @Test
    void testCreateShelterEvent() throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity newEvent = factory.manufacturePojo(ShelterEventEntity.class);
        ShelterEventEntity result = shelterEventService.createShelterEvent(shelter.getId(), newEvent);
        assertNotNull(result);

        ShelterEventEntity stored = entityManager.find(ShelterEventEntity.class, result.getId());
        assertEquals(newEvent.getName(), stored.getName());
        assertEquals(shelter.getId(), stored.getShelter().getId());
    }

    @Test
    void testCreateShelterEventInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            ShelterEventEntity newEvent = factory.manufacturePojo(ShelterEventEntity.class);
            shelterEventService.createShelterEvent(0L, newEvent);
        });
    }

    @Test
    void testCreateShelterEventWithoutDate() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEventEntity newEvent = factory.manufacturePojo(ShelterEventEntity.class);
            newEvent.setDate(null);
            shelterEventService.createShelterEvent(shelter.getId(), newEvent);
        });
    }

    @Test
    void testCreateShelterEventWithoutName() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEventEntity newEvent = factory.manufacturePojo(ShelterEventEntity.class);
            newEvent.setName(null);
            shelterEventService.createShelterEvent(shelter.getId(), newEvent);
        });
    }

    // ---------- getShelterEvents ----------

    @Test
    void testGetShelterEvents() {
        assertFalse(shelterEventService.getShelterEvents().isEmpty());
    }

    // ---------- getShelterEvent ----------

    @Test
    void testGetShelterEvent() throws EntityNotFoundException {
        ShelterEventEntity result = shelterEventService.getShelterEvent(shelterEvent.getId());
        assertNotNull(result);
        assertEquals(shelterEvent.getName(), result.getName());
    }

    @Test
    void testGetInvalidShelterEvent() {
        assertThrows(EntityNotFoundException.class, () -> {
            shelterEventService.getShelterEvent(0L);
        });
    }

    // ---------- updateShelterEvent ----------

    @Test
    void testUpdateShelterEvent() throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity updated = factory.manufacturePojo(ShelterEventEntity.class);
        updated.setId(shelterEvent.getId());
        updated.setShelter(shelter);
        shelterEventService.updateShelterEvent(shelterEvent.getId(), updated);

        ShelterEventEntity stored = entityManager.find(ShelterEventEntity.class, shelterEvent.getId());
        assertEquals(updated.getName(), stored.getName());
    }

    @Test
    void testUpdateInvalidShelterEvent() {
        assertThrows(EntityNotFoundException.class, () -> {
            ShelterEventEntity updated = factory.manufacturePojo(ShelterEventEntity.class);
            shelterEventService.updateShelterEvent(0L, updated);
        });
    }

    @Test
    void testUpdateShelterEventInvalidShelter() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEventEntity updated = factory.manufacturePojo(ShelterEventEntity.class);
            ShelterEntity fakeShelter = new ShelterEntity();
            fakeShelter.setId(0L);
            updated.setShelter(fakeShelter);
            shelterEventService.updateShelterEvent(shelterEvent.getId(), updated);
        });
    }

    // ---------- deleteShelterEvent ----------

    @Test
    void testDeleteShelterEvent() throws EntityNotFoundException {
        shelterEventService.deleteShelterEvent(shelterEvent.getId());
        ShelterEventEntity deleted = entityManager.find(ShelterEventEntity.class, shelterEvent.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidShelterEvent() {
        assertThrows(EntityNotFoundException.class, () -> {
            shelterEventService.deleteShelterEvent(0L);
        });
    }
}
