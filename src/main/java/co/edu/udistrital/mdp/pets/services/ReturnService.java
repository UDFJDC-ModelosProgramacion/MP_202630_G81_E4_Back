package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ReturnEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ReturnRepository;
import co.edu.udistrital.mdp.pets.repositories.TrialRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
@RequiredArgsConstructor 
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final TrialRequestRepository trialRequestRepository;

    @Transactional(readOnly = true)
    public List<ReturnEntity> getReturns() {
        log.info("Inicia proceso de consulta de todas las devoluciones");
        return returnRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ReturnEntity getReturn(Long returnId) throws EntityNotFoundException {
        log.info("Inicia proceso de consulta de la devolución con id = {}", returnId);
        return returnRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return does not exist"));
    }

    @Transactional(readOnly = true)
public ReturnEntity getReturnByTrialRequest(Long trialRequestId) throws EntityNotFoundException, IllegalOperationException {
    log.info("Inicia proceso de consulta de la devolución de la convivencia con id = {}", trialRequestId);
    if (!trialRequestRepository.existsById(trialRequestId))
        throw new EntityNotFoundException("Trial request does not exist");
    return returnRepository.findByTrialRequestId(trialRequestId)
            .orElseThrow(() -> new IllegalOperationException("The trial request does not have a return registered"));
}

    @Transactional
    public ReturnEntity createReturn(ReturnEntity returnEntity) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación de la devolución");

        if(returnEntity.getTrialRequest() == null || !trialRequestRepository.existsById(returnEntity.getTrialRequest().getId()))
            throw new EntityNotFoundException("Trial request does not exist");

        if (returnRepository.existsByTrialRequestId(returnEntity.getTrialRequest().getId()))
            throw new IllegalOperationException("The trial request already has a return registered");

        if(returnEntity.getDescription() == null || returnEntity.getDescription().isEmpty())
            throw new IllegalOperationException("Description cannot be null or empty");

        if(returnEntity.getReturnType() == null || returnEntity.getReturnType().isEmpty())
            throw new IllegalOperationException("Return type cannot be null or empty");
        return returnRepository.save(returnEntity);
    }
}