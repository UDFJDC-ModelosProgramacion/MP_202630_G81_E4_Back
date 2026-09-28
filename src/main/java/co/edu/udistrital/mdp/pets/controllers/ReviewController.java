package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.ReviewDTO;
import co.edu.udistrital.mdp.pets.dto.ReviewDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ReviewService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "reviews" resource.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    private final ModelMapper modelMapper;

    /**
     * Returns all reviews existing in the application.
     *
     * @return JSONArray {@link ReviewDetailDTO}. Empty list if none exist.
     */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ReviewDetailDTO> findAll() {
        List<ReviewEntity> reviews = reviewService.getReviews();
        return modelMapper.map(reviews, new TypeToken<List<ReviewDetailDTO>>() {
        }.getType());
    }

    /**
     * Returns the review with the ID received in the URL.
     *
     * @param id Identifier of the review.
     * @return JSON {@link ReviewDetailDTO}
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ReviewDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        ReviewEntity reviewEntity = reviewService.getReview(id);
        return modelMapper.map(reviewEntity, ReviewDetailDTO.class);
    }

    /**
     * Creates a new review with the information received in the body.
     *
     * @param reviewDTO {@link ReviewDTO} The review to be saved.
     * @return JSON {@link ReviewDTO} The saved review with its ID.
     */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ReviewDTO create(@RequestBody ReviewDTO reviewDTO)
            throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity reviewEntity = reviewService.createReview(modelMapper.map(reviewDTO, ReviewEntity.class));
        return modelMapper.map(reviewEntity, ReviewDTO.class);
    }

    /**
     * Updates the review with the ID received in the URL. Only its author
     * (the adopter) can do it. The pet and adopter of the review do not change.
     *
     * @param id        Identifier of the review.
     * @param userId    Identifier of the user making the request.
     * @param reviewDTO {@link ReviewDTO} The new rating, comment and date.
     * @return JSON {@link ReviewDTO} The updated review.
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ReviewDTO update(@PathVariable Long id, @RequestParam Long userId, @RequestBody ReviewDTO reviewDTO)
            throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity existing = reviewService.getReview(id);
        ReviewEntity changes = modelMapper.map(reviewDTO, ReviewEntity.class);
        changes.setId(id);
        changes.setPet(existing.getPet());
        changes.setAdopter(existing.getAdopter());
        ReviewEntity updated = reviewService.updateReview(changes, userId);
        return modelMapper.map(updated, ReviewDTO.class);
    }

    /**
     * Deletes the review with the ID received in the URL.
     * Only its author (the adopter) can do it.
     *
     * @param id     Identifier of the review.
     * @param userId Identifier of the user making the request.
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @RequestParam Long userId)
            throws EntityNotFoundException, IllegalOperationException {
        reviewService.deleteReview(id, userId);
    }
}