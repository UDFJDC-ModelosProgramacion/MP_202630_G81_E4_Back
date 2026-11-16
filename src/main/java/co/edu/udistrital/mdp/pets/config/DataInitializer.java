package co.edu.udistrital.mdp.pets.config;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.entities.FollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.entities.ReturnEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.entities.TrialRequestEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRequestRepository;
import co.edu.udistrital.mdp.pets.repositories.FollowUpRepository;
import co.edu.udistrital.mdp.pets.repositories.MedicalHistoryRepository;
import co.edu.udistrital.mdp.pets.repositories.MessageRepository;
import co.edu.udistrital.mdp.pets.repositories.NotificationRepository;
import co.edu.udistrital.mdp.pets.repositories.PetEventRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.ReturnRepository;
import co.edu.udistrital.mdp.pets.repositories.ReviewRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterEventRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.repositories.TrialRequestRepository;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;
import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "app.seed", havingValue = "true")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final AdopterRepository adopterRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final ShelterRepository shelterRepository;
    private final ShelterEventRepository shelterEventRepository;
    private final PetRepository petRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PetEventRepository petEventRepository;
    private final AdoptionRequestRepository adoptionRequestRepository;
    private final AdoptionRepository adoptionRepository;
    private final TrialRequestRepository trialRequestRepository;
    private final ReturnRepository returnRepository;
    private final FollowUpRepository followUpRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("[DataInitializer] La base de datos ya contiene registros. Se omite el seed.");
            return;
        }

        List<ShelterEntity> shelters = seedShelters();
        ShelterEntity shelter1 = shelters.get(0);
        ShelterEntity shelter2 = shelters.get(1);

        List<AdopterEntity> adopters = seedAdopters();
        AdopterEntity maria = adopters.get(0);   // userId = 1, adopterId = 1
        AdopterEntity carlos = adopters.get(1);  // userId = 2, adopterId = 2
        AdopterEntity sofia = adopters.get(2);   // userId = 3, adopterId = 3

        List<VeterinarianEntity> vets = seedVeterinarians(shelter1, shelter2);
        UserEntity coordinator = seedCoordinatorUser(); // userId = 8

        List<PetEntity> pets = seedPets(shelter1, shelter2);
        seedMedicalHistories(pets);
        seedPetEvents(pets);

        seedAdoptionRequests(adopters, pets);
        List<AdoptionEntity> adoptions = seedAdoptions(maria, carlos, pets);
        seedTrialsAndReturns(adopters, shelters, pets);
        seedFollowUps(adoptions, pets, vets);
        seedReviews(adopters, pets);
        seedNotifications(maria, carlos, adoptions, pets);
        seedMessages(maria, coordinator, vets.get(0));

        logSummary(shelters, adopters, vets, pets, adoptions);
    }

    private List<ShelterEntity> seedShelters() {
        ShelterEntity shelter1 = new ShelterEntity();
        shelter1.setNit(900111222);
        shelter1.setDescription("Huellitas del Norte - Refugio y centro de adopción integral");
        shelter1.setCapacity("50");
        shelter1.setCity("Bogotá");
        shelter1.setPhoto("https://images.unsplash.com/photo-1601758228041-f3b2795255f1");
        shelter1.setVideo("https://www.youtube.com/watch?v=huellitas-norte");
        shelter1 = shelterRepository.save(shelter1);

        ShelterEntity shelter2 = new ShelterEntity();
        shelter2.setNit(900333444);
        shelter2.setDescription("Patitas del Sur - Hogar de paso y rehabilitación animal");
        shelter2.setCapacity("35");
        shelter2.setCity("Bogotá");
        shelter2.setPhoto("https://images.unsplash.com/photo-1548199973-03cce0bbc87b");
        shelter2.setVideo("https://www.youtube.com/watch?v=patitas-sur");
        shelter2 = shelterRepository.save(shelter2);

        ShelterEventEntity event = new ShelterEventEntity();
        event.setEventId("EVT-SH-01");
        event.setName("Jornada de Adopción Fin de Semana");
        event.setDescription("Encuentro abierto para conocer perros y gatos listos para adopción.");
        event.setDate(toDate(2026, 11, 22));
        event.setType("JORNADA_ADOPCION");
        event.setShelter(shelter1);
        shelterEventRepository.save(event);

        return List.of(shelter1, shelter2);
    }

    private List<AdopterEntity> seedAdopters() {
        // ID 1: María Gómez (debe ser la primera entidad de usuario guardada)
        AdopterEntity maria = createAdopter(
                "María Gómez",
                "maria.gomez@example.com",
                "3104567890",
                "Clave123*",
                "Calle 127 # 15-40, Bogotá",
                "Ingeniera de Sistemas");

        // ID 2: Carlos Mendoza
        AdopterEntity carlos = createAdopter(
                "Carlos Mendoza",
                "carlos.mendoza@example.com",
                "3119876543",
                "Clave123*",
                "Carrera 7 # 45-12, Bogotá",
                "Arquitecto");

        // ID 3: Sofía Ramírez
        AdopterEntity sofia = createAdopter(
                "Sofía Ramírez",
                "sofia.ramirez@example.com",
                "3205551234",
                "Clave123*",
                "Avenida Suba # 100-25, Bogotá",
                "Diseñadora Gráfica");

        return List.of(maria, carlos, sofia);
    }

    private AdopterEntity createAdopter(
            String name, String email, String phone, String password, String address, String occupation) {
        AdopterEntity adopter = new AdopterEntity();
        adopter.setName(name);
        adopter.setEmail(email);
        adopter.setPassword(password);
        adopter.setPhone(phone);
        adopter.setAddress(address);
        adopter.setOccupation(occupation);
        // Poblar también los campos heredados en UserEntity (herencia JOINED)
        ((UserEntity) adopter).setPhone(phone);
        ((UserEntity) adopter).setAddress(address);
        return adopterRepository.save(adopter);
    }

    private List<VeterinarianEntity> seedVeterinarians(ShelterEntity shelter1, ShelterEntity shelter2) {
        // IDs 4, 5, 6 en Refugio 1
        VeterinarianEntity vet1 = createVet(
                "Dra. Laura Gómez", "laura.gomez@huellitas.org", "3001112233", "Vet123*",
                "Calle 134 # 9-20, Bogotá", "MV-10231", "Medicina Interna", "Lunes a Viernes 08:00 - 17:00", shelter1);

        VeterinarianEntity vet2 = createVet(
                "Dr. Carlos Ruiz", "carlos.ruiz@huellitas.org", "3002223344", "Vet123*",
                "Calle 140 # 12-15, Bogotá", "MV-20877", "Cirugía y Ortopedia", "Martes a Sábado 09:00 - 18:00", shelter1);

        VeterinarianEntity vet3 = createVet(
                "Dra. Ana Torres", "ana.torres@huellitas.org", "3003334455", "Vet123*",
                "Carrera 15 # 124-30, Bogotá", "MV-30412", "Dermatología y Nutrición", "Lunes a Viernes 10:00 - 19:00", shelter1);

        // ID 7 en Refugio 2
        VeterinarianEntity vet4 = createVet(
                "Dr. Andrés Castro", "andres.castro@patitas.org", "3004445566", "Vet123*",
                "Calle 40 Sur # 72-10, Bogotá", "MV-40519", "Medicina Preventiva y Exóticos", "Lunes a Sábado 08:00 - 16:00", shelter2);

        return List.of(vet1, vet2, vet3, vet4);
    }

    private VeterinarianEntity createVet(
            String name, String email, String phone, String password, String address,
            String vetLicense, String specialty, String availability, ShelterEntity shelter) {
        VeterinarianEntity vet = new VeterinarianEntity();
        vet.setName(name);
        vet.setEmail(email);
        vet.setPhone(phone);
        vet.setPassword(password);
        vet.setAddress(address);
        vet.setVeterinarianId(vetLicense);
        vet.setSpecialty(specialty);
        vet.setAvailability(availability);
        vet.setShelter(shelter);
        return veterinarianRepository.save(vet);
    }

    private UserEntity seedCoordinatorUser() {
        // ID 8: Usuario administrativo del refugio para mensajería
        UserEntity coordinator = new UserEntity();
        coordinator.setName("Coordinación Huellitas del Norte");
        coordinator.setEmail("contacto@huellitas.org");
        coordinator.setPhone("6017654321");
        coordinator.setPassword("Admin123*");
        coordinator.setAddress("Calle 127 # 15-40, Bogotá");
        return userRepository.save(coordinator);
    }

    private List<PetEntity> seedPets(ShelterEntity shelter1, ShelterEntity shelter2) {
        // ID 1: Luna (HU15: Labrador, 2 años, hembra, mediana, disponible)
        PetEntity luna = createPet(
                "Luna", "Perro", "Labrador", 2, "Hembra", "Mediana", 24.5,
                "Saludable", "DISPONIBLE",
                "Muy sociable, juguetona y acostumbrada a convivir con niños.",
                "2026-08-10", "https://images.unsplash.com/photo-1552053831-71594a27632d", shelter1);

        // ID 2: Max (HU15: en periodo de prueba, sin reseñas)
        PetEntity max = createPet(
                "Max", "Perro", "Criollo", 3, "Macho", "Grande", 28.0,
                "Saludable", "EN_PRUEBA",
                "Activo, obediente y excelente compañero para caminatas largas.",
                "2026-07-15", "https://images.unsplash.com/photo-1587300003388-59208cc962cb", shelter1);

        // ID 3: Nala (HU09/HU10: sin historial médico, disponible)
        PetEntity nala = createPet(
                "Nala", "Gato", "Siamés", 1, "Hembra", "Pequeña", 4.2,
                "Saludable", "DISPONIBLE",
                "Tranquila, curiosa y muy cariñosa en ambientes interiores.",
                "2026-09-05", "https://images.unsplash.com/photo-1513245543132-31f507417b26", shelter1);

        // ID 4: Toby (HU04: adoptado por María en adopción activa)
        PetEntity toby = createPet(
                "Toby", "Perro", "Beagle", 4, "Macho", "Mediano", 13.5,
                "Saludable", "ADOPTADO",
                "Alegre, con buen olfato y totalmente adaptado a hogar familiar.",
                "2026-06-01", "https://images.unsplash.com/photo-1505628346881-b72b27e84530", shelter1);

        // ID 5: Milo (HU04: adoptado por Carlos en adopción finalizada)
        PetEntity milo = createPet(
                "Milo", "Gato", "Persa", 2, "Macho", "Pequeño", 5.0,
                "Saludable", "ADOPTADO",
                "Gato dócil de pelaje largo, ideal para apartamento.",
                "2026-05-12", "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba", shelter2);

        // ID 6: Rocky (HU01: salud Enfermo -> no debe aparecer como disponible)
        PetEntity rocky = createPet(
                "Rocky", "Perro", "Pastor Alemán", 5, "Macho", "Grande", 32.0,
                "Enfermo", "NO_DISPONIBLE",
                "En recuperación respiratoria bajo supervisión veterinaria.",
                "2026-09-20", "https://images.unsplash.com/photo-1589941013453-ec89f33b5e95", shelter1);

        // ID 7: Kiara (HU01: salud En tratamiento -> no debe aparecer como disponible)
        PetEntity kiara = createPet(
                "Kiara", "Conejo", "Mini Lop", 1, "Hembra", "Pequeña", 1.8,
                "En tratamiento", "DISPONIBLE",
                "En tratamiento dermatológico leve; muy dócil al manejo.",
                "2026-10-01", "https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308", shelter2);

        // ID 8: Bruno (HU11: salud Fallecido para probar restricción)
        PetEntity bruno = createPet(
                "Bruno", "Perro", "Bulldog Francés", 6, "Macho", "Pequeño", 12.0,
                "Fallecido", "NO_DISPONIBLE",
                "Ingresó con cardiopatía congénita avanzada.",
                "2026-04-18", "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e", shelter2);

        return List.of(luna, max, nala, toby, milo, rocky, kiara, bruno);
    }

    private PetEntity createPet(
            String name, String specie, String breed, Integer age, String sex, String size,
            Double weight, String healthStatus, String adoptionStatus, String description,
            String admissionDate, String photo, ShelterEntity shelter) {
        PetEntity pet = new PetEntity();
        pet.setName(name);
        pet.setSpecie(specie);
        pet.setBreed(breed);
        pet.setAge(age);
        pet.setSex(sex);
        pet.setSize(size);
        pet.setWeight(weight);
        pet.setHealthStatus(healthStatus);
        pet.setAdoptionStatus(adoptionStatus);
        pet.setDescription(description);
        pet.setAdmissionDate(admissionDate);
        pet.setPhoto(photo);
        pet.setShelter(shelter);
        return petRepository.save(pet);
    }

    private void seedMedicalHistories(List<PetEntity> pets) {
        // Se omite pets.get(2) (Nala, ID 3) intencionalmente para tener mascota SIN historial
        saveHistory(pets.get(0), "Pentavalente, Rabia, Bordetella", "Ninguna", "Desparasitación trimestral preventiva", true);
        saveHistory(pets.get(1), "Pentavalente, Rabia", "Ninguna", "Suplemento articular preventivo", true);
        saveHistory(pets.get(3), "Pentavalente, Rabia, Leptospira", "Otitis leve resuelta", "Limpieza ótica finalizada", true);
        saveHistory(pets.get(4), "Triple Felina, Rabia, Leucemia", "Ninguna", "Control dental anual", true);
        saveHistory(pets.get(5), "Pentavalente, Rabia", "Traqueobronquitis infecciosa", "Antibiótico oral y nebulizaciones por 10 días", false);
        saveHistory(pets.get(6), "Mixomatosis", "Dermatitis fúngica leve", "Crema antimicótica tópica cada 12 horas", false);
        saveHistory(pets.get(7), "Pentavalente, Rabia", "Insuficiencia cardíaca congestiva", "Manejo paliativo hospitalario", true);
    }

    private void saveHistory(
            PetEntity pet, String vaccine, String diseases, String treatment, Boolean sterilized) {
        MedicalHistoryEntity history = new MedicalHistoryEntity();
        history.setVaccine(vaccine);
        history.setDiseases(diseases);
        history.setTreatment(treatment);
        history.setSterilized(sterilized);
        history.setPet(pet);
        pet.setMedicalHistory(history);
        medicalHistoryRepository.save(history);
    }

    private void seedPetEvents(List<PetEntity> pets) {
        PetEntity luna = pets.get(0);  // ID 1
        PetEntity bruno = pets.get(7); // ID 8 (Fallecido)

        savePetEvent(luna, "INGRESO", "2026-08-10", "Ingreso al refugio Huellitas del Norte tras rescate.");
        savePetEvent(luna, "VACUNACION", "2026-08-18", "Aplicación de refuerzo pentavalente y antirrábica.");
        savePetEvent(luna, "ESTERILIZACION", "2026-09-02", "Procedimiento de esterilización exitoso sin complicaciones.");

        savePetEvent(bruno, "INGRESO", "2026-04-18", "Ingreso por entrega voluntaria con cuadro respiratorio.");
        savePetEvent(bruno, "HOSPITALIZACION", "2026-05-10", "Hospitalización por descompensación cardíaca.");
        savePetEvent(bruno, "FALLECIMIENTO", "2026-05-25", "Fallecimiento registrado por falla cardíaca.");
    }

    private void savePetEvent(PetEntity pet, String type, String date, String description) {
        PetEventEntity event = new PetEventEntity();
        event.setType(type);
        event.setDate(date);
        event.setDescription(description);
        event.setPet(pet);
        petEventRepository.save(event);
    }

    private void seedAdoptionRequests(List<AdopterEntity> adopters, List<PetEntity> pets) {
        AdopterEntity maria = adopters.get(0);
        AdopterEntity carlos = adopters.get(1);
        AdopterEntity sofia = adopters.get(2);

        PetEntity luna = pets.get(0); // ID 1
        PetEntity max = pets.get(1);  // ID 2
        PetEntity nala = pets.get(2); // ID 3
        PetEntity toby = pets.get(3); // ID 4
        PetEntity milo = pets.get(4); // ID 5

        // Solicitudes PENDIENTE
        saveAdoptionRequest("REQ-1001", toDate(2026, 11, 1), "PENDIENTE",
                "Trabajo desde casa y cuento con parque cercano para pasear a Luna.", carlos, luna);
        saveAdoptionRequest("REQ-1002", toDate(2026, 11, 3), "PENDIENTE",
                "Busco una compañera activa para salidas de fin de semana.", sofia, luna);

        // Solicitud APROBADO lista para formalizar en HU04
        saveAdoptionRequest("REQ-1003", toDate(2026, 11, 5), "APROBADO",
                "Apartamento con mallas de seguridad instaladas para Nala.", sofia, nala);

        // Solicitud CANCELADO
        saveAdoptionRequest("REQ-1004", toDate(2026, 10, 12), "CANCELADO",
                "Solicitud cancelada por cambio temporal de residencia.", maria, max);

        // Solicitudes ya aprobadas correspondientes a las adopciones formalizadas
        saveAdoptionRequest("REQ-1005", toDate(2026, 9, 15), "APROBADO",
                "Hogar estable con experiencia previa cuidando perros medianos.", maria, toby);
        saveAdoptionRequest("REQ-1006", toDate(2026, 7, 10), "APROBADO",
                "Ambiente tranquilo ideal para gato Persa.", carlos, milo);
    }

    private void saveAdoptionRequest(
            String requestId, Date dateRequest, String status, String description,
            AdopterEntity adopter, PetEntity pet) {
        AdoptionRequestEntity req = new AdoptionRequestEntity();
        req.setRequestId(requestId);
        req.setDateRequest(dateRequest);
        req.setStatus(status);
        req.setDescription(description);
        req.setAdopter(adopter);
        req.setPet(pet);
        adoptionRequestRepository.save(req);
    }

    private List<AdoptionEntity> seedAdoptions(
            AdopterEntity maria, AdopterEntity carlos, List<PetEntity> pets) {
        // ID 1: Adopción ACTIVA de María (ID 1) con Toby (ID 4)
        AdoptionEntity activeAdoption = new AdoptionEntity();
        activeAdoption.setAdoptionId("ADP-2001");
        activeAdoption.setStatus("ACTIVA");
        activeAdoption.setDate(toDate(2026, 9, 25));
        activeAdoption.setAdopter(maria);
        activeAdoption.setPet(pets.get(3));
        activeAdoption = adoptionRepository.save(activeAdoption);

        // ID 2: Adopción FINALIZADA de Carlos (ID 2) con Milo (ID 5)
        AdoptionEntity finishedAdoption = new AdoptionEntity();
        finishedAdoption.setAdoptionId("ADP-2002");
        finishedAdoption.setStatus("FINALIZADA");
        finishedAdoption.setDate(toDate(2026, 7, 20));
        finishedAdoption.setAdopter(carlos);
        finishedAdoption.setPet(pets.get(4));
        finishedAdoption = adoptionRepository.save(finishedAdoption);

        return List.of(activeAdoption, finishedAdoption);
    }

    private void seedTrialsAndReturns(
            List<AdopterEntity> adopters, List<ShelterEntity> shelters, List<PetEntity> pets) {
        AdopterEntity maria = adopters.get(0);
        AdopterEntity carlos = adopters.get(1);
        AdopterEntity sofia = adopters.get(2);

        ShelterEntity shelter1 = shelters.get(0);
        ShelterEntity shelter2 = shelters.get(1);

        // ID 1: Periodo de prueba ACTIVA de María con Max (ID 2)
        TrialRequestEntity activeTrial = new TrialRequestEntity();
        activeTrial.setTrialId("TRL-3001");
        activeTrial.setDate(toDate(2026, 11, 8));
        activeTrial.setStatus("ACTIVA");
        activeTrial.setDescription("Periodo de convivencia de 15 días para evaluar adaptación en casa.");
        activeTrial.setAdopter(maria);
        activeTrial.setShelter(shelter1);
        activeTrial.setPet(pets.get(1));
        trialRequestRepository.save(activeTrial);

        // ID 2: Periodo de prueba FINALIZADA de Carlos con Kiara (ID 7) + Return asociado
        TrialRequestEntity finishedTrial = new TrialRequestEntity();
        finishedTrial.setTrialId("TRL-3002");
        finishedTrial.setDate(toDate(2026, 10, 10));
        finishedTrial.setStatus("FINALIZADA");
        finishedTrial.setDescription("Prueba de convivencia con conejo Mini Lop.");
        finishedTrial.setAdopter(carlos);
        finishedTrial.setShelter(shelter2);
        finishedTrial.setPet(pets.get(6));
        finishedTrial = trialRequestRepository.save(finishedTrial);

        ReturnEntity trialReturn = new ReturnEntity();
        trialReturn.setReturnType("FIN_PERIODO_PRUEBA");
        trialReturn.setDescription("Se retorna al refugio para completar tratamiento dermatológico antes de adopción definitiva.");
        trialReturn.setDate("2026-10-24");
        trialReturn.setTrialRequest(finishedTrial);
        returnRepository.save(trialReturn);

        // ID 3: Periodo de prueba CANCELADA de Sofía con Luna (ID 1)
        TrialRequestEntity cancelledTrial = new TrialRequestEntity();
        cancelledTrial.setTrialId("TRL-3003");
        cancelledTrial.setDate(toDate(2026, 10, 18));
        cancelledTrial.setStatus("CANCELADA");
        cancelledTrial.setDescription("Cancelada por viaje laboral inesperado de la solicitante.");
        cancelledTrial.setAdopter(sofia);
        cancelledTrial.setShelter(shelter1);
        cancelledTrial.setPet(pets.get(0));
        trialRequestRepository.save(cancelledTrial);
    }

    private void seedFollowUps(
            List<AdoptionEntity> adoptions, List<PetEntity> pets, List<VeterinarianEntity> vets) {
        AdoptionEntity activeAdoption = adoptions.get(0);   // ID 1 (Toby)
        AdoptionEntity finishedAdoption = adoptions.get(1); // ID 2 (Milo)

        PetEntity toby = pets.get(3); // ID 4
        PetEntity milo = pets.get(4); // ID 5

        // Seguimientos realizados (con notas) en adopción activa
        saveFollowUp("FUP-4001",
                "Control inicial post-adopción: peso estable (13.5 kg), buen apetito y adaptación positiva.",
                toDate(2026, 10, 10), "Excelente", vets.get(0), activeAdoption, toby);

        saveFollowUp("FUP-4002",
                "Segundo control: esquema de vacunación al día y sin signos de ansiedad por separación.",
                toDate(2026, 11, 2), "Saludable", vets.get(1), activeAdoption, toby);

        // Seguimiento programado futuro (sin notas) en adopción activa
        saveFollowUp("FUP-4003",
                null,
                toDate(2026, 12, 5), "Pendiente de valoración", vets.get(2), activeAdoption, toby);

        // Seguimiento de cierre en adopción finalizada
        saveFollowUp("FUP-4004",
                "Cierre exitoso del proceso de adopción. Mascota completamente integrada al hogar.",
                toDate(2026, 9, 20), "Óptima", vets.get(3), finishedAdoption, milo);
    }

    private void saveFollowUp(
            String followId, String notes, Date date, String petCondition,
            VeterinarianEntity vet, AdoptionEntity adoption, PetEntity pet) {
        FollowUpEntity followUp = new FollowUpEntity();
        followUp.setFollowId(followId);
        followUp.setNotes(notes);
        followUp.setDate(date);
        followUp.setPetCondition(petCondition);
        followUp.setVeterinarian(vet);
        followUp.setAdoption(adoption);
        followUp.setPet(pet);
        followUpRepository.save(followUp);
    }

    private void seedReviews(List<AdopterEntity> adopters, List<PetEntity> pets) {
        PetEntity luna = pets.get(0); // ID 1 (3 reseñas de 3 adoptantes distintos; Max ID 2 queda sin reseñas)

        saveReview(5, "Luna es súper cariñosa y aprendió rápido a pasear con correa.",
                "2026-10-15", luna, adopters.get(0));
        saveReview(4, "Muy juguetona y sociable con otros perros en el parque.",
                "2026-10-22", luna, adopters.get(1));
        saveReview(5, "Excelente temperamento, ideal para compartir en familia.",
                "2026-11-04", luna, adopters.get(2));
    }

    private void saveReview(
            Integer rating, String comment, String date, PetEntity pet, AdopterEntity adopter) {
        ReviewEntity review = new ReviewEntity();
        review.setRating(rating);
        review.setComment(comment);
        review.setDate(date);
        review.setPet(pet);
        review.setAdopter(adopter);
        reviewRepository.save(review);
    }

    private void seedNotifications(
            AdopterEntity maria, AdopterEntity carlos,
            List<AdoptionEntity> adoptions, List<PetEntity> pets) {
        AdoptionEntity activeAdoption = adoptions.get(0); // ID 1
        PetEntity luna = pets.get(0); // ID 1
        PetEntity toby = pets.get(3); // ID 4

        // 5 notificaciones para María (userId = 1)
        saveNotification("email",
                "Tu adopción de Toby (ADP-2001) fue formalizada correctamente.",
                "2026-09-25T10:15:00Z", true, maria, activeAdoption, toby);

        saveNotification("push",
                "Recordatorio: tienes un seguimiento veterinario programado para Toby el 2026-12-05.",
                "2026-11-10T09:00:00Z", false, maria, activeAdoption, toby);

        saveNotification("sms",
                "Huellitas del Norte confirmó el inicio de tu periodo de prueba con Max.",
                "2026-11-08T14:30:00Z", false, maria, null, pets.get(1));

        saveNotification("email",
                "Luna recibió nuevas valoraciones en el catálogo de mascotas disponibles.",
                "2026-11-05T16:45:00Z", true, maria, null, luna);

        saveNotification("push",
                "Dra. Laura Gómez registró las notas del control veterinario de Toby.",
                "2026-11-02T18:20:00Z", false, maria, activeAdoption, toby);

        // 1 notificación de OTRO usuario (Carlos, userId = 2) para probar regla de ownership
        saveNotification("email",
                "Tu solicitud REQ-1001 por Luna está en revisión por el refugio.",
                "2026-11-01T11:00:00Z", false, carlos, null, luna);
    }

    private void saveNotification(
            String channel, String content, String date, boolean read,
            UserEntity user, AdoptionEntity adoption, PetEntity pet) {
        NotificationEntity notification = new NotificationEntity();
        notification.setChannel(channel);
        notification.setContent(content);
        notification.setDate(date);
        notification.setRead(read);
        notification.setUser(user);
        notification.setAdoption(adoption);
        notification.setPet(pet);
        notificationRepository.save(notification);
    }

    private void seedMessages(AdopterEntity maria, UserEntity coordinator, VeterinarianEntity vet1) {
        saveMessage("Hola, quisiera confirmar el horario para recoger los documentos de Toby.",
                "2026-11-12T09:30:00Z", true, maria, coordinator);

        saveMessage("Hola María, puedes pasar de lunes a viernes entre 8:00 a.m. y 5:00 p.m.",
                "2026-11-12T10:05:00Z", true, coordinator, maria);

        saveMessage("Doctora Laura, Toby ha estado muy activo después del último control.",
                "2026-11-14T15:10:00Z", true, maria, vet1);

        saveMessage("Qué excelente noticia María. Nos vemos en el seguimiento de diciembre.",
                "2026-11-14T16:00:00Z", false, vet1, maria);
    }

    private void saveMessage(
            String content, String date, boolean read, UserEntity sender, UserEntity receiver) {
        MessageEntity message = new MessageEntity();
        message.setContent(content);
        message.setDate(date);
        message.setRead(read);
        message.setSender(sender);
        message.setReceiver(receiver);
        messageRepository.save(message);
    }

    private Date toDate(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void logSummary(
            List<ShelterEntity> shelters, List<AdopterEntity> adopters,
            List<VeterinarianEntity> vets, List<PetEntity> pets, List<AdoptionEntity> adoptions) {
        log.info("=================================================================");
        log.info("[DataInitializer] Seed completado con éxito:");
        log.info("  - Refugios: ID {} (Huellitas del Norte), ID {} (Patitas del Sur)",
                shelters.get(0).getId(), shelters.get(1).getId());
        log.info("  - Adoptante principal (CURRENT_USER): ID {} ({})",
                adopters.get(0).getId(), adopters.get(0).getName());
        log.info("  - Veterinarios Refugio 1: IDs {}, {}, {}",
                vets.get(0).getId(), vets.get(1).getId(), vets.get(2).getId());
        log.info("  - Mascotas: ID {} (Luna - disponible/reseñas), ID {} (Max - en prueba), ID {} (Nala - sin historial), ID {} (Toby - adoptado), ID {} (Bruno - fallecido)",
                pets.get(0).getId(), pets.get(1).getId(), pets.get(2).getId(), pets.get(3).getId(), pets.get(7).getId());
        log.info("  - Adopciones: ID {} (ACTIVA), ID {} (FINALIZADA)",
                adoptions.get(0).getId(), adoptions.get(1).getId());
        log.info("  - URLs útiles del Front:");
        log.info("      * Catálogo mascotas:       http://localhost:5173/pets");
        log.info("      * Reseñas Luna (ID 1):     http://localhost:5173/pets/1/reviews");
        log.info("      * Reseñas Max (ID 2):      http://localhost:5173/pets/2/reviews");
        log.info("      * Historial Nala (ID 3):   http://localhost:5173/pets/3/manage-medical-history");
        log.info("      * Vets Refugio 1:          http://localhost:5173/shelters/1/veterinarians");
        log.info("      * Seguimientos (Rol VET):  http://localhost:5173/follow-ups");
        log.info("      * Notificaciones María:    http://localhost:5173/notifications");
        log.info("      * Mensajes María:          http://localhost:5173/messages");
        log.info("      * Perfil María:            http://localhost:5173/profile");
        log.info("=================================================================");
    }
}