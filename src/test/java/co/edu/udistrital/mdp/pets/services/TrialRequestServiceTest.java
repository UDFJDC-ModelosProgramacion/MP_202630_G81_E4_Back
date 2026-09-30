package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ReturnEntity;
import co.edu.udistrital.mdp.pets.entities.TrialRequestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(TrialRequestService.class)
class TrialRequestServiceTest {

	@Autowired
	private TrialRequestService trialRequestService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<AdopterEntity> adopterList = new ArrayList<>();
	private List<PetEntity> petList = new ArrayList<>();
	private List<TrialRequestEntity> trialRequestList = new ArrayList<>();

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from ReturnEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from TrialRequestEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
	}

	/**
	 * adopter0/pet0: TrialRequest ACTIVA existente (para probar duplicados).
	 * adopter1..4/pet1..4: libres para los distintos escenarios de creación.
	 * pet2: adoptionStatus = "ADOPTADA".
	 */
	private void insertData() {
		for (int i = 0; i < 5; i++) {
			AdopterEntity adopter = factory.manufacturePojo(AdopterEntity.class);
			entityManager.persist(adopter);
			adopterList.add(adopter);

			PetEntity pet = factory.manufacturePojo(PetEntity.class);
			pet.setAdoptionStatus(i == 2 ? "ADOPTADA" : "DISPONIBLE");
			entityManager.persist(pet);
			petList.add(pet);
		}

		TrialRequestEntity trial = factory.manufacturePojo(TrialRequestEntity.class);
		trial.setAdopter(adopterList.get(0));
		trial.setPet(petList.get(0));
		trial.setStatus("ACTIVA");
		trial.setDate(new Date());
		entityManager.persist(trial);
		trialRequestList.add(trial);
	}

	// ---------- createTrialRequest ----------

	@Test
	void testCreateTrialRequest() throws IllegalOperationException {
		TrialRequestEntity newEntity = factory.manufacturePojo(TrialRequestEntity.class);
		newEntity.setAdopter(adopterList.get(1));
		newEntity.setPet(petList.get(1));
		newEntity.setStatus("ACTIVA");
		newEntity.setDate(new Date());

		TrialRequestEntity result = trialRequestService.createTrialRequest(newEntity);
		assertNotNull(result);

		TrialRequestEntity stored = entityManager.find(TrialRequestEntity.class, result.getId());
		assertEquals(newEntity.getAdopter().getId(), stored.getAdopter().getId());
		assertEquals(newEntity.getPet().getId(), stored.getPet().getId());
	}

	@Test
	void testCreateTrialRequestWithNoAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			TrialRequestEntity newEntity = factory.manufacturePojo(TrialRequestEntity.class);
			newEntity.setAdopter(null);
			newEntity.setPet(petList.get(1));
			newEntity.setDate(new Date());
			trialRequestService.createTrialRequest(newEntity);
		});
	}

	@Test
	void testCreateTrialRequestWithInvalidAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			AdopterEntity invalid = new AdopterEntity();
			invalid.setId(0L);

			TrialRequestEntity newEntity = factory.manufacturePojo(TrialRequestEntity.class);
			newEntity.setAdopter(invalid);
			newEntity.setPet(petList.get(1));
			newEntity.setDate(new Date());
			trialRequestService.createTrialRequest(newEntity);
		});
	}

	@Test
	void testCreateTrialRequestWithAdopterAlreadyActive() {
		assertThrows(IllegalOperationException.class, () -> {
			TrialRequestEntity newEntity = factory.manufacturePojo(TrialRequestEntity.class);
			newEntity.setAdopter(adopterList.get(0));
			newEntity.setPet(petList.get(3));
			newEntity.setDate(new Date());
			trialRequestService.createTrialRequest(newEntity);
		});
	}

	@Test
	void testCreateTrialRequestForPetAlreadyInTrial() {
		assertThrows(IllegalOperationException.class, () -> {
			TrialRequestEntity newEntity = factory.manufacturePojo(TrialRequestEntity.class);
			newEntity.setAdopter(adopterList.get(1));
			newEntity.setPet(petList.get(0));
			newEntity.setDate(new Date());
			trialRequestService.createTrialRequest(newEntity);
		});
	}

	@Test
	void testCreateTrialRequestForAdoptedPet() {
		assertThrows(IllegalOperationException.class, () -> {
			TrialRequestEntity newEntity = factory.manufacturePojo(TrialRequestEntity.class);
			newEntity.setAdopter(adopterList.get(2));
			newEntity.setPet(petList.get(2));
			newEntity.setDate(new Date());
			trialRequestService.createTrialRequest(newEntity);
		});
	}

	@Test
	void testCreateTrialRequestWithNullDate() {
		assertThrows(IllegalOperationException.class, () -> {
			TrialRequestEntity newEntity = factory.manufacturePojo(TrialRequestEntity.class);
			newEntity.setAdopter(adopterList.get(3));
			newEntity.setPet(petList.get(3));
			newEntity.setDate(null);
			trialRequestService.createTrialRequest(newEntity);
		});
	}

	// ---------- getTrialRequests ----------

	@Test
	void testGetTrialRequests() {
		List<TrialRequestEntity> list = trialRequestService.getTrialRequests();
		assertEquals(trialRequestList.size(), list.size());
		for (TrialRequestEntity entity : list) {
			boolean found = false;
			for (TrialRequestEntity stored : trialRequestList) {
				if (entity.getId().equals(stored.getId()))
					found = true;
			}
			assertTrue(found);
		}
	}

	// ---------- getTrialRequest ----------

	@Test
	void testGetTrialRequest() throws EntityNotFoundException {
		TrialRequestEntity entity = trialRequestList.get(0);
		TrialRequestEntity result = trialRequestService.getTrialRequest(entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
	}

	@Test
	void testGetInvalidTrialRequest() {
		assertThrows(EntityNotFoundException.class, () -> {
			trialRequestService.getTrialRequest(0L);
		});
	}

	// ---------- updateTrialRequest ----------

	@Test
	void testUpdateTrialRequest() throws EntityNotFoundException, IllegalOperationException {
		TrialRequestEntity entity = trialRequestList.get(0);
		TrialRequestEntity pojoEntity = factory.manufacturePojo(TrialRequestEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setAdopter(entity.getAdopter());
		pojoEntity.setPet(entity.getPet());
		pojoEntity.setStatus("ACTIVA");
		pojoEntity.setDate(entity.getDate());

		trialRequestService.updateTrialRequest(entity.getId(), pojoEntity);

		TrialRequestEntity resp = entityManager.find(TrialRequestEntity.class, entity.getId());
		assertEquals(pojoEntity.getDescription(), resp.getDescription());
	}

	@Test
	void testUpdateInvalidTrialRequest() {
		assertThrows(EntityNotFoundException.class, () -> {
			TrialRequestEntity pojoEntity = factory.manufacturePojo(TrialRequestEntity.class);
			pojoEntity.setId(0L);
			trialRequestService.updateTrialRequest(0L, pojoEntity);
		});
	}

	@Test
	void testUpdateFinishedTrialRequest() {
		assertThrows(IllegalOperationException.class, () -> {
			TrialRequestEntity entity = trialRequestList.get(0);
			entity.setStatus("FINALIZADA");
			entityManager.merge(entity);

			TrialRequestEntity pojoEntity = factory.manufacturePojo(TrialRequestEntity.class);
			pojoEntity.setId(entity.getId());
			trialRequestService.updateTrialRequest(entity.getId(), pojoEntity);
		});
	}

	// ---------- deleteTrialRequest ----------

	@Test
	void testDeleteTrialRequest() throws EntityNotFoundException, IllegalOperationException {
		TrialRequestEntity entity = trialRequestList.get(0);
		trialRequestService.deleteTrialRequest(entity.getId());
		TrialRequestEntity deleted = entityManager.find(TrialRequestEntity.class, entity.getId());
		assertNull(deleted);
	}

	@Test
	void testDeleteInvalidTrialRequest() {
		assertThrows(EntityNotFoundException.class, () -> {
			trialRequestService.deleteTrialRequest(0L);
		});
	}

	@Test
	void testDeleteTrialRequestWithAssociatedReturn() {
		assertThrows(IllegalOperationException.class, () -> {
			TrialRequestEntity entity = trialRequestList.get(0);

			ReturnEntity returnEntity = factory.manufacturePojo(ReturnEntity.class);
			returnEntity.setTrialRequest(entity);
			entityManager.persist(returnEntity);

			trialRequestService.deleteTrialRequest(entity.getId());
		});
	}
}
