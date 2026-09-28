package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.PetEventDTO;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.services.PetEventService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PetEventController {

    @Autowired
    private PetEventService eventService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/pets/{petId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public PetEventDTO create(@PathVariable Long petId, @RequestBody PetEventDTO dto) {
        PetEventEntity entity = modelMapper.map(dto, PetEventEntity.class);
        PetEventEntity created = eventService.createPetEvent(petId, entity);
        return modelMapper.map(created, PetEventDTO.class);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<PetEventDTO> findAll() {
        List<PetEventEntity> events = eventService.getPetEvents();
        return modelMapper.map(events, new TypeToken<List<PetEventDTO>>() {}.getType());
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PetEventDTO findOne(@PathVariable Long id) {
        PetEventEntity event = eventService.getPetEvent(id);
        return modelMapper.map(event, PetEventDTO.class);
    }

    @PutMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PetEventDTO update(@PathVariable Long id, @RequestBody PetEventDTO dto) {
        PetEventEntity entity = modelMapper.map(dto, PetEventEntity.class);
        PetEventEntity updated = eventService.updatePetEvent(id, entity);
        return modelMapper.map(updated, PetEventDTO.class);
    }

    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        eventService.deletePetEvent(id);
    }
}
