package co.edu.udistrital.mdp.ZZZ.entities;
 
/*
 Interfaz del patrón Strategy para calcular el puntaje de compatibilidad
 */
public interface CompatibilityStrategy {
 
	double calculateScore(AdopterEntity adopter, PetEntity pet);
 
}