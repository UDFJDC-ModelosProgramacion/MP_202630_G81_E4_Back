package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.dto.PetDetailDTO;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.services.PetService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetDTO create(@RequestBody PetDTO petDTO) {
        PetEntity petEntity = modelMapper.map(petDTO, PetEntity.class);
        PetEntity newPet = petService.createPet(petEntity);
        return modelMapper.map(newPet, PetDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PetDetailDTO> findAll() {
        List<PetEntity> pets = petService.getPets();
        return modelMapper.map(pets, new TypeToken<List<PetDetailDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PetDetailDTO findOne(@PathVariable Long id) {
        PetEntity pet = petService.getPet(id);
        return modelMapper.map(pet, PetDetailDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PetDTO update(@PathVariable Long id, @RequestBody PetDTO petDTO) {
        PetEntity petEntity = modelMapper.map(petDTO, PetEntity.class);
        PetEntity updatedPet = petService.updatePet(id, petEntity);
        return modelMapper.map(updatedPet, PetDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        petService.deletePet(id);
    }
}