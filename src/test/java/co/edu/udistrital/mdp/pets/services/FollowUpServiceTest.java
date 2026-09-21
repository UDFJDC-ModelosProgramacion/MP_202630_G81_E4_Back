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
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.FollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.FollowUpService;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(FollowUpService.class)
class FollowUpServiceTest {

	@Autowired
	private FollowUpService followUpService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<AdoptionEntity> adoptionList = new ArrayList<>();
	private List<PetEntity> petList = new ArrayList<>();
	private List<VeterinarianEntity> veterinarianList = new ArrayList<>();
	private List<FollowUpEntity> followUpList = new ArrayList<>();

	private AdoptionEntity inactiveAdoption;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from FollowUpEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from VeterinarianEntity").executeUpdate();
	}

	private void insertData() {
		for (int i = 0; i < 2; i++) {
			AdopterEntity adopter = factory.manufacturePojo(AdopterEntity.class);
			entityManager.persist(adopter);

			PetEntity pet = factory.manufacturePojo(PetEntity.class);
			pet.setAdoptionStatus("ADOPTADA");
			entityManager.persist(pet);
			petList.add(pet);

			AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
			adoption.setAdopter(adopter);
			adoption.setPet(pet);
			adoption.setStatus(i == 0 ? "ACTIVA" : "PENDIENTE");
			adoption.setDate(new Date());
			entityManager.persist(adoption);
			adoptionList.add(adoption);
		}
		inactiveAdoption = adoptionList.get(1);

		for (int i = 0; i < 3; i++) {
			VeterinarianEntity vet = factory.manufacturePojo(VeterinarianEntity.class);
			entityManager.persist(vet);
			veterinarianList.add(vet);
		}

		FollowUpEntity followUp = factory.manufacturePojo(FollowUpEntity.class);
		followUp.setAdoption(adoptionList.get(0));
		followUp.setPet(petList.get(0));
		followUp.setVeterinarian(veterinarianList.get(0));
		followUp.setDate(new Date());
		followUp.setNotes(null);
		entityManager.persist(followUp);
		followUpList.add(followUp);

		FollowUpEntity followUpWithNotes = factory.manufacturePojo(FollowUpEntity.class);
		followUpWithNotes.setAdoption(adoptionList.get(0));
		followUpWithNotes.setPet(petList.get(0));
		followUpWithNotes.setVeterinarian(veterinarianList.get(1));
		followUpWithNotes.setDate(new Date(followUp.getDate().getTime() + 86400000L));
		followUpWithNotes.setNotes("Chequeo realizado, mascota en buen estado");
		entityManager.persist(followUpWithNotes);
		followUpList.add(followUpWithNotes);
	}

	// ---------- createFollowUp ----------

	@Test
	void testCreateFollowUp() throws IllegalOperationException {
		FollowUpEntity newEntity = factory.manufacturePojo(FollowUpEntity.class);
		newEntity.setAdoption(adoptionList.get(0));
		newEntity.setPet(petList.get(0));
		newEntity.setVeterinarian(veterinarianList.get(2));
		newEntity.setDate(new Date(followUpList.get(1).getDate().getTime() + 86400000L));
		newEntity.setNotes(null);

		FollowUpEntity result = followUpService.createFollowUp(newEntity);
		assertNotNull(result);

		FollowUpEntity stored = entityManager.find(FollowUpEntity.class, result.getId());
		assertEquals(newEntity.getAdoption().getId(), stored.getAdoption().getId());
		assertEquals(newEntity.getVeterinarian().getId(), stored.getVeterinarian().getId());
	}

	@Test
	void testCreateFollowUpWithInactiveAdoption() {
		assertThrows(IllegalOperationException.class, () -> {
			FollowUpEntity newEntity = factory.manufacturePojo(FollowUpEntity.class);
			newEntity.setAdoption(inactiveAdoption);
			newEntity.setPet(petList.get(1));
			newEntity.setVeterinarian(veterinarianList.get(0));
			newEntity.setDate(new Date());
			followUpService.createFollowUp(newEntity);
		});
	}

	@Test
	void testCreateFollowUpWithDuplicateDate() {
		assertThrows(IllegalOperationException.class, () -> {
			FollowUpEntity newEntity = factory.manufacturePojo(FollowUpEntity.class);
			newEntity.setAdoption(adoptionList.get(0));
			newEntity.setPet(petList.get(0));
			newEntity.setVeterinarian(veterinarianList.get(2));
			newEntity.setDate(followUpList.get(0).getDate());
			followUpService.createFollowUp(newEntity);
		});
	}

	@Test
	void testCreateFollowUpWithNoVeterinarian() {
		assertThrows(IllegalOperationException.class, () -> {
			FollowUpEntity newEntity = factory.manufacturePojo(FollowUpEntity.class);
			newEntity.setAdoption(adoptionList.get(0));
			newEntity.setPet(petList.get(0));
			newEntity.setVeterinarian(null);
			newEntity.setDate(new Date(followUpList.get(1).getDate().getTime() + 86400000L));
			followUpService.createFollowUp(newEntity);
		});
	}

	@Test
	void testCreateFollowUpWithInvalidVeterinarian() {
		assertThrows(IllegalOperationException.class, () -> {
			VeterinarianEntity invalid = new VeterinarianEntity();
			invalid.setId(0L);

			FollowUpEntity newEntity = factory.manufacturePojo(FollowUpEntity.class);
			newEntity.setAdoption(adoptionList.get(0));
			newEntity.setPet(petList.get(0));
			newEntity.setVeterinarian(invalid);
			newEntity.setDate(new Date(followUpList.get(1).getDate().getTime() + 86400000L));
			followUpService.createFollowUp(newEntity);
		});
	}

	@Test
	void testCreateFollowUpWithNullDate() {
		assertThrows(IllegalOperationException.class, () -> {
			FollowUpEntity newEntity = factory.manufacturePojo(FollowUpEntity.class);
			newEntity.setAdoption(adoptionList.get(0));
			newEntity.setPet(petList.get(0));
			newEntity.setVeterinarian(veterinarianList.get(2));
			newEntity.setDate(null);
			followUpService.createFollowUp(newEntity);
		});
	}

	@Test
	void testCreateFollowUpWithNoPet() {
		assertThrows(IllegalOperationException.class, () -> {
			FollowUpEntity newEntity = factory.manufacturePojo(FollowUpEntity.class);
			newEntity.setAdoption(adoptionList.get(0));
			newEntity.setPet(null);
			newEntity.setVeterinarian(veterinarianList.get(2));
			newEntity.setDate(new Date(followUpList.get(1).getDate().getTime() + 86400000L));
			followUpService.createFollowUp(newEntity);
		});
	}

	@Test
	void testCreateFollowUpWithInvalidPet() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity invalid = new PetEntity();
			invalid.setId(0L);

			FollowUpEntity newEntity = factory.manufacturePojo(FollowUpEntity.class);
			newEntity.setAdoption(adoptionList.get(0));
			newEntity.setPet(invalid);
			newEntity.setVeterinarian(veterinarianList.get(2));
			newEntity.setDate(new Date(followUpList.get(1).getDate().getTime() + 86400000L));
			followUpService.createFollowUp(newEntity);
		});
	}

	// ---------- getFollowUps ----------

	@Test
	void testGetFollowUps() {
		List<FollowUpEntity> list = followUpService.getFollowUps();
		assertEquals(followUpList.size(), list.size());
		for (FollowUpEntity entity : list) {
			boolean found = false;
			for (FollowUpEntity stored : followUpList) {
				if (entity.getId().equals(stored.getId()))
					found = true;
			}
			assertTrue(found);
		}
	}

	// ---------- getFollowUp ----------

	@Test
	void testGetFollowUp() throws EntityNotFoundException {
		FollowUpEntity entity = followUpList.get(0);
		FollowUpEntity result = followUpService.getFollowUp(entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
	}

	@Test
	void testGetInvalidFollowUp() {
		assertThrows(EntityNotFoundException.class, () -> {
			followUpService.getFollowUp(0L);
		});
	}

	// ---------- updateFollowUp ----------

	@Test
	void testUpdateFollowUp() throws EntityNotFoundException, IllegalOperationException {
		FollowUpEntity entity = followUpList.get(0);
		FollowUpEntity pojoEntity = factory.manufacturePojo(FollowUpEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setAdoption(entity.getAdoption());
		pojoEntity.setPet(entity.getPet());
		pojoEntity.setVeterinarian(entity.getVeterinarian());
		pojoEntity.setDate(entity.getDate());
		pojoEntity.setNotes("Todo en orden");

		followUpService.updateFollowUp(entity.getId(), pojoEntity);

		FollowUpEntity resp = entityManager.find(FollowUpEntity.class, entity.getId());
		assertEquals("Todo en orden", resp.getNotes());
	}

	@Test
	void testUpdateInvalidFollowUp() {
		assertThrows(EntityNotFoundException.class, () -> {
			FollowUpEntity pojoEntity = factory.manufacturePojo(FollowUpEntity.class);
			pojoEntity.setId(0L);
			followUpService.updateFollowUp(0L, pojoEntity);
		});
	}

	@Test
	void testUpdateDateOfRegisteredFollowUp() {
		assertThrows(IllegalOperationException.class, () -> {
			FollowUpEntity entity = followUpList.get(1);
			FollowUpEntity pojoEntity = factory.manufacturePojo(FollowUpEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setAdoption(entity.getAdoption());
			pojoEntity.setPet(entity.getPet());
			pojoEntity.setVeterinarian(entity.getVeterinarian());
			pojoEntity.setNotes(entity.getNotes());
			pojoEntity.setDate(new Date(entity.getDate().getTime() + 500000L));
			followUpService.updateFollowUp(entity.getId(), pojoEntity);
		});
	}

	// ---------- deleteFollowUp ----------

	@Test
	void testDeleteFollowUp() throws EntityNotFoundException, IllegalOperationException {
		FollowUpEntity entity = followUpList.get(0);
		followUpService.deleteFollowUp(entity.getId());
		FollowUpEntity deleted = entityManager.find(FollowUpEntity.class, entity.getId());
		assertNull(deleted);
	}

	@Test
	void testDeleteInvalidFollowUp() {
		assertThrows(EntityNotFoundException.class, () -> {
			followUpService.deleteFollowUp(0L);
		});
	}

	@Test
	void testDeleteFollowUpWithRegisteredNotes() {
		assertThrows(IllegalOperationException.class, () -> {
			FollowUpEntity entity = followUpList.get(1);
			followUpService.deleteFollowUp(entity.getId());
		});
	}
}
