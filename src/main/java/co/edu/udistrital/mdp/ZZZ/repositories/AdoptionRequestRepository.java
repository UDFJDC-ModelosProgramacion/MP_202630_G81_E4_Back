package co.edu.udistrital.mdp.ZZZ.repositories;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import co.edu.udistrital.mdp.ZZZ.entities.AdoptionRequestEntity;
 
@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequestEntity, Long> {

	List<AdoptionRequestEntity> findByAdopterIdAndPetId(Long adopterId, Long petId);

	List<AdoptionRequestEntity> findByAdopterId(Long adopterId);
 
}