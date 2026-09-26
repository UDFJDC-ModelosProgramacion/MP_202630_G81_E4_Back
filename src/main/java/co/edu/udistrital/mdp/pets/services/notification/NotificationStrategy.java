package co.edu.udistrital.mdp.pets.services.notification;

import co.edu.udistrital.mdp.pets.entities.UserEntity;

public interface NotificationStrategy {
    void sendMessage(String content, UserEntity user);
}