package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ReturnEntity;
import co.edu.udistrital.mdp.pets.entities.TrialRequestEntity;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ReturnService.class)
class ReturnServiceTest {

    @Autowired
    private ReturnService returnService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private TrialRequestEntity trialRequest;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from ReturnEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from TrialRequestEntity").executeUpdate();
    }

    private void insertData() {
        trialRequest = factory.manufacturePojo(TrialRequestEntity.class);
        entityManager.persist(trialRequest);
    }

    @Test
    void testCreateReturn() {
        ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
        newEntity.setTrialRequest(trialRequest);
        newEntity.setDescription("Pet returned due to allergies");
        newEntity.setReturnType("VOLUNTARY");

        ReturnEntity result = returnService.createReturn(newEntity);

        assertNotNull(result);
        ReturnEntity stored = entityManager.find(ReturnEntity.class, result.getId());
        assertEquals(newEntity.getDescription(), stored.getDescription());
        assertEquals(newEntity.getReturnType(), stored.getReturnType());
        assertEquals(trialRequest.getId(), stored.getTrialRequest().getId());
    }

    @Test
    void testCreateReturnWithNullTrialRequest() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
            newEntity.setTrialRequest(null);
            newEntity.setDescription("Pet returned due to allergies");
            newEntity.setReturnType("VOLUNTARY");
            returnService.createReturn(newEntity);
        });
    }

    @Test
    void testCreateReturnWithInvalidTrialRequest() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
            TrialRequestEntity invalidTrial = new TrialRequestEntity();
            invalidTrial.setId(0L);
            newEntity.setTrialRequest(invalidTrial);
            newEntity.setDescription("Pet returned due to allergies");
            newEntity.setReturnType("VOLUNTARY");
            returnService.createReturn(newEntity);
        });
    }

    @Test
    void testCreateReturnWithNullDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
            newEntity.setTrialRequest(trialRequest);
            newEntity.setDescription(null);
            newEntity.setReturnType("VOLUNTARY");
            returnService.createReturn(newEntity);
        });
    }

    @Test
    void testCreateReturnWithEmptyDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
            newEntity.setTrialRequest(trialRequest);
            newEntity.setDescription("");
            newEntity.setReturnType("VOLUNTARY");
            returnService.createReturn(newEntity);
        });
    }

    @Test
    void testCreateReturnWithNullReturnType() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
            newEntity.setTrialRequest(trialRequest);
            newEntity.setDescription("Pet returned due to allergies");
            newEntity.setReturnType(null);
            returnService.createReturn(newEntity);
        });
    }

    @Test
    void testCreateReturnWithEmptyReturnType() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
            newEntity.setTrialRequest(trialRequest);
            newEntity.setDescription("Pet returned due to allergies");
            newEntity.setReturnType("");
            returnService.createReturn(newEntity);
        });
    }
}