package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ReturnEntity;
import co.edu.udistrital.mdp.pets.entities.TrialRequestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;

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

    private List<ReturnEntity> returnList = new ArrayList<>();

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

        for (int i = 0; i < 3; i++) {
            TrialRequestEntity ownTrialRequest = factory.manufacturePojo(TrialRequestEntity.class);
            entityManager.persist(ownTrialRequest);

            ReturnEntity returnEntity = factory.manufacturePojo(ReturnEntity.class);
            returnEntity.setTrialRequest(ownTrialRequest);
            returnEntity.setDescription("Pet returned due to allergies");
            returnEntity.setReturnType("VOLUNTARY");
            entityManager.persist(returnEntity);
            returnList.add(returnEntity);
        }
    }

    // ---------- getReturns / getReturn ----------

    @Test
    void testGetReturns() {
        List<ReturnEntity> list = returnService.getReturns();

        assertEquals(returnList.size(), list.size());
        for (ReturnEntity entity : list) {
            boolean found = returnList.stream()
                .anyMatch(item -> item.getId().equals(entity.getId()));
            assertTrue(found);
        }
    }

    @Test
    void testGetReturn() throws EntityNotFoundException {
        ReturnEntity entity = returnList.get(0);

        ReturnEntity result = returnService.getReturn(entity.getId());

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getDescription(), result.getDescription());
        assertEquals(entity.getReturnType(), result.getReturnType());
    }

    @Test
    void testGetInvalidReturn() {
        assertThrows(EntityNotFoundException.class,
            () -> returnService.getReturn(0L));
    }

    // ---------- createReturn ----------

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
        ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
        newEntity.setTrialRequest(null);
        newEntity.setDescription("Pet returned due to allergies");
        newEntity.setReturnType("VOLUNTARY");

        assertThrows(IllegalArgumentException.class,
            () -> returnService.createReturn(newEntity));
    }

    @Test
    void testCreateReturnWithInvalidTrialRequest() {
        ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
        TrialRequestEntity invalidTrial = new TrialRequestEntity();
        invalidTrial.setId(0L);
        newEntity.setTrialRequest(invalidTrial);
        newEntity.setDescription("Pet returned due to allergies");
        newEntity.setReturnType("VOLUNTARY");

        assertThrows(IllegalArgumentException.class,
            () -> returnService.createReturn(newEntity));
    }

    static Stream<Arguments> invalidDescriptionAndReturnType() {
        return Stream.of(
            Arguments.of(null, "VOLUNTARY"),
            Arguments.of("", "VOLUNTARY"),
            Arguments.of("Pet returned due to allergies", null),
            Arguments.of("Pet returned due to allergies", ""));
    }

    @ParameterizedTest
    @MethodSource("invalidDescriptionAndReturnType")
    void testCreateReturnWithInvalidDescriptionOrReturnType(String description, String returnType) {
        ReturnEntity newEntity = factory.manufacturePojo(ReturnEntity.class);
        newEntity.setTrialRequest(trialRequest);
        newEntity.setDescription(description);
        newEntity.setReturnType(returnType);

        assertThrows(IllegalArgumentException.class,
            () -> returnService.createReturn(newEntity));
    }
}