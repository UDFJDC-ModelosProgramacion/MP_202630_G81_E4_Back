package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.pets.entities.ReviewEntity;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByAdopterId(Long adopterId);

}