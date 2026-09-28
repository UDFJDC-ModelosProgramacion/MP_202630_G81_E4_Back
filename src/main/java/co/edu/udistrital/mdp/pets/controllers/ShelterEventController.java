package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.ShelterEventDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterEventService;
import co.edu.udistrital.mdp.pets.services.ShelterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ShelterEventController {

    private final ShelterEventService shelterEventService;
    private final ShelterService shelterService;
    private final ModelMapper modelMapper;

    @GetMapping("/shelters/{shelterId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<ShelterEventDTO> findAllByShelter(@PathVariable Long shelterId) throws EntityNotFoundException {
        List<ShelterEventEntity> events = shelterService.getShelter(shelterId).getShelterEvents();
        return modelMapper.map(events, new TypeToken<List<ShelterEventDTO>>() {
        }.getType());
    }

    @PostMapping("/shelters/{shelterId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ShelterEventDTO create(@PathVariable Long shelterId, @RequestBody ShelterEventDTO shelterEventDTO)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity event = shelterEventService.createShelterEvent(shelterId,
                modelMapper.map(shelterEventDTO, ShelterEventEntity.class));
        return modelMapper.map(event, ShelterEventDTO.class);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterEventDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(shelterEventService.getShelterEvent(id), ShelterEventDTO.class);
    }

    @PutMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterEventDTO update(@PathVariable Long id, @RequestBody ShelterEventDTO shelterEventDTO)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity event = shelterEventService.updateShelterEvent(id,
                modelMapper.map(shelterEventDTO, ShelterEventEntity.class));
        return modelMapper.map(event, ShelterEventDTO.class);
    }

    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        shelterEventService.deleteShelterEvent(id);
    }
}