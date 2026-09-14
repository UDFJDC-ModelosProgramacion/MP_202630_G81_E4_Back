package co.edu.udistrital.mdp.ZZZ.services.notification;

import co.edu.udistrital.mdp.ZZZ.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component("SMS")
public class SMSNotificationStrategy implements NotificationStrategy {
    @Override
    public void sendMessage(String content, UserEntity user) {
        System.out.println("Enviando SMS a " + user.getPhone() + ": " + content);
    }
}