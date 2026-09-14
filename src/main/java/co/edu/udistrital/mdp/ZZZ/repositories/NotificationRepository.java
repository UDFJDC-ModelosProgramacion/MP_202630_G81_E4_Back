package co.edu.udistrital.mdp.ZZZ.repositories;

import java.util.List;

import co.edu.udistrital.mdp.ZZZ.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdAndReadFalse(Long userId);

}