package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.exceptions.BusinessLogicException;
import co.edu.udistrital.mdp.pets.repositories.MessageRepository;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MessageEntity createMessage(MessageEntity message) {
        validateMessageIntegrity(message);

        Long senderId = message.getSender().getId();
        Long receiverId = message.getReceiver().getId();

        if (senderId.equals(receiverId)) {
            throw new BusinessLogicException("Un usuario no puede enviarse un mensaje a sí mismo.");
        }
        if (userRepository.findById(senderId).isEmpty()) {
            throw new EntityNotFoundException("El usuario remitente con ID " + senderId + " no existe.");
        }
        if (userRepository.findById(receiverId).isEmpty()) {
            throw new EntityNotFoundException("El usuario destinatario con ID " + receiverId + " no existe.");
        }

        return messageRepository.save(message);
    }

    public List<MessageEntity> getMessages() {
        return messageRepository.findAll();
    }

    public MessageEntity getMessage(Long id) {
        Optional<MessageEntity> message = messageRepository.findById(id);
        if (message.isEmpty()) {
            throw new EntityNotFoundException("El mensaje con ID " + id + " no existe.");
        }
        return message.get();
    }

    @Transactional
    public MessageEntity updateMessage(Long id, MessageEntity message) {
        MessageEntity existingMessage = getMessage(id);

        if (existingMessage.isRead()) {
            throw new BusinessLogicException("No se puede modificar un mensaje que ya fue leído.");
        }

        validateMessageIntegrity(message);
        message.setId(id);
        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Long id) {
        MessageEntity message = getMessage(id);

        if (!message.isRead()) {
            throw new BusinessLogicException("No se puede eliminar un mensaje que aún no ha sido leído por el destinatario.");
        }

        messageRepository.deleteById(id);
    }

    private void validateMessageIntegrity(MessageEntity message) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new BusinessLogicException("El contenido del mensaje es obligatorio.");
        }
        if (message.getSender() == null || message.getSender().getId() == null) {
            throw new BusinessLogicException("El mensaje debe tener un remitente.");
        }
        if (message.getReceiver() == null || message.getReceiver().getId() == null) {
            throw new BusinessLogicException("El mensaje debe tener un destinatario.");
        }
    }
}