package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

@Component("participantService")
public class ParticipantService {

	DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Participant> getAll() {
		return connector.getSession().createCriteria(Participant.class).list();
	}
	
	public Participant findByLogin(String what) {
		return (Participant)connector.getSession().get(Participant.class, what);
	}

	public void addParticipant(Participant participant) {
		Transaction transaction=connector.getSession().beginTransaction();
		connector.getSession().save(participant);
		transaction.commit();	
	}

	public void delete(Participant requestedParticipant) {
		Transaction transaction=connector.getSession().beginTransaction();
		connector.getSession().delete(requestedParticipant);
		transaction.commit();	
		
		
	} 

}
