package co.edu.udistrital.mdp.pets.entities;
 

public class SMSNotificationStrategy implements NotificationStrategy {
 
	@Override
	public void sendMessage(String content, UserEntity user) {

		System.out.println("Enviando SMS a " + user.getPhone() + ": " + content);
	}
 
}