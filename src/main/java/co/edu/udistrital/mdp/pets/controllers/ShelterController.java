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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.ShelterDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shelters")
@RequiredArgsConstructor
public class ShelterController {

    private final ShelterService shelterService;
    private final ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShelterDTO> findAll() {
        List<ShelterEntity> shelters = shelterService.getShelters();
        return modelMapper.map(shelters, new TypeToken<List<ShelterDTO>>() {
        }.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        ShelterEntity shelter = shelterService.getShelter(id);
        return modelMapper.map(shelter, ShelterDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShelterDTO create(@RequestBody ShelterDTO shelterDTO) throws IllegalOperationException {
        ShelterEntity shelter = shelterService.createShelter(modelMapper.map(shelterDTO, ShelterEntity.class));
        return modelMapper.map(shelter, ShelterDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterDTO update(@PathVariable Long id, @RequestBody ShelterDTO shelterDTO)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = shelterService.updateShelter(id, modelMapper.map(shelterDTO, ShelterEntity.class));
        return modelMapper.map(shelter, ShelterDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        shelterService.deleteShelter(id);
    }
}