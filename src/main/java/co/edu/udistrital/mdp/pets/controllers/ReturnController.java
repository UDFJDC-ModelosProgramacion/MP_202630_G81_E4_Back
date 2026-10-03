package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.ReturnDTO;
import co.edu.udistrital.mdp.pets.dto.ReturnDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ReturnEntity;
import co.edu.udistrital.mdp.pets.entities.TrialRequestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ReturnService;
import lombok.RequiredArgsConstructor;

/**
 * Class implementing the "returns" resource. Returns are an immutable audit
 * record: they can only be created and read (no update or delete).
 */
@RequiredArgsConstructor
@RestController
public class ReturnController {

    private final ReturnService returnService;

    private final ModelMapper modelMapper;

    /**
     * Returns all returns existing in the application.
     *
     * @return JSONArray {@link ReturnDetailDTO}. Empty list if none exist.
     */
    @GetMapping("/returns")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ReturnDetailDTO> findAll() {
        List<ReturnEntity> returns = returnService.getReturns();
        return modelMapper.map(returns, new TypeToken<List<ReturnDetailDTO>>() {
        }.getType());
    }

    /**
     * Returns the return with the ID received in the URL.
     *
     * @param id Identifier of the return.
     * @return JSON {@link ReturnDetailDTO}
     */
    @GetMapping("/returns/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ReturnDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        ReturnEntity returnEntity = returnService.getReturn(id);
        return modelMapper.map(returnEntity, ReturnDetailDTO.class);
    }

    /**
     * Registers the return of the trial request received in the URL.
     *
     * @param trialRequestId Identifier of the trial request being returned.
     * @param returnDTO      {@link ReturnDTO} The return to be saved.
     * @return JSON {@link ReturnDTO} The saved return with its ID.
     */
    @PostMapping("/trialrequests/{trialRequestId}/return")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ReturnDTO create(@PathVariable Long trialRequestId, @RequestBody ReturnDTO returnDTO){
        ReturnEntity returnEntity = modelMapper.map(returnDTO, ReturnEntity.class);
        TrialRequestEntity trialRequest = new TrialRequestEntity();
        trialRequest.setId(trialRequestId);
        returnEntity.setTrialRequest(trialRequest);
        return modelMapper.map(returnService.createReturn(returnEntity), ReturnDTO.class);
    }
}