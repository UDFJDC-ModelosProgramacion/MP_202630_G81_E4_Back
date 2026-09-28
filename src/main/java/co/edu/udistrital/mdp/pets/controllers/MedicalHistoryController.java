package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.MedicalHistoryDTO;
import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.services.MedicalHistoryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MedicalHistoryController {

    @Autowired
    private MedicalHistoryService historyService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/pets/{petId}/medical-histories")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalHistoryDTO create(@PathVariable Long petId, @RequestBody MedicalHistoryDTO dto) {
        MedicalHistoryEntity entity = modelMapper.map(dto, MedicalHistoryEntity.class);
        MedicalHistoryEntity created = historyService.createMedicalHistory(petId, entity);
        return modelMapper.map(created, MedicalHistoryDTO.class);
    }

    @GetMapping("/medical-histories")
    @ResponseStatus(HttpStatus.OK)
    public List<MedicalHistoryDTO> findAll() {
        List<MedicalHistoryEntity> histories = historyService.getMedicalHistories();
        return modelMapper.map(histories, new TypeToken<List<MedicalHistoryDTO>>() {}.getType());
    }

    @GetMapping("/medical-histories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MedicalHistoryDTO findOne(@PathVariable Long id) {
        MedicalHistoryEntity history = historyService.getMedicalHistory(id);
        return modelMapper.map(history, MedicalHistoryDTO.class);
    }

    @PutMapping("/medical-histories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MedicalHistoryDTO update(@PathVariable Long id, @RequestBody MedicalHistoryDTO dto) {
        MedicalHistoryEntity entity = modelMapper.map(dto, MedicalHistoryEntity.class);
        MedicalHistoryEntity updated = historyService.updateMedicalHistory(id, entity);
        return modelMapper.map(updated, MedicalHistoryDTO.class);
    }

    @DeleteMapping("/medical-histories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        historyService.deleteMedicalHistory(id);
    }
}
