package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionRequestService;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdoptionRequestService.class)
class AdoptionRequestServiceTest {

	@Autowired
	private AdoptionRequestService adoptionRequestService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<AdoptionRequestEntity> adoptionRequestList = new ArrayList<>();
	private List<AdopterEntity> adopterList = new ArrayList<>();
	private List<PetEntity> petList = new ArrayList<>();

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from AdoptionRequestEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
	}

	private void insertData() {
		for (int i = 0; i < 3; i++) {
			AdopterEntity adopter = factory.manufacturePojo(AdopterEntity.class);
			entityManager.persist(adopter);
			adopterList.add(adopter);
		}

		for (int i = 0; i < 3; i++) {
			PetEntity pet = factory.manufacturePojo(PetEntity.class);
			pet.setAdoptionStatus("DISPONIBLE");
			entityManager.persist(pet);
			petList.add(pet);
		}

		for (int i = 0; i < 3; i++) {
			AdoptionRequestEntity adoptionRequest = factory.manufacturePojo(AdoptionRequestEntity.class);
			adoptionRequest.setAdopter(adopterList.get(i));
			adoptionRequest.setPet(petList.get(i));
			adoptionRequest.setDateRequest(new Date());
			adoptionRequest.setStatus("PENDIENTE");
			entityManager.persist(adoptionRequest);
			adoptionRequestList.add(adoptionRequest);
		}
	}

	// ---------- createAdoptionRequest ----------

	@Test
	void testCreateAdoptionRequest() throws IllegalOperationException {
		AdoptionRequestEntity newEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
		newEntity.setAdopter(adopterList.get(0));
		newEntity.setPet(petList.get(1));
		newEntity.setDateRequest(new Date());
		newEntity.setStatus("PENDIENTE");

		AdoptionRequestEntity result = adoptionRequestService.createAdoptionRequest(newEntity);
		assertNotNull(result);

		AdoptionRequestEntity stored = entityManager.find(AdoptionRequestEntity.class, result.getId());
		assertEquals(newEntity.getAdopter().getId(), stored.getAdopter().getId());
		assertEquals(newEntity.getPet().getId(), stored.getPet().getId());
		assertEquals(newEntity.getStatus(), stored.getStatus());
	}

	@Test
	void testCreateAdoptionRequestWithNoAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionRequestEntity newEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
			newEntity.setAdopter(null);
			newEntity.setPet(petList.get(0));
			newEntity.setDateRequest(new Date());
			adoptionRequestService.createAdoptionRequest(newEntity);
		});
	}

	@Test
	void testCreateAdoptionRequestWithInvalidAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			AdopterEntity invalidAdopter = new AdopterEntity();
			invalidAdopter.setId(0L);

			AdoptionRequestEntity newEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
			newEntity.setAdopter(invalidAdopter);
			newEntity.setPet(petList.get(0));
			newEntity.setDateRequest(new Date());
			adoptionRequestService.createAdoptionRequest(newEntity);
		});
	}

	@Test
	void testCreateAdoptionRequestWithAdoptedPet() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity adoptedPet = factory.manufacturePojo(PetEntity.class);
			adoptedPet.setAdoptionStatus("ADOPTADA");
			entityManager.persist(adoptedPet);

			AdoptionRequestEntity newEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
			newEntity.setAdopter(adopterList.get(0));
			newEntity.setPet(adoptedPet);
			newEntity.setDateRequest(new Date());
			adoptionRequestService.createAdoptionRequest(newEntity);
		});
	}

	@Test
	void testCreateAdoptionRequestWithAvailablePet() throws IllegalOperationException {
		AdoptionRequestEntity newEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
		newEntity.setAdopter(adopterList.get(1));
		newEntity.setPet(petList.get(2));
		newEntity.setDateRequest(new Date());
		AdoptionRequestEntity result = adoptionRequestService.createAdoptionRequest(newEntity);
		assertNotNull(result);
	}

	@Test
	void testCreateAdoptionRequestWithNullDate() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionRequestEntity newEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
			newEntity.setAdopter(adopterList.get(0));
			newEntity.setPet(petList.get(1));
			newEntity.setDateRequest(null);
			adoptionRequestService.createAdoptionRequest(newEntity);
		});
	}

	@Test
	void testCreateAdoptionRequestWithDuplicatePending() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionRequestEntity newEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
			newEntity.setAdopter(adoptionRequestList.get(0).getAdopter());
			newEntity.setPet(adoptionRequestList.get(0).getPet());
			newEntity.setDateRequest(new Date());
			adoptionRequestService.createAdoptionRequest(newEntity);
		});
	}

	// ---------- getAdoptionRequests ----------

	@Test
	void testGetAdoptionRequests() {
		List<AdoptionRequestEntity> list = adoptionRequestService.getAdoptionRequests();
		assertEquals(adoptionRequestList.size(), list.size());
		for (AdoptionRequestEntity entity : list) {
			boolean found = false;
			for (AdoptionRequestEntity stored : adoptionRequestList) {
				if (entity.getId().equals(stored.getId()))
					found = true;
			}
			assertTrue(found);
		}
	}

	// ---------- getAdoptionRequest ----------

	@Test
	void testGetAdoptionRequest() throws EntityNotFoundException {
		AdoptionRequestEntity entity = adoptionRequestList.get(0);
		AdoptionRequestEntity result = adoptionRequestService.getAdoptionRequest(entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
		assertEquals(entity.getStatus(), result.getStatus());
	}

	@Test
	void testGetInvalidAdoptionRequest() {
		assertThrows(EntityNotFoundException.class, () -> {
			adoptionRequestService.getAdoptionRequest(0L);
		});
	}

	// ---------- updateAdoptionRequest ----------

	@Test
	void testUpdateAdoptionRequest() throws EntityNotFoundException, IllegalOperationException {
		AdoptionRequestEntity entity = adoptionRequestList.get(0);
		AdoptionRequestEntity pojoEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setAdopter(entity.getAdopter());
		pojoEntity.setPet(entity.getPet());
		pojoEntity.setDateRequest(new Date());
		pojoEntity.setStatus("RECHAZADO");

		adoptionRequestService.updateAdoptionRequest(entity.getId(), pojoEntity);

		AdoptionRequestEntity resp = entityManager.find(AdoptionRequestEntity.class, entity.getId());
		assertEquals(pojoEntity.getStatus(), resp.getStatus());
	}

	@Test
	void testUpdateInvalidAdoptionRequest() {
		assertThrows(EntityNotFoundException.class, () -> {
			AdoptionRequestEntity pojoEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
			pojoEntity.setId(0L);
			adoptionRequestService.updateAdoptionRequest(0L, pojoEntity);
		});
	}

	@Test
	void testUpdateCancelledAdoptionRequest() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionRequestEntity entity = adoptionRequestList.get(0);
			entity.setStatus("CANCELADO");
			entityManager.merge(entity);

			AdoptionRequestEntity pojoEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setStatus("PENDIENTE");
			adoptionRequestService.updateAdoptionRequest(entity.getId(), pojoEntity);
		});
	}

	@Test
	void testUpdateApproveWithActiveAdoption() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionRequestEntity entity = adoptionRequestList.get(1);

			AdoptionEntity activeAdoption = factory.manufacturePojo(AdoptionEntity.class);
			activeAdoption.setAdopter(entity.getAdopter());
			activeAdoption.setStatus("ACTIVA");
			entityManager.persist(activeAdoption);

			AdoptionRequestEntity pojoEntity = factory.manufacturePojo(AdoptionRequestEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setAdopter(entity.getAdopter());
			pojoEntity.setPet(entity.getPet());
			pojoEntity.setStatus("APROBADO");
			adoptionRequestService.updateAdoptionRequest(entity.getId(), pojoEntity);
		});
	}

	// ---------- deleteAdoptionRequest ----------

	@Test
	void testDeleteAdoptionRequest() throws EntityNotFoundException, IllegalOperationException {
		AdoptionRequestEntity entity = adoptionRequestList.get(0);
		adoptionRequestService.deleteAdoptionRequest(entity.getId());
		AdoptionRequestEntity deleted = entityManager.find(AdoptionRequestEntity.class, entity.getId());
		assertEquals(null, deleted);
	}

	@Test
	void testDeleteInvalidAdoptionRequest() {
		assertThrows(EntityNotFoundException.class, () -> {
			adoptionRequestService.deleteAdoptionRequest(0L);
		});
	}

	@Test
	void testDeleteApprovedAdoptionRequest() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionRequestEntity entity = adoptionRequestList.get(0);
			entity.setStatus("APROBADO");
			entityManager.merge(entity);
			adoptionRequestService.deleteAdoptionRequest(entity.getId());
		});
	}
}
