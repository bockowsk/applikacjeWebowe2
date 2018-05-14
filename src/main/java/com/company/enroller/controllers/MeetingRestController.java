package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	ParticipantService participantService;
	@Autowired
	MeetingService meetingService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") Long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
		// czy nie istnieje
		if (meetingService.findById(meeting.getId()) != null) {
			return new ResponseEntity<Participant>(HttpStatus.CONFLICT);
		}
		meetingService.addMeeting(meeting);

		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/registration", method = RequestMethod.POST)
	public ResponseEntity<?> registerParticipant(@PathVariable("id") long id, @RequestBody Participant participant) {
		// sprawdzanie czy meeting jest i czy participant jest
		// czy jest meeting?
		Meeting meeting=meetingService.findById(id);
		if ( meeting == null) {
			return new ResponseEntity<Participant>(HttpStatus.CONFLICT);
		}
		// czy jest participant - nie trzeba, to spoczywa na Jackson'ie
		
		// dodawanie
		meetingService.registerMeeting(meeting,participant);
		// zwrot meetingow?
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

}
