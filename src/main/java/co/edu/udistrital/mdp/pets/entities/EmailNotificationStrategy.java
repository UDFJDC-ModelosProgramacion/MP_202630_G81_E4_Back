package co.edu.udistrital.mdp.pets.entities;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("EMAIL")
@Slf4j 
public class EmailNotificationStrategy implements NotificationStrategy {
 
	@Override
	public void sendMessage(String content, UserEntity user) {
		log.info("Enviando EMAIL a " + user.getEmail() + ": " + content);
	}
 
}