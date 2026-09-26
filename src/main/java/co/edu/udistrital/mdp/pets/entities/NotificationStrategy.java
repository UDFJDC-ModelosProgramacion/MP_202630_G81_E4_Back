package co.edu.udistrital.mdp.pets.entities;
 
/*
Interfaz del patrón Strategy para el envío de notificaciones.
*/
public interface NotificationStrategy {
 
	void sendMessage(String content, UserEntity user);
 
}
 