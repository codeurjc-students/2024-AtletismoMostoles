package com.example.service1.Unit_TESTs;

import com.example.service1.Exceptions.DuplicateResourceException;
import com.example.service1.Exceptions.ResourceNotFoundException;
import com.example.service1.Entities.Athlete;
import com.example.service1.Repositories.AthleteRepository;
import com.example.service1.Services.AthleteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AthleteServiceTest {

    @Mock
    private AthleteRepository athleteRepository;

    @InjectMocks
    private AthleteService athleteService;

    @Test
    void testGetAthletes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Athlete> athletePage = new PageImpl<>(Collections.singletonList(new Athlete()));
        when(athleteRepository.findAll(pageable)).thenReturn(athletePage);

        Page<Athlete> result = athleteService.getAthletes(pageable);

        assertEquals(1, result.getTotalElements());
        verify(athleteRepository).findAll(pageable);
    }

    @Test
    void testGetAthleteById_Success() {
        Athlete athlete = new Athlete();
        athlete.setLicenseNumber("ABC123");
        when(athleteRepository.findById("ABC123")).thenReturn(Optional.of(athlete));

        Athlete result = athleteService.getAthleteById("ABC123");

        assertEquals("ABC123", result.getLicenseNumber());
        verify(athleteRepository).findById("ABC123");
    }

    @Test
    void testGetAthleteById_NotFound() {
        when(athleteRepository.findById("XYZ999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            athleteService.getAthleteById("XYZ999");
        });

        verify(athleteRepository).findById("XYZ999");
    }

    @Test
    void testCreateAthlete_Success() {
        Athlete athlete = new Athlete();
        athlete.setLicenseNumber("NEW001");

        when(athleteRepository.existsById("NEW001")).thenReturn(false);
        when(athleteRepository.save(athlete)).thenReturn(athlete);

        Athlete result = athleteService.createAthlete(athlete);

        assertEquals("NEW001", result.getLicenseNumber());
        verify(athleteRepository).save(athlete);
    }

    @Test
    void testCreateAthlete_Duplicate() {
        Athlete athlete = new Athlete();
        athlete.setLicenseNumber("DUP001");

        when(athleteRepository.existsById("DUP001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            athleteService.createAthlete(athlete);
        });

        verify(athleteRepository, never()).save(any());
    }

    @Test
    void testUpdateAthlete() {
        Athlete existing = new Athlete();
        existing.setLicenseNumber("UPD001");
        existing.setFirstName("OldName");

        Athlete updated = new Athlete();
        updated.setFirstName("NewName");

        when(athleteRepository.findById("UPD001")).thenReturn(Optional.of(existing));
        when(athleteRepository.save(existing)).thenReturn(existing);

        Athlete result = athleteService.updateAthlete("UPD001", updated);

        assertEquals("NewName", result.getFirstName());
        verify(athleteRepository).save(existing);
    }

    @Test
    void testDeleteAthlete() {
        Athlete athlete = new Athlete();
        athlete.setLicenseNumber("DEL001");

        when(athleteRepository.findById("DEL001")).thenReturn(Optional.of(athlete));
        doNothing().when(athleteRepository).delete(athlete);

        athleteService.deleteAthlete("DEL001");

        verify(athleteRepository).delete(athlete);
    }

    @Test
    void testGetFilteredAthletes_OnlyFirstName() {
        Pageable pageable = PageRequest.of(0, 10);

        doAnswer(invocation -> new PageImpl<>(Collections.singletonList(new Athlete()))).when(athleteRepository).findAll(any(Specification.class), eq(pageable));

        Page<Athlete> result = athleteService.getFilteredAthletes("John", null, null, null, null, pageable);

        assertEquals(1, result.getContent().size());
        verify(athleteRepository).findAll(any(Specification.class), eq(pageable));
    }
}
