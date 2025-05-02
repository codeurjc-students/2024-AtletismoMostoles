package com.example.TFG_WebApp.Unit_TESTs;

import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Models.Discipline;
import com.example.TFG_WebApp.Repositories.CoachRepository;
import com.example.TFG_WebApp.Repositories.DisciplineRepository;
import com.example.TFG_WebApp.Services.DisciplineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisciplineServiceTest {

    @Mock
    private DisciplineRepository disciplineRepository;

    @Mock
    private CoachRepository coachRepository;

    @InjectMocks
    private DisciplineService disciplineService;

    @Test
    void testGetDisciplines() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Discipline> disciplinePage = new PageImpl<>(Collections.singletonList(new Discipline()));
        when(disciplineRepository.findAll(pageable)).thenReturn(disciplinePage);

        Page<Discipline> result = disciplineService.getDisciplines(pageable);

        assertEquals(1, result.getTotalElements());
        verify(disciplineRepository).findAll(pageable);
    }

    @Test
    void testGetDisciplineById_Success() {
        Discipline discipline = new Discipline();
        discipline.setId(1L);
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(discipline));

        Discipline result = disciplineService.getDisciplineById(1L);

        assertEquals(1L, result.getId());
        verify(disciplineRepository).findById(1L);
    }

    @Test
    void testGetDisciplineById_NotFound() {
        when(disciplineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            disciplineService.getDisciplineById(99L);
        });

        verify(disciplineRepository).findById(99L);
    }

    @Test
    void testCreateDiscipline_WithCoaches() {
        Discipline discipline = new Discipline();
        Coach coach = new Coach();
        coach.setLicenseNumber("COACH001");

        discipline.setCoaches(Set.of(coach));

        when(coachRepository.findById("COACH001")).thenReturn(Optional.of(coach));
        when(disciplineRepository.save(any(Discipline.class))).thenReturn(discipline);

        Discipline result = disciplineService.createDiscipline(discipline);

        assertEquals(1, result.getCoaches().size());
        verify(coachRepository).findById("COACH001");
        verify(disciplineRepository).save(discipline);
    }

    @Test
    void testUpdateDiscipline() {
        Discipline existing = new Discipline();
        existing.setId(1L);
        existing.setName("OldName");

        Discipline updated = new Discipline();
        updated.setName("NewName");

        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(disciplineRepository.save(existing)).thenReturn(existing);

        Discipline result = disciplineService.updateDiscipline(1L, updated);

        assertEquals("NewName", result.getName());
        verify(disciplineRepository).save(existing);
    }

    @Test
    void testDeleteDiscipline() {
        Discipline discipline = new Discipline();
        discipline.setId(1L);

        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(discipline));
        doNothing().when(disciplineRepository).delete(discipline);

        disciplineService.deleteDiscipline(1L);

        verify(disciplineRepository).delete(discipline);
    }
}
