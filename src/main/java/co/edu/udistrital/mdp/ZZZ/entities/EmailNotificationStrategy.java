package co.edu.udistrital.mdp.ZZZ.entities;
 

public class EmailNotificationStrategy implements NotificationStrategy {
 
	@Override
	public void sendMessage(String content, UserEntity user) {

		System.out.println("Enviando EMAIL a " + user.getEmail() + ": " + content);
	}
 
}