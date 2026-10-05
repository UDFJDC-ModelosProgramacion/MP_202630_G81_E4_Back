package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.MessageDTO;
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.services.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDTO create(@RequestBody MessageDTO messageDTO) {
        MessageEntity messageEntity = modelMapper.map(messageDTO, MessageEntity.class);
        MessageEntity newMessage = messageService.createMessage(messageEntity);
        return modelMapper.map(newMessage, MessageDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MessageDTO> findAll() {
        List<MessageEntity> messages = messageService.getMessages();
        return modelMapper.map(messages, new TypeToken<List<MessageDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MessageDTO findOne(@PathVariable Long id) {
        MessageEntity message = messageService.getMessage(id);
        return modelMapper.map(message, MessageDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MessageDTO update(@PathVariable Long id, @RequestBody MessageDTO messageDTO) {
        MessageEntity messageEntity = modelMapper.map(messageDTO, MessageEntity.class);
        MessageEntity updatedMessage = messageService.updateMessage(id, messageEntity);
        return modelMapper.map(updatedMessage, MessageDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        messageService.deleteMessage(id);
    }
}