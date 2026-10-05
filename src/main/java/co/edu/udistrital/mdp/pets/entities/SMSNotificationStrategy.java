package co.edu.udistrital.mdp.pets.entities;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("sms")
@Slf4j
public class SMSNotificationStrategy implements NotificationStrategy {
 
	@Override
	public void sendMessage(String content, UserEntity user) {

		log.info("Enviando SMS a " + user.getPhone() + ": " + content);
	}
 
}