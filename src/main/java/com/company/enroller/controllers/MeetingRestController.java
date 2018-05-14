package com.company.enroller.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.EmptyJsonResponse;
import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.model.SearchString;
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

	@RequestMapping(value = "/sorted", method = RequestMethod.GET)
	public ResponseEntity<?> getSortedMeetings() {
		// pobranie wszystkich
		Collection<Meeting> meetings = meetingService.getAll();
		// zmiana na ArrayList
		ArrayList<Meeting> meetingsList = new ArrayList<Meeting>(Collections.unmodifiableCollection(meetings));
		// SORTOWANIE
		Collections.sort(meetingsList);
		return new ResponseEntity<Collection<Meeting>>(meetingsList, HttpStatus.OK);
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
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<Participant>(HttpStatus.CONFLICT);
		}
		// czy jest participant - nie trzeba, to spoczywa na Jackson'ie

		// dodawanie
		meetingService.registerMeeting(meeting, participant);
		// zwrot meetingow?
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	// pobranie uczestnikow
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipants(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		Collection<Participant> participants = meeting.getParticipants();
		HashSet<String> logins = new HashSet<String>();
		for (Participant p : participants) {
			logins.add(p.getLogin());
		}
		return new ResponseEntity<Collection<String>>(logins, HttpStatus.OK);

	}

	// kasowanie spotkan
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
		Meeting requestedMeeting = meetingService.findById(id);
		if (requestedMeeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meetingService.deleteMeeting(requestedMeeting);
		return new ResponseEntity<Meeting>(requestedMeeting, HttpStatus.OK);
	}

	// update meeting
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting requestedMeeting) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		String newTitle = requestedMeeting.getTitle();
		String newDescription = requestedMeeting.getDescription();
		String newDate = requestedMeeting.getDate();
		meeting.setTitle(newTitle);
		meeting.setDescription(newDescription);
		meeting.setDate(newDate);
		meetingService.addMeeting(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	// kasowanie uczestnika ze spotkania
	@RequestMapping(value = "/{id}/{id2}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id, @PathVariable("id2") String id2) {
		Meeting requestedMeeting = meetingService.findById(id);
		// sprawdzanie czy jest taki meeting
		if (requestedMeeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		Participant participant = participantService.findByLogin(id2);
		// sprawdzanie czy participant istnieje
		if (participant == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meetingService.removeParticipant(requestedMeeting, participant);
		return new ResponseEntity<Meeting>(requestedMeeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public ResponseEntity<?> searchMeetings(@RequestBody SearchString searchString) {
		HashSet<Meeting> newSet = new HashSet<Meeting>();
		Collection<Meeting> meetings = meetingService.getAll();
		for (Meeting m : meetings) {
			if (!m.getTitle().contains(searchString.getTitle())
					|| !m.getDescription().contains(searchString.getDescription())) {
				newSet.add(m);
			}
		}
		for (Meeting m : newSet) {
			meetings.remove(m);
		}
		if (meetings.size() > 0) {
			return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
		} else {
			return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/search/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> searchMeetingsByUser(@RequestBody String id) {
		// sprawdzanie czy taki user jest
		Participant participant = participantService.findByLogin(id);
		if (participant == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		// lista meetingow, w ktorych jest
		HashSet<Meeting> newSet = new HashSet<Meeting>();
		Collection<Meeting> meetings = meetingService.getAll();
		for (Meeting m : meetings) {
			// sprawdzanie czy jest participant
			if (!m.getParticipants().contains(participant)) {
				newSet.add(m);
			}
		}
		for (Meeting m : newSet) {
			meetings.remove(m);
		}

		if (meetings.size() > 0) {
			return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
		} else {
			return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
		}
	}

}
