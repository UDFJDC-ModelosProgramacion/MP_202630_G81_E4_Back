package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequestEntity, Long> {

    @Query("SELECT a FROM AdoptionRequestEntity a " +
           "WHERE a.adopter.id = :adopterId " +
           "AND a.pet.id = :petId")
    List<AdoptionRequestEntity> findByAdopterIdAndPetId(
            @Param("adopterId") Long adopterId,
            @Param("petId") Long petId);

    List<AdoptionRequestEntity> findByAdopterId(Long adopterId);

}