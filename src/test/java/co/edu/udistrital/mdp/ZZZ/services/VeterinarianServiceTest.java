package co.edu.udistrital.mdp.ZZZ.services;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.ZZZ.entities.ShelterEntity;
import co.edu.udistrital.mdp.ZZZ.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(VeterinarianService.class)
class VeterinarianServiceTest {

    @Autowired
    private VeterinarianService veterinarianService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private ShelterEntity shelter = new ShelterEntity();
    private VeterinarianEntity veterinarian = new VeterinarianEntity();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from VeterinarianEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);

        veterinarian = factory.manufacturePojo(VeterinarianEntity.class);
        veterinarian.setShelter(shelter);
        entityManager.persist(veterinarian);
    }

    // ---------- createVeterinarian ----------

    @Test
    void testCreateVeterinarian() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity newVet = factory.manufacturePojo(VeterinarianEntity.class);
        VeterinarianEntity result = veterinarianService.createVeterinarian(shelter.getId(), newVet);
        assertNotNull(result);

        VeterinarianEntity stored = entityManager.find(VeterinarianEntity.class, result.getId());
        assertEquals(newVet.getVeterinarianId(), stored.getVeterinarianId());
        assertEquals(shelter.getId(), stored.getShelter().getId());
    }

    @Test
    void testCreateVeterinarianInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            VeterinarianEntity newVet = factory.manufacturePojo(VeterinarianEntity.class);
            veterinarianService.createVeterinarian(0L, newVet);
        });
    }

    @Test
    void testCreateVeterinarianWithoutId() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity newVet = factory.manufacturePojo(VeterinarianEntity.class);
            newVet.setVeterinarianId(null);
            veterinarianService.createVeterinarian(shelter.getId(), newVet);
        });
    }

    @Test
    void testCreateVeterinarianWithDuplicateId() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity newVet = factory.manufacturePojo(VeterinarianEntity.class);
            newVet.setVeterinarianId(veterinarian.getVeterinarianId());
            veterinarianService.createVeterinarian(shelter.getId(), newVet);
        });
    }

    @Test
    void testCreateVeterinarianWithoutSpecialty() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity newVet = factory.manufacturePojo(VeterinarianEntity.class);
            newVet.setSpecialty(null);
            veterinarianService.createVeterinarian(shelter.getId(), newVet);
        });
    }

    // ---------- getVeterinarians ----------

    @Test
    void testGetVeterinarians() {
        assertFalse(veterinarianService.getVeterinarians().isEmpty());
    }

    // ---------- getVeterinarian ----------

    @Test
    void testGetVeterinarian() throws EntityNotFoundException {
        VeterinarianEntity result = veterinarianService.getVeterinarian(veterinarian.getId());
        assertNotNull(result);
        assertEquals(veterinarian.getVeterinarianId(), result.getVeterinarianId());
    }

    @Test
    void testGetInvalidVeterinarian() {
        assertThrows(EntityNotFoundException.class, () -> {
            veterinarianService.getVeterinarian(0L);
        });
    }

    // ---------- updateVeterinarian ----------

    @Test
    void testUpdateVeterinarian() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity updated = factory.manufacturePojo(VeterinarianEntity.class);
        updated.setId(veterinarian.getId());
        updated.setShelter(shelter);
        veterinarianService.updateVeterinarian(veterinarian.getId(), updated);

        VeterinarianEntity stored = entityManager.find(VeterinarianEntity.class, veterinarian.getId());
        assertEquals(updated.getSpecialty(), stored.getSpecialty());
    }

    @Test
    void testUpdateInvalidVeterinarian() {
        assertThrows(EntityNotFoundException.class, () -> {
            VeterinarianEntity updated = factory.manufacturePojo(VeterinarianEntity.class);
            veterinarianService.updateVeterinarian(0L, updated);
        });
    }

    @Test
    void testUpdateVeterinarianInvalidShelter() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity updated = factory.manufacturePojo(VeterinarianEntity.class);
            ShelterEntity fakeShelter = new ShelterEntity();
            fakeShelter.setId(0L);
            updated.setShelter(fakeShelter);
            veterinarianService.updateVeterinarian(veterinarian.getId(), updated);
        });
    }

    // ---------- deleteVeterinarian ----------

    @Test
    void testDeleteVeterinarian() throws EntityNotFoundException {
        veterinarianService.deleteVeterinarian(veterinarian.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, veterinarian.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidVeterinarian() {
        assertThrows(EntityNotFoundException.class, () -> {
            veterinarianService.deleteVeterinarian(0L);
        });
    }
}