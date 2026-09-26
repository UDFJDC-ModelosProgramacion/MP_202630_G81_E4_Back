package co.edu.udistrital.mdp.pets.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.ReviewRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service 
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private PetRepository petRepository;

    @Transactional
    public ReviewEntity createReview(ReviewEntity review) {
        log.info("Inicia proceso de creación de la reseña");
        if(review.getRating() == null || review.getRating() < 1 || review.getRating() > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5");

        if(review.getComment() == null || review.getComment().isEmpty())
            throw new IllegalArgumentException("Comment cannot be null or empty");

        if(review.getPet() == null || !petRepository.existsById(review.getPet().getId()))
            throw new IllegalArgumentException("Pet does not exist");

        return reviewRepository.save(review);
    }

    @Transactional
    public ReviewEntity updateReview(ReviewEntity review, Long requestingUserId) {
        log.info("Inicia proceso de actualización de la reseña");
        if (review.getId() == null || !reviewRepository.existsById(review.getId()))
            throw new IllegalArgumentException("Review does not exist");

        if(!review.getAdopter().getId().equals(requestingUserId))
            throw new IllegalArgumentException("User is not authorized to update this review");

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long requestingUserId) {
        log.info("Inicia proceso de eliminación de la reseña");
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review does not exist"));

        if(!review.getAdopter().getId().equals(requestingUserId))
            throw new IllegalArgumentException("User is not authorized to delete this review");

        reviewRepository.delete(review);
    }
}

