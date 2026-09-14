package co.edu.udistrital.mdp.ZZZ.services.notification;

import co.edu.udistrital.mdp.ZZZ.entities.UserEntity;

public interface NotificationStrategy {
    void sendMessage(String content, UserEntity user);
}