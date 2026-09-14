package co.edu.udistrital.mdp.ZZZ.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.ZZZ.entities.ReturnEntity;
import co.edu.udistrital.mdp.ZZZ.repositories.ReturnRepository;
import co.edu.udistrital.mdp.ZZZ.repositories.TrialRequestRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class ReturnService {

    @Autowired
    private ReturnRepository returnRepository;
    @Autowired
    private TrialRequestRepository trialRequestRepository;

    @Transactional
    public ReturnEntity createReturn(ReturnEntity returnEntity) {
        log.info("Inicia proceso de creación de la devolución");

        if(returnEntity.getTrialRequest() == null || !trialRequestRepository.existsById(returnEntity.getTrialRequest().getId()))
            throw new IllegalArgumentException("Trial request does not exist");

        if(returnEntity.getDescription() == null || returnEntity.getDescription().isEmpty())
            throw new IllegalArgumentException("Description cannot be null or empty");

        if(returnEntity.getReturnType() == null || returnEntity.getReturnType().isEmpty())
            throw new IllegalArgumentException("Return type cannot be null or empty");
        return returnRepository.save(returnEntity);
    }
}
