package co.edu.udistrital.mdp.ZZZ.entities;
 

public class PushNotificationStrategy implements NotificationStrategy {
 
	@Override
	public void sendMessage(String content, UserEntity user) {
		
		System.out.println("Enviando PUSH a " + user.getUserId() + ": " + content);
	}
 
}