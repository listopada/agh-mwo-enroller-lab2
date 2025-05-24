package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;
    ParticipantService participantService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return ResponseEntity.ok(meetings);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(meeting);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
        Meeting createdMeeting = meetingService.add(meeting);
        return ResponseEntity.status(201).body(createdMeeting);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }
        meetingService.delete(meeting);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting updatedMeeting) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }

        meeting.setTitle(updatedMeeting.getTitle());
        meeting.setDescription(updatedMeeting.getDescription());
        meeting.setDate(updatedMeeting.getDate());

        meetingService.update(meeting);
        return ResponseEntity.ok(meeting);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long meetingId, @RequestBody Participant participant) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }

        Participant existingParticipant = participantService.findByLogin(participant.getLogin());
        if (existingParticipant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Participant with login " + participant.getLogin() + " not found.");
        }

        meeting.addParticipant(existingParticipant);
        meetingService.update(meeting);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") long meetingId, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }

        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Participant with login " + login + " not found.");
        }

        if (!meeting.getParticipants().contains(participant)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Participant not registered in this meeting.");
        }

        meeting.removeParticipant(participant);
        meetingService.update(meeting);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }

        Collection<Participant> participants = meeting.getParticipants();
        return ResponseEntity.ok(participants);
    }


}
