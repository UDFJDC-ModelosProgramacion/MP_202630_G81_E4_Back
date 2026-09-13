package co.edu.udistrital.mdp.ZZZ.repositories;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import co.edu.udistrital.mdp.ZZZ.entities.TrialRequestEntity;
 
@Repository
public interface TrialRequestRepository extends JpaRepository<TrialRequestEntity, Long> {

	List<TrialRequestEntity> findByAdopterId(Long adopterId);

	List<TrialRequestEntity> findByPetId(Long petId);
 
}
 