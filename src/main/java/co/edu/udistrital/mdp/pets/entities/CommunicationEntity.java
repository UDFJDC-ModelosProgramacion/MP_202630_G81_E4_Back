package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class CommunicationEntity extends BaseEntity{
  protected String content, date;
  protected boolean read;
}
