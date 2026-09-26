package co.edu.udistrital.mdp.pets.repositories;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
 
@Repository
public interface AdoptionRepository extends JpaRepository<AdoptionEntity, Long> {

	List<AdoptionEntity> findByAdopterId(Long adopterId);

	List<AdoptionEntity> findByPetId(Long petId);

	List<AdoptionEntity> findByAdopterIdAndPetId(Long adopterId, Long petId);
 
}
 