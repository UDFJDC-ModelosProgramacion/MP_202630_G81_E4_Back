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

import co.edu.udistrital.mdp.pets.dto.AdopterDTO;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.services.AdopterService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST del recurso Adopter. Se necesita para poder crear
 * adoptantes por API (las colecciones de Adoption, AdoptionRequest,
 * TrialRequest y FollowUp dependen de que existan).
 */
@RestController
@RequestMapping("/adopters")
@RequiredArgsConstructor
public class AdopterController {

	private final AdopterService adopterService;

	private final ModelMapper modelMapper;

	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<AdopterDTO> findAll() {
		List<AdopterEntity> entities = adopterService.getAdopters();
		return modelMapper.map(entities, new TypeToken<List<AdopterDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public AdopterDTO findOne(@PathVariable Long id) {
		AdopterEntity entity = adopterService.getAdopter(id);
		return modelMapper.map(entity, AdopterDTO.class);
	}

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public AdopterDTO create(@RequestBody AdopterDTO dto) {
		AdopterEntity entity = adopterService.createAdopter(modelMapper.map(dto, AdopterEntity.class));
		return modelMapper.map(entity, AdopterDTO.class);
	}

	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public AdopterDTO update(@PathVariable Long id, @RequestBody AdopterDTO dto) {
		AdopterEntity entity = adopterService.updateAdopter(id, modelMapper.map(dto, AdopterEntity.class));
		return modelMapper.map(entity, AdopterDTO.class);
	}

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		adopterService.deleteAdopter(id);
	}
}