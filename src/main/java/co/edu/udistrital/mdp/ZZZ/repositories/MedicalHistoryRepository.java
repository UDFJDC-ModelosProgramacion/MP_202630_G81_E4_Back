package co.edu.udistrital.mdp.ZZZ.repositories;

import co.edu.udistrital.mdp.ZZZ.entities.MedicalHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistoryEntity, Long> {
}