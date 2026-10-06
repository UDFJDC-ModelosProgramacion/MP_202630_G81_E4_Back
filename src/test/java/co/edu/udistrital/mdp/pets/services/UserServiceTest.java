package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.NotificationRepository;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity validUser;

    @BeforeEach
    void setup() {
        validUser = new UserEntity();
        validUser.setId(1L);
        validUser.setName("Juan Pérez");
        validUser.setEmail("juan@example.com");
        validUser.setPhone("3001234567");
        validUser.setPassword("clave123");
        validUser.setAddress("Calle 1 # 2-3");
    }

    // --- CREATE USER ---
    @Test
    void testCreateUserSuccess() {
        Mockito.when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any(UserEntity.class))).thenReturn(validUser);
        UserEntity created = userService.createUser(validUser);
        assertNotNull(created);
        assertEquals("Juan Pérez", created.getName());
    }

    @Test
    void testCreateUserMissingNameFails() {
        validUser.setName("");
        assertThrows(BusinessLogicException.class, () -> userService.createUser(validUser));
    }

    @Test
    void testCreateUserInvalidEmailFails() {
        validUser.setEmail("no-es-un-email");
        assertThrows(BusinessLogicException.class, () -> userService.createUser(validUser));
    }

    @Test
    void testCreateUserMissingPasswordFails() {
        validUser.setPassword("");
        assertThrows(BusinessLogicException.class, () -> userService.createUser(validUser));
    }

    @Test
    void testCreateUserDuplicateEmailFails() {
        Mockito.when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.of(validUser));
        assertThrows(BusinessLogicException.class, () -> userService.createUser(validUser));
    }

    // --- GET USERS ---
    @Test
    void testGetUsersSuccess() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(validUser));
        List<UserEntity> users = userService.getUsers();
        assertFalse(users.isEmpty());
    }

    @Test
    void testGetUserSuccess() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        UserEntity found = userService.getUser(1L);
        assertNotNull(found);
    }

    @Test
    void testGetUserNotFoundFails() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(99L));
    }

    // --- UPDATE USER ---
    @Test
    void testUpdateUserSuccess() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        Mockito.when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.of(validUser));
        Mockito.when(userRepository.save(any(UserEntity.class))).thenReturn(validUser);
        validUser.setName("Juan Actualizado");
        UserEntity updated = userService.updateUser(1L, validUser);
        assertEquals("Juan Actualizado", updated.getName());
    }

    @Test
    void testUpdateUserNotFoundFails() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(99L, validUser));
    }

    @Test
    void testUpdateUserDuplicateEmailFails() {
        UserEntity another = new UserEntity();
        another.setId(2L);
        another.setEmail(validUser.getEmail());

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        Mockito.when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.of(another));

        assertThrows(BusinessLogicException.class, () -> userService.updateUser(1L, validUser));
    }

    // --- DELETE USER ---
    @Test
    void testDeleteUserSuccess() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        Mockito.when(notificationRepository.findByUserIdAndReadFalse(1L)).thenReturn(List.of());
        assertDoesNotThrow(() -> userService.deleteUser(1L));
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFoundFails() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(99L));
    }

    @Test
    void testDeleteUserWithUnreadNotificationsFails() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        Mockito.when(notificationRepository.findByUserIdAndReadFalse(1L))
                .thenReturn(List.of(new co.edu.udistrital.mdp.pets.entities.NotificationEntity()));
        assertThrows(BusinessLogicException.class, () -> userService.deleteUser(1L));
    }
}