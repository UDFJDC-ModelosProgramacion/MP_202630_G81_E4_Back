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
 
import co.edu.udistrital.mdp.pets.dto.TrialRequestDTO;
import co.edu.udistrital.mdp.pets.dto.TrialRequestDetailDTO;
import co.edu.udistrital.mdp.pets.entities.TrialRequestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.TrialRequestService;
 
import lombok.RequiredArgsConstructor;
 
/**
 * Controlador REST del recurso TrialRequest. Las excepciones de negocio
 * (EntityNotFoundException -> 404, IllegalOperationException -> 412) son
 * traducidas por RestExceptionHandler.
 */
@RestController
@RequestMapping("/trial-requests")
@RequiredArgsConstructor
public class TrialRequestController {
 
	private final TrialRequestService trialRequestService;
 
	private final ModelMapper modelMapper;
 
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<TrialRequestDetailDTO> findAll() {
		List<TrialRequestEntity> entities = trialRequestService.getTrialRequests();
		return modelMapper.map(entities, new TypeToken<List<TrialRequestDetailDTO>>() {
		}.getType());
	}
 
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public TrialRequestDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
		TrialRequestEntity entity = trialRequestService.getTrialRequest(id);
		return modelMapper.map(entity, TrialRequestDetailDTO.class);
	}
 
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public TrialRequestDTO create(@RequestBody TrialRequestDTO dto)
			throws EntityNotFoundException, IllegalOperationException {
		TrialRequestEntity entity = trialRequestService.createTrialRequest(modelMapper.map(dto, TrialRequestEntity.class));
		return modelMapper.map(entity, TrialRequestDTO.class);
	}
 
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public TrialRequestDTO update(@PathVariable Long id, @RequestBody TrialRequestDTO dto)
			throws EntityNotFoundException, IllegalOperationException {
		TrialRequestEntity entity = trialRequestService.updateTrialRequest(id, modelMapper.map(dto, TrialRequestEntity.class));
		return modelMapper.map(entity, TrialRequestDTO.class);
	}
 
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
		trialRequestService.deleteTrialRequest(id);
	}
}
