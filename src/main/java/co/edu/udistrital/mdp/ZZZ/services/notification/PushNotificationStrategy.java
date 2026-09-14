package co.edu.udistrital.mdp.ZZZ.services.notification;

import co.edu.udistrital.mdp.ZZZ.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component("PUSH")
public class PushNotificationStrategy implements NotificationStrategy {
    @Override
    public void sendMessage(String content, UserEntity user) {
        System.out.println("Enviando PUSH a " + user.getId() + ": " + content);
    }
}