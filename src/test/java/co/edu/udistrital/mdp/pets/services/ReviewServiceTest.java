package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
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
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ReviewService.class)
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<ReviewEntity> reviewList = new ArrayList<>();

    private PetEntity pet;
    private AdopterEntity owner;
    private AdopterEntity otherAdopter;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from ReviewEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
    }

    private void insertData() {
        pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);

        owner = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(owner);

        otherAdopter = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(otherAdopter);

        for (int i = 0; i < 3; i++) {
            ReviewEntity review = factory.manufacturePojo(ReviewEntity.class);
            review.setRating(4);
            review.setPet(pet);
            review.setAdopter(owner);
            entityManager.persist(review);
            reviewList.add(review);
        }
    }

    // ---------- createReview ----------

    @Test
    void testCreateReview() {
        ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
        newEntity.setRating(5);
        newEntity.setComment("Great experience");
        newEntity.setPet(pet);
        newEntity.setAdopter(owner);

        ReviewEntity result = reviewService.createReview(newEntity);

        assertNotNull(result);
        ReviewEntity stored = entityManager.find(ReviewEntity.class, result.getId());
        assertEquals(newEntity.getRating(), stored.getRating());
        assertEquals(newEntity.getComment(), stored.getComment());
        assertEquals(pet.getId(), stored.getPet().getId());
    }

    @Test
    void testCreateReviewWithNullRating() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
            newEntity.setRating(null);
            newEntity.setComment("Great experience");
            newEntity.setPet(pet);
            reviewService.createReview(newEntity);
        });
    }

    @Test
    void testCreateReviewWithRatingOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
            newEntity.setRating(6);
            newEntity.setComment("Great experience");
            newEntity.setPet(pet);
            reviewService.createReview(newEntity);
        });
    }

    @Test
    void testCreateReviewWithNullComment() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
            newEntity.setRating(5);
            newEntity.setComment(null);
            newEntity.setPet(pet);
            reviewService.createReview(newEntity);
        });
    }

    @Test
    void testCreateReviewWithEmptyComment() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
            newEntity.setRating(5);
            newEntity.setComment("");
            newEntity.setPet(pet);
            reviewService.createReview(newEntity);
        });
    }

    @Test
    void testCreateReviewWithNullPet() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
            newEntity.setRating(5);
            newEntity.setComment("Great experience");
            newEntity.setPet(null);
            reviewService.createReview(newEntity);
        });
    }

    @Test
    void testCreateReviewWithInvalidPet() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
            newEntity.setRating(5);
            newEntity.setComment("Great experience");
            PetEntity invalidPet = new PetEntity();
            invalidPet.setId(0L);
            newEntity.setPet(invalidPet);
            reviewService.createReview(newEntity);
        });
    }

    // ---------- updateReview ----------

    @Test
    void testUpdateReview() {
        ReviewEntity entity = reviewList.get(0);
        entity.setComment("Updated comment");

        ReviewEntity result = reviewService.updateReview(entity, owner.getId());

        assertNotNull(result);
        ReviewEntity stored = entityManager.find(ReviewEntity.class, entity.getId());
        assertEquals("Updated comment", stored.getComment());
    }

    @Test
    void testUpdateReviewInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
            newEntity.setId(0L);
            reviewService.updateReview(newEntity, owner.getId());
        });
    }

    @Test
    void testUpdateReviewNotAuthorized() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewEntity entity = reviewList.get(0);
            reviewService.updateReview(entity, otherAdopter.getId());
        });
    }

    // ---------- deleteReview ----------

    @Test
    void testDeleteReview() {
        ReviewEntity entity = reviewList.get(0);

        reviewService.deleteReview(entity.getId(), owner.getId());

        ReviewEntity deleted = entityManager.find(ReviewEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidReview() {
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.deleteReview(0L, owner.getId());
        });
    }

    @Test
    void testDeleteReviewNotAuthorized() {
        assertThrows(IllegalArgumentException.class, () -> {
            ReviewEntity entity = reviewList.get(0);
            reviewService.deleteReview(entity.getId(), otherAdopter.getId());
        });
    }
}