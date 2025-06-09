package com.example.TFG_WebApp.Unit_TESTs;

import com.example.TFG_WebApp.Exceptions.DuplicateResourceException;
import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Repositories.CoachRepository;
import com.example.TFG_WebApp.Services.CoachService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachServiceTest {

    @Mock
    private CoachRepository coachRepository;

    @InjectMocks
    private CoachService coachService;

    @Test
    void testGetCoaches() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Coach> coachPage = new PageImpl<>(Collections.singletonList(new Coach()));
        when(coachRepository.findAll(pageable)).thenReturn(coachPage);

        Page<Coach> result = coachService.getCoaches(pageable);

        assertEquals(1, result.getTotalElements());
        verify(coachRepository).findAll(pageable);
    }

    @Test
    void testGetCoachById_Success() {
        Coach coach = new Coach();
        coach.setLicenseNumber("COACH123");
        when(coachRepository.findById("COACH123")).thenReturn(Optional.of(coach));

        Coach result = coachService.getCoachById("COACH123");

        assertEquals("COACH123", result.getLicenseNumber());
        verify(coachRepository).findById("COACH123");
    }

    @Test
    void testGetCoachById_NotFound() {
        when(coachRepository.findById("XYZ999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            coachService.getCoachById("XYZ999");
        });

        verify(coachRepository).findById("XYZ999");
    }

    @Test
    void testCreateCoach_Success() {
        Coach coach = new Coach();
        coach.setLicenseNumber("NEWC001");

        when(coachRepository.existsById("NEWC001")).thenReturn(false);
        when(coachRepository.save(coach)).thenReturn(coach);

        Coach result = coachService.createCoach(coach);

        assertEquals("NEWC001", result.getLicenseNumber());
        verify(coachRepository).save(coach);
    }

    @Test
    void testCreateCoach_Duplicate() {
        Coach coach = new Coach();
        coach.setLicenseNumber("DUPC001");

        when(coachRepository.existsById("DUPC001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            coachService.createCoach(coach);
        });

        verify(coachRepository, never()).save(any());
    }

    @Test
    void testUpdateCoach() {
        Coach existing = new Coach();
        existing.setLicenseNumber("UPDC001");
        existing.setFirstName("OldName");

        Coach updated = new Coach();
        updated.setFirstName("NewName");

        when(coachRepository.findById("UPDC001")).thenReturn(Optional.of(existing));
        when(coachRepository.save(existing)).thenReturn(existing);

        Coach result = coachService.updateCoach("UPDC001", updated);

        assertEquals("NewName", result.getFirstName());
        verify(coachRepository).save(existing);
    }

    @Test
    void testDeleteCoach() {
        Coach coach = new Coach();
        coach.setLicenseNumber("DELC001");

        when(coachRepository.findById("DELC001")).thenReturn(Optional.of(coach));
        doNothing().when(coachRepository).delete(coach);

        coachService.deleteCoach("DELC001");

        verify(coachRepository).delete(coach);
    }

    @Test
    void testGetFilteredCoaches_OnlyFirstName() {
        Pageable pageable = PageRequest.of(0, 10);

        doAnswer(invocation -> {
            Specification<?> spec = invocation.getArgument(0);
            Pageable pg = invocation.getArgument(1);
            return new PageImpl<>(Collections.singletonList(new Coach()));
        }).when(coachRepository).findAll(any(Specification.class), eq(pageable));

        Page<Coach> result = coachService.getFilteredCoaches("John", null, null, null, pageable);

        assertEquals(1, result.getContent().size());
        verify(coachRepository).findAll(any(Specification.class), eq(pageable));
    }
}
