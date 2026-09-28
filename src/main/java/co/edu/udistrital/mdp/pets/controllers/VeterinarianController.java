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

import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterService;
import co.edu.udistrital.mdp.pets.services.VeterinarianService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VeterinarianController {

    private final VeterinarianService veterinarianService;
    private final ShelterService shelterService;
    private final ModelMapper modelMapper;

    @GetMapping("/shelters/{shelterId}/veterinarians")
    @ResponseStatus(HttpStatus.OK)
    public List<VeterinarianDTO> findAllByShelter(@PathVariable Long shelterId) throws EntityNotFoundException {
        List<VeterinarianEntity> veterinarians = shelterService.getShelter(shelterId).getVeterinarians();
        return modelMapper.map(veterinarians, new TypeToken<List<VeterinarianDTO>>() {
        }.getType());
    }

    @PostMapping("/shelters/{shelterId}/veterinarians")
    @ResponseStatus(HttpStatus.CREATED)
    public VeterinarianDTO create(@PathVariable Long shelterId, @RequestBody VeterinarianDTO veterinarianDTO)
            throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity veterinarian = veterinarianService.createVeterinarian(shelterId,
                modelMapper.map(veterinarianDTO, VeterinarianEntity.class));
        return modelMapper.map(veterinarian, VeterinarianDTO.class);
    }

    @GetMapping("/veterinarians/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VeterinarianDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(veterinarianService.getVeterinarian(id), VeterinarianDTO.class);
    }

    @PutMapping("/veterinarians/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VeterinarianDTO update(@PathVariable Long id, @RequestBody VeterinarianDTO veterinarianDTO)
            throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity veterinarian = veterinarianService.updateVeterinarian(id,
                modelMapper.map(veterinarianDTO, VeterinarianEntity.class));
        return modelMapper.map(veterinarian, VeterinarianDTO.class);
    }

    @DeleteMapping("/veterinarians/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        veterinarianService.deleteVeterinarian(id);
    }
}