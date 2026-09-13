package co.edu.udistrital.mdp.ZZZ.services;

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

import co.edu.udistrital.mdp.ZZZ.entities.AdopterEntity;
import co.edu.udistrital.mdp.ZZZ.entities.AdoptionEntity;
import co.edu.udistrital.mdp.ZZZ.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.ZZZ.entities.FollowUpEntity;
import co.edu.udistrital.mdp.ZZZ.entities.PetEntity;
import co.edu.udistrital.mdp.ZZZ.entities.TrialRequestEntity;
import co.edu.udistrital.mdp.ZZZ.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.ZZZ.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.ZZZ.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdoptionService.class)
class AdoptionServiceTest {

	@Autowired
	private AdoptionService adoptionService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<AdopterEntity> adopterList = new ArrayList<>();
	private List<PetEntity> petList = new ArrayList<>();
	private List<AdoptionEntity> adoptionList = new ArrayList<>();

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from FollowUpEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from TrialRequestEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdoptionRequestEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
	}

	/**
	 * adopterList/petList index 0,1,2: ya tienen una Adoption ACTIVA.
	 * index 3: tiene un AdoptionRequest aprobado, sin Adoption (caso válido de creación).
	 * index 4: tiene un AdoptionRequest aprobado y una TrialRequest ACTIVA (conflicto).
	 */
	private void insertData() {
		for (int i = 0; i < 5; i++) {
			AdopterEntity adopter = factory.manufacturePojo(AdopterEntity.class);
			entityManager.persist(adopter);
			adopterList.add(adopter);

			PetEntity pet = factory.manufacturePojo(PetEntity.class);
			pet.setAdoptionStatus("DISPONIBLE");
			entityManager.persist(pet);
			petList.add(pet);

			AdoptionRequestEntity request = factory.manufacturePojo(AdoptionRequestEntity.class);
			request.setAdopter(adopter);
			request.setPet(pet);
			request.setDateRequest(new Date());
			request.setStatus("APROBADO");
			entityManager.persist(request);
		}

		for (int i = 0; i < 3; i++) {
			AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
			adoption.setAdopter(adopterList.get(i));
			adoption.setPet(petList.get(i));
			adoption.setStatus("ACTIVA");
			adoption.setDate(new Date());
			entityManager.persist(adoption);
			adoptionList.add(adoption);
		}

		TrialRequestEntity trial = factory.manufacturePojo(TrialRequestEntity.class);
		trial.setAdopter(adopterList.get(4));
		trial.setPet(petList.get(4));
		trial.setStatus("ACTIVA");
		trial.setDate(new Date());
		entityManager.persist(trial);
	}

	// ---------- createAdoption ----------

	@Test
	void testCreateAdoption() throws IllegalOperationException {
		AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
		newEntity.setAdopter(adopterList.get(3));
		newEntity.setPet(petList.get(3));
		newEntity.setStatus("ACTIVA");
		newEntity.setDate(new Date());

		AdoptionEntity result = adoptionService.createAdoption(newEntity);
		assertNotNull(result);

		AdoptionEntity stored = entityManager.find(AdoptionEntity.class, result.getId());
		assertEquals(newEntity.getAdopter().getId(), stored.getAdopter().getId());
		assertEquals(newEntity.getPet().getId(), stored.getPet().getId());
	}

	@Test
	void testCreateAdoptionWithNoAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setAdopter(null);
			newEntity.setPet(petList.get(3));
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithInvalidAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			AdopterEntity invalid = new AdopterEntity();
			invalid.setId(0L);

			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setAdopter(invalid);
			newEntity.setPet(petList.get(3));
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithNoPet() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setAdopter(adopterList.get(3));
			newEntity.setPet(null);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithInvalidPet() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity invalid = new PetEntity();
			invalid.setId(0L);

			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setAdopter(adopterList.get(3));
			newEntity.setPet(invalid);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionForPetWithActiveAdoption() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setAdopter(adopterList.get(0));
			newEntity.setPet(petList.get(0));
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithoutApprovedRequest() {
		assertThrows(IllegalOperationException.class, () -> {
			AdopterEntity newAdopter = factory.manufacturePojo(AdopterEntity.class);
			entityManager.persist(newAdopter);
			PetEntity newPet = factory.manufacturePojo(PetEntity.class);
			newPet.setAdoptionStatus("DISPONIBLE");
			entityManager.persist(newPet);

			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setAdopter(newAdopter);
			newEntity.setPet(newPet);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithActiveTrialRequest() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setAdopter(adopterList.get(4));
			newEntity.setPet(petList.get(4));
			adoptionService.createAdoption(newEntity);
		});
	}

	// ---------- getAdoptions ----------

	@Test
	void testGetAdoptions() {
		List<AdoptionEntity> list = adoptionService.getAdoptions();
		assertEquals(adoptionList.size(), list.size());
		for (AdoptionEntity entity : list) {
			boolean found = false;
			for (AdoptionEntity stored : adoptionList) {
				if (entity.getId().equals(stored.getId()))
					found = true;
			}
			assertTrue(found);
		}
	}

	// ---------- getAdoption ----------

	@Test
	void testGetAdoption() throws EntityNotFoundException {
		AdoptionEntity entity = adoptionList.get(0);
		AdoptionEntity result = adoptionService.getAdoption(entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
	}

	@Test
	void testGetInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			adoptionService.getAdoption(0L);
		});
	}

	// ---------- updateAdoption ----------

	@Test
	void testUpdateAdoption() throws EntityNotFoundException, IllegalOperationException {
		AdoptionEntity entity = adoptionList.get(0);
		AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setAdopter(entity.getAdopter());
		pojoEntity.setPet(entity.getPet());
		pojoEntity.setStatus("FINALIZADA");
		pojoEntity.setDate(entity.getDate());

		adoptionService.updateAdoption(entity.getId(), pojoEntity);

		AdoptionEntity resp = entityManager.find(AdoptionEntity.class, entity.getId());
		assertEquals("FINALIZADA", resp.getStatus());
	}

	@Test
	void testUpdateInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
			pojoEntity.setId(0L);
			adoptionService.updateAdoption(0L, pojoEntity);
		});
	}

	@Test
	void testUpdateAlreadyFinishedAdoption() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity entity = adoptionList.get(1);
			entity.setStatus("FINALIZADA");
			entityManager.merge(entity);

			AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setStatus("ACTIVA");
			adoptionService.updateAdoption(entity.getId(), pojoEntity);
		});
	}

	@Test
	void testConfirmAdoptionWithoutFollowUp() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity entity = adoptionList.get(2);
			AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setStatus("ACTIVA");
			adoptionService.updateAdoption(entity.getId(), pojoEntity);
		});
	}

	// ---------- deleteAdoption ----------

	@Test
	void testDeleteAdoption() throws EntityNotFoundException, IllegalOperationException {
		AdoptionEntity entity = adoptionList.get(0);
		adoptionService.deleteAdoption(entity.getId());
		AdoptionEntity deleted = entityManager.find(AdoptionEntity.class, entity.getId());
		assertNull(deleted);
	}

	@Test
	void testDeleteInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			adoptionService.deleteAdoption(0L);
		});
	}

	@Test
	void testDeleteAdoptionWithRegisteredFollowUpNotes() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity entity = adoptionList.get(1);

			VeterinarianEntity vet = factory.manufacturePojo(VeterinarianEntity.class);
			entityManager.persist(vet);

			FollowUpEntity followUp = factory.manufacturePojo(FollowUpEntity.class);
			followUp.setAdoption(entity);
			followUp.setPet(entity.getPet());
			followUp.setVeterinarian(vet);
			followUp.setDate(new Date());
			followUp.setNotes("Chequeo registrado con notas");
			entityManager.persist(followUp);

			entityManager.flush();
			entityManager.clear();

			adoptionService.deleteAdoption(entity.getId());
		});
	}
}
