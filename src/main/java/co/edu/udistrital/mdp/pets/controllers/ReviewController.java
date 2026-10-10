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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.ReviewDTO;
import co.edu.udistrital.mdp.pets.dto.ReviewDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ReviewService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "reviews" resource, including its association
 * endpoints. No class-level @RequestMapping on purpose: the association
 * routes (/pets/{id}/reviews, /adopters/{id}/reviews) live outside
 * /reviews, so every method declares its own full path.
 */
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    private final ModelMapper modelMapper;

    @GetMapping("/reviews")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ReviewDetailDTO> findAll() {
        List<ReviewEntity> reviews = reviewService.getReviews();
        return modelMapper.map(reviews, new TypeToken<List<ReviewDetailDTO>>() {
        }.getType());
    }

    @GetMapping("/reviews/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ReviewDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        ReviewEntity reviewEntity = reviewService.getReview(id);
        return modelMapper.map(reviewEntity, ReviewDetailDTO.class);
    }

    @PostMapping("/reviews")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ReviewDTO create(@RequestBody ReviewDetailDTO reviewDTO)
            throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity reviewEntity = modelMapper.map(reviewDTO, ReviewEntity.class);

        PetEntity pet = new PetEntity();
        pet.setId(reviewDTO.getPet().getId());
        reviewEntity.setPet(pet);

        AdopterEntity adopter = new AdopterEntity();
        adopter.setId(reviewDTO.getAdopter().getId());
        reviewEntity.setAdopter(adopter);

        ReviewEntity saved = reviewService.createReview(reviewEntity);
        return modelMapper.map(saved, ReviewDTO.class);
    }

    @PutMapping("/reviews/{id}")
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

    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @RequestParam Long userId)
            throws EntityNotFoundException, IllegalOperationException {
        reviewService.deleteReview(id, userId);
    }

    @GetMapping("/pets/{petId}/reviews")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ReviewDetailDTO> findByPet(@PathVariable Long petId) throws EntityNotFoundException {
        List<ReviewEntity> reviews = reviewService.getReviewsByPet(petId);
        return modelMapper.map(reviews, new TypeToken<List<ReviewDetailDTO>>() {
        }.getType());
    }

    @GetMapping("/adopters/{adopterId}/reviews")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ReviewDetailDTO> findByAdopter(@PathVariable Long adopterId) throws EntityNotFoundException {
        List<ReviewEntity> reviews = reviewService.getReviewsByAdopter(adopterId);
        return modelMapper.map(reviews, new TypeToken<List<ReviewDetailDTO>>() {
        }.getType());
    }
}