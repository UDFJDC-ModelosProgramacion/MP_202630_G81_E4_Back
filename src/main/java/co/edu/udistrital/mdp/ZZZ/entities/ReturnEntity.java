package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ReturnEntity extends BaseEntity {
    private String returnType;
    private String description;
    private String date;

    @PodamExclude
    @OneToOne
    private TrialRequestEntity trialRequest;
}
