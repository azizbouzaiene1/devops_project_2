package tn.esprit.eventsproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.services.EventServicesImpl;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServicesImplTest {

    @InjectMocks
    private EventServicesImpl eventServices;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddParticipant() {
        Participant participant = new Participant();
        participant.setNom("John");
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant result = eventServices.addParticipant(participant);

        assertNotNull(result);
        assertEquals("John", result.getNom());
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testAddAffectEvenParticipant_WithId() {
        Event event = new Event();
        event.setDescription("Test Event");
        Participant participant = new Participant();
        participant.setIdPart(1);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event, 1);

        assertNotNull(result);
        assertEquals("Test Event", result.getDescription());
        assertTrue(participant.getEvents().contains(event));
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testAddAffectEvenParticipant_WithParticipants() {
        Event event = new Event();
        Participant participant = new Participant();
        participant.setIdPart(1);

        event.setParticipants(new HashSet<>(Collections.singletonList(participant)));
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event);

        assertNotNull(result);
        assertTrue(participant.getEvents().contains(event));
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testAddAffectLog() {
        Logistics logistics = new Logistics();
        logistics.setPrixUnit(100.0f);

        Event event = new Event();
        event.setDescription("Test Event");

        when(eventRepository.findByDescription("Test Event")).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics result = eventServices.addAffectLog(logistics, "Test Event");

        assertNotNull(result);
        assertTrue(event.getLogistics().contains(logistics));
        verify(eventRepository, times(1)).save(event);
        verify(logisticsRepository, times(1)).save(logistics);
    }

    @Test
    void testGetLogisticsDates() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        Event event = new Event();
        Logistics logistics = new Logistics();
        logistics.setReserve(true);

        event.setLogistics(new HashSet<>(Collections.singletonList(logistics)));

        when(eventRepository.findByDateDebutBetween(startDate, endDate))
                .thenReturn(Collections.singletonList(event));

        List<Logistics> result = eventServices.getLogisticsDates(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(logistics));
        verify(eventRepository, times(1)).findByDateDebutBetween(startDate, endDate);
    }

    @Test
    void testCalculCout() {
        Event event = new Event();
        event.setDescription("Test Event");

        Logistics logistics = new Logistics();
        logistics.setPrixUnit(100.0f);
        logistics.setQuantite(2);
        logistics.setReserve(true);

        event.setLogistics(new HashSet<>(Collections.singletonList(logistics)));

        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache(
                "Tounsi", "Ahmed", Tache.ORGANISATEUR))
                .thenReturn(Collections.singletonList(event));

        eventServices.calculCout();

        assertEquals(200.0f, event.getCout());
        verify(eventRepository, times(1))
                .findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR);
        verify(eventRepository, times(1)).save(event);
    }
}