package co.edu.udistrital.mdp.ZZZ.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.udistrital.mdp.ZZZ.entities.ShelterEventEntity;

@Repository
public interface ShelterEventRepository extends JpaRepository<ShelterEventEntity, Long> {
}