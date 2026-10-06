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
 
import co.edu.udistrital.mdp.pets.dto.AdoptionRequestDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionRequestDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionRequestService;
 
import lombok.RequiredArgsConstructor;
 

@RestController
@RequestMapping("/adoption-requests")
@RequiredArgsConstructor
public class AdoptionRequestController {
 
	private final AdoptionRequestService adoptionRequestService;
 
	private final ModelMapper modelMapper;
 
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<AdoptionRequestDetailDTO> findAll() {
		List<AdoptionRequestEntity> entities = adoptionRequestService.getAdoptionRequests();
		return modelMapper.map(entities, new TypeToken<List<AdoptionRequestDetailDTO>>() {
		}.getType());
	}
 
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public AdoptionRequestDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
		AdoptionRequestEntity entity = adoptionRequestService.getAdoptionRequest(id);
		return modelMapper.map(entity, AdoptionRequestDetailDTO.class);
	}
 
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public AdoptionRequestDTO create(@RequestBody AdoptionRequestDTO dto)
			throws IllegalOperationException {
		AdoptionRequestEntity entity = adoptionRequestService.createAdoptionRequest(modelMapper.map(dto, AdoptionRequestEntity.class));
		return modelMapper.map(entity, AdoptionRequestDTO.class);
	}
 
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public AdoptionRequestDTO update(@PathVariable Long id, @RequestBody AdoptionRequestDTO dto)
			throws EntityNotFoundException, IllegalOperationException {
		AdoptionRequestEntity entity = adoptionRequestService.updateAdoptionRequest(id, modelMapper.map(dto, AdoptionRequestEntity.class));
		return modelMapper.map(entity, AdoptionRequestDTO.class);
	}
 
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
		adoptionRequestService.deleteAdoptionRequest(id);
	}
}