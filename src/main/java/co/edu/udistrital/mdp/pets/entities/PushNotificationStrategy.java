package co.edu.udistrital.mdp.pets.entities;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("push")
@Slf4j 
public class PushNotificationStrategy implements NotificationStrategy {
 
	@Override
	public void sendMessage(String content, UserEntity user) {
		
		log.info("Enviando PUSH a " + user.getId() + ": " + content);
	}
 
}