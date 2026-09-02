package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperClass
public abstract class CommunicationEntity extends BaseEntity{
  protected String content, date;
  protected boolean read;
}
