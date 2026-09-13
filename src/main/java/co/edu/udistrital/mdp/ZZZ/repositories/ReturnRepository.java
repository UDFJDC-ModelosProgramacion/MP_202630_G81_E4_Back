package co.edu.udistrital.mdp.ZZZ.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.udistrital.mdp.ZZZ.entities.ReturnEntity;

@Repository
public interface ReturnRepository extends JpaRepository<ReturnEntity, Long> {

    List<ReturnEntity> findByTrialRequestId(Long trialRequestId);
    
}