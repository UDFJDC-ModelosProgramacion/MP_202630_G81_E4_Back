package co.edu.udistrital.mdp.pets.services.notification;

import co.edu.udistrital.mdp.pets.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component("EMAIL")
public class EmailNotificationStrategy implements NotificationStrategy {
    @Override
    public void sendMessage(String content, UserEntity user) {
        System.out.println("Enviando EMAIL a " + user.getEmail() + ": " + content);
    }
}