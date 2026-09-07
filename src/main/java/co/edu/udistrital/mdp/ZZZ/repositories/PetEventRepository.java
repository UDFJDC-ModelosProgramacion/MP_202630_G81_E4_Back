package co.edu.udistrital.mdp.ZZZ.repositories;

import co.edu.udistrital.mdp.ZZZ.entities.PetEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetEventRepository extends JpaRepository<PetEventEntity, Long> {
}