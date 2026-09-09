package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class MessageEntity extends CommunicationEntity {

    @ManyToOne
    private UserEntity sender;

    @ManyToOne
    private UserEntity receiver;
}