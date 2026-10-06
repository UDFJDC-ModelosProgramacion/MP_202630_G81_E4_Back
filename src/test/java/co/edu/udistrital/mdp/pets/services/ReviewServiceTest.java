package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;

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

    // ---------- getReviews / getReview ----------

    @Test
    void testGetReviews() {
        List<ReviewEntity> list = reviewService.getReviews();

        assertEquals(reviewList.size(), list.size());
        for (ReviewEntity entity : list) {
            boolean found = reviewList.stream()
                .anyMatch(item -> item.getId().equals(entity.getId()));
            assertTrue(found);
        }
    }

    @Test
    void testGetReview() throws EntityNotFoundException {
        ReviewEntity entity = reviewList.get(0);

        ReviewEntity result = reviewService.getReview(entity.getId());

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getRating(), result.getRating());
        assertEquals(entity.getComment(), result.getComment());
    }

    @Test
    void testGetInvalidReview() {
        assertThrows(EntityNotFoundException.class,
            () -> reviewService.getReview(0L));
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

    static Stream<Arguments> invalidRatingOrComment() {
        return Stream.of(
            Arguments.of(null, "Great experience"),
            Arguments.of(6, "Great experience"),
            Arguments.of(5, null),
            Arguments.of(5, ""));
    }

    @ParameterizedTest
    @MethodSource("invalidRatingOrComment")
    void testCreateReviewWithInvalidRatingOrComment(Integer rating, String comment) {
        ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
        newEntity.setRating(rating);
        newEntity.setComment(comment);
        newEntity.setPet(pet);

        assertThrows(IllegalArgumentException.class,
            () -> reviewService.createReview(newEntity));
    }

    @Test
    void testCreateReviewWithNullPet() {
        ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
        newEntity.setRating(5);
        newEntity.setComment("Great experience");
        newEntity.setPet(null);

        assertThrows(IllegalArgumentException.class,
            () -> reviewService.createReview(newEntity));
    }

    @Test
    void testCreateReviewWithInvalidPet() {
        ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
        newEntity.setRating(5);
        newEntity.setComment("Great experience");
        PetEntity invalidPet = new PetEntity();
        invalidPet.setId(0L);
        newEntity.setPet(invalidPet);

        assertThrows(IllegalArgumentException.class,
            () -> reviewService.createReview(newEntity));
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
        ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
        newEntity.setId(0L);
        Long ownerId = owner.getId();

        assertThrows(IllegalArgumentException.class,
            () -> reviewService.updateReview(newEntity, ownerId));
    }

    @Test
    void testUpdateReviewNotAuthorized() {
        ReviewEntity entity = reviewList.get(0);
        Long otherAdopterId = otherAdopter.getId();

        assertThrows(IllegalArgumentException.class,
            () -> reviewService.updateReview(entity, otherAdopterId));
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
        Long ownerId = owner.getId();

        assertThrows(IllegalArgumentException.class,
            () -> reviewService.deleteReview(0L, ownerId));
    }

    @Test
    void testDeleteReviewNotAuthorized() {
        Long reviewId = reviewList.get(0).getId();
        Long otherAdopterId = otherAdopter.getId();

        assertThrows(IllegalArgumentException.class,
            () -> reviewService.deleteReview(reviewId, otherAdopterId));
    }
}