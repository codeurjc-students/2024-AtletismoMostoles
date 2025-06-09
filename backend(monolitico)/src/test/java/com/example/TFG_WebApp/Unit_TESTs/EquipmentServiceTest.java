package com.example.TFG_WebApp.Unit_TESTs;

import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Equipment;
import com.example.TFG_WebApp.Repositories.EquipmentRepository;
import com.example.TFG_WebApp.Services.EquipmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    @Test
    void testGetAllEquipment() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Equipment> equipmentPage = new PageImpl<>(Collections.singletonList(new Equipment()));
        when(equipmentRepository.findAll(pageable)).thenReturn(equipmentPage);

        Page<Equipment> result = equipmentService.getAllEquipment(pageable);

        assertEquals(1, result.getTotalElements());
        verify(equipmentRepository).findAll(pageable);
    }

    @Test
    void testGetEquipmentById_Success() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        Equipment result = equipmentService.getEquipmentById(1L);

        assertEquals(1L, result.getId());
        verify(equipmentRepository).findById(1L);
    }

    @Test
    void testGetEquipmentById_NotFound() {
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            equipmentService.getEquipmentById(99L);
        });

        verify(equipmentRepository).findById(99L);
    }

    @Test
    void testCreateEquipment() {
        Equipment newEquipment = new Equipment();
        newEquipment.setName("New Equipment");

        when(equipmentRepository.save(newEquipment)).thenReturn(newEquipment);

        Equipment result = equipmentService.createEquipment(newEquipment);

        assertEquals("New Equipment", result.getName());
        verify(equipmentRepository).save(newEquipment);
    }

    @Test
    void testUpdateEquipment() {
        Equipment existing = new Equipment();
        existing.setId(1L);
        existing.setName("Old Equipment");

        Equipment updated = new Equipment();
        updated.setName("Updated Equipment");

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(equipmentRepository.save(existing)).thenReturn(existing);

        Equipment result = equipmentService.updateEquipment(1L, updated);

        assertEquals("Updated Equipment", result.getName());
        verify(equipmentRepository).save(existing);
    }

    @Test
    void testDeleteEquipment() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        doNothing().when(equipmentRepository).delete(equipment);

        equipmentService.deleteEquipment(1L);

        verify(equipmentRepository).delete(equipment);
    }
}
