package co.edu.udistrital.mdp.ZZZ.services;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.ZZZ.entities.PetEntity;
import co.edu.udistrital.mdp.ZZZ.entities.ShelterEntity;
import co.edu.udistrital.mdp.ZZZ.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.ZZZ.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ShelterService.class)
class ShelterServiceTest {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private ShelterEntity shelter = new ShelterEntity();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from ShelterEventEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from VeterinarianEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);
    }

    // ---------- createShelter ----------

    @Test
    void testCreateShelter() throws IllegalOperationException {
        ShelterEntity newShelter = factory.manufacturePojo(ShelterEntity.class);
        ShelterEntity result = shelterService.createShelter(newShelter);
        assertNotNull(result);

        ShelterEntity stored = entityManager.find(ShelterEntity.class, result.getId());
        assertEquals(newShelter.getNit(), stored.getNit());
        assertEquals(newShelter.getCity(), stored.getCity());
    }

    @Test
    void testCreateShelterWithoutNit() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newShelter = factory.manufacturePojo(ShelterEntity.class);
            newShelter.setNit(null);
            shelterService.createShelter(newShelter);
        });
    }

    @Test
    void testCreateShelterWithDuplicateNit() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newShelter = factory.manufacturePojo(ShelterEntity.class);
            newShelter.setNit(shelter.getNit());
            shelterService.createShelter(newShelter);
        });
    }

    @Test
    void testCreateShelterWithoutCity() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newShelter = factory.manufacturePojo(ShelterEntity.class);
            newShelter.setCity(null);
            shelterService.createShelter(newShelter);
        });
    }

    // ---------- getShelters ----------

    @Test
    void testGetShelters() {
        assertFalse(shelterService.getShelters().isEmpty());
    }

    // ---------- getShelter ----------

    @Test
    void testGetShelter() throws EntityNotFoundException {
        ShelterEntity result = shelterService.getShelter(shelter.getId());
        assertNotNull(result);
        assertEquals(shelter.getNit(), result.getNit());
        assertEquals(shelter.getCity(), result.getCity());
    }

    @Test
    void testGetInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            shelterService.getShelter(0L);
        });
    }

    // ---------- updateShelter ----------

    @Test
    void testUpdateShelter() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity updated = factory.manufacturePojo(ShelterEntity.class);
        updated.setId(shelter.getId());
        shelterService.updateShelter(shelter.getId(), updated);

        ShelterEntity stored = entityManager.find(ShelterEntity.class, shelter.getId());
        assertEquals(updated.getNit(), stored.getNit());
        assertEquals(updated.getCity(), stored.getCity());
    }

    @Test
    void testUpdateInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            ShelterEntity updated = factory.manufacturePojo(ShelterEntity.class);
            shelterService.updateShelter(0L, updated);
        });
    }

    @Test
    void testUpdateShelterWithDuplicateNit() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity another = factory.manufacturePojo(ShelterEntity.class);
            entityManager.persist(another);

            ShelterEntity updated = factory.manufacturePojo(ShelterEntity.class);
            updated.setNit(another.getNit());
            shelterService.updateShelter(shelter.getId(), updated);
        });
    }

    // ---------- deleteShelter ----------

    @Test
    void testDeleteShelter() throws EntityNotFoundException, IllegalOperationException {
        shelterService.deleteShelter(shelter.getId());
        ShelterEntity deleted = entityManager.find(ShelterEntity.class, shelter.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            shelterService.deleteShelter(0L);
        });
    }

    @Test
    void testDeleteShelterWithVeterinarians() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity vet = factory.manufacturePojo(VeterinarianEntity.class);
            vet.setShelter(shelter);
            entityManager.persist(vet);
            shelter.getVeterinarians().add(vet);

            shelterService.deleteShelter(shelter.getId());
        });
    }

    @Test
    void testDeleteShelterWithEvents() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEventEntity event = factory.manufacturePojo(ShelterEventEntity.class);
            event.setShelter(shelter);
            entityManager.persist(event);
            shelter.getShelterEvents().add(event);

            shelterService.deleteShelter(shelter.getId());
        });
    }

    @Test
    void testDeleteShelterWithPets() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = factory.manufacturePojo(PetEntity.class);
            pet.setShelter(shelter);
            entityManager.persist(pet);
            shelter.getPets().add(pet);

            shelterService.deleteShelter(shelter.getId());
        });
    }
}