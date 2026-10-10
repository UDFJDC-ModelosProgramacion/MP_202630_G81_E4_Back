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

import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.MessageRepository;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    private MessageEntity validMessage;
    private UserEntity sender;
    private UserEntity receiver;

    @BeforeEach
    void setup() {
        sender = new UserEntity();
        sender.setId(1L);

        receiver = new UserEntity();
        receiver.setId(2L);

        validMessage = new MessageEntity();
        validMessage.setId(1L);
        validMessage.setContent("Hola, ¿cómo va la solicitud?");
        validMessage.setDate("2026-09-28");
        validMessage.setRead(false);
        validMessage.setSender(sender);
        validMessage.setReceiver(receiver);
    }

    // --- CREATE MESSAGE ---
    @Test
    void testCreateMessageSuccess() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        Mockito.when(messageRepository.save(any(MessageEntity.class))).thenReturn(validMessage);
        MessageEntity created = messageService.createMessage(validMessage);
        assertNotNull(created);
    }

    @Test
    void testCreateMessageMissingContentFails() {
        validMessage.setContent("");
        assertThrows(BusinessLogicException.class, () -> messageService.createMessage(validMessage));
    }

    @Test
    void testCreateMessageMissingSenderFails() {
        validMessage.setSender(null);
        assertThrows(BusinessLogicException.class, () -> messageService.createMessage(validMessage));
    }

    @Test
    void testCreateMessageMissingReceiverFails() {
        validMessage.setReceiver(null);
        assertThrows(BusinessLogicException.class, () -> messageService.createMessage(validMessage));
    }

    @Test
    void testCreateMessageSameSenderAndReceiverFails() {
        validMessage.setReceiver(sender);
        assertThrows(BusinessLogicException.class, () -> messageService.createMessage(validMessage));
    }

    @Test
    void testCreateMessageSenderNotFoundFails() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> messageService.createMessage(validMessage));
    }

    @Test
    void testCreateMessageReceiverNotFoundFails() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> messageService.createMessage(validMessage));
    }

    // --- GET MESSAGES ---
    @Test
    void testGetMessagesSuccess() {
        Mockito.when(messageRepository.findAll()).thenReturn(List.of(validMessage));
        assertFalse(messageService.getMessages().isEmpty());
    }

    @Test
    void testGetMessageSuccess() {
        Mockito.when(messageRepository.findById(1L)).thenReturn(Optional.of(validMessage));
        assertNotNull(messageService.getMessage(1L));
    }

    @Test
    void testGetMessageNotFoundFails() {
        Mockito.when(messageRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> messageService.getMessage(99L));
    }

    // --- UPDATE MESSAGE ---
    @Test
    void testUpdateMessageSuccess() {
        Mockito.when(messageRepository.findById(1L)).thenReturn(Optional.of(validMessage));
        Mockito.when(messageRepository.save(any(MessageEntity.class))).thenReturn(validMessage);
        validMessage.setContent("Mensaje editado");
        MessageEntity updated = messageService.updateMessage(1L, validMessage);
        assertEquals("Mensaje editado", updated.getContent());
    }

    @Test
    void testUpdateMessageAlreadyReadFails() {
        validMessage.setRead(true);
        Mockito.when(messageRepository.findById(1L)).thenReturn(Optional.of(validMessage));
        assertThrows(BusinessLogicException.class, () -> messageService.updateMessage(1L, validMessage));
    }

    @Test
    void testUpdateMessageNotFoundFails() {
        Mockito.when(messageRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> messageService.updateMessage(99L, validMessage));
    }

    // --- DELETE MESSAGE ---
    @Test
    void testDeleteMessageSuccess() {
        validMessage.setRead(true);
        Mockito.when(messageRepository.findById(1L)).thenReturn(Optional.of(validMessage));
        assertDoesNotThrow(() -> messageService.deleteMessage(1L));
        Mockito.verify(messageRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMessageUnreadFails() {
        validMessage.setRead(false);
        Mockito.when(messageRepository.findById(1L)).thenReturn(Optional.of(validMessage));
        assertThrows(BusinessLogicException.class, () -> messageService.deleteMessage(1L));
    }

    @Test
    void testDeleteMessageNotFoundFails() {
        Mockito.when(messageRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> messageService.deleteMessage(99L));
    }
}