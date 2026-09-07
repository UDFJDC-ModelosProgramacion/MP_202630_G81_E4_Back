package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class MessageEntity extends CommunicationEntity {
    // Aquí van los atributos propios de MessageEntity, además de
    // content, date y read que ya heredas de CommunicationEntity
}