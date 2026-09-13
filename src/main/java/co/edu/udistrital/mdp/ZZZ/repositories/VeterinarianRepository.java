package co.edu.udistrital.mdp.ZZZ.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.udistrital.mdp.ZZZ.entities.VeterinarianEntity;

@Repository
public interface VeterinarianRepository extends JpaRepository<VeterinarianEntity, Long> {
    Optional<VeterinarianEntity> findByVeterinarianId(String veterinarianId);
}