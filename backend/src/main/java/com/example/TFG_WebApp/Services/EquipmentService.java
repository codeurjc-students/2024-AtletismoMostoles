package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Equipment;
import com.example.TFG_WebApp.Repositories.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
public class EquipmentService {
    @Autowired
    private EquipmentRepository equipmentRepository;

    public Page<Equipment> getAllEquipment(Pageable pageable) {
        return equipmentRepository.findAll(pageable);
    }

    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + id));
    }

    public Equipment createEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(Long id, Equipment updatedEquipment) {
        Equipment existingEquipment = getEquipmentById(id);
        existingEquipment.setName(updatedEquipment.getName());
        existingEquipment.setDescription(updatedEquipment.getDescription());
        existingEquipment.setImageLink(updatedEquipment.getImageLink());
        return equipmentRepository.save(existingEquipment);
    }

    public void deleteEquipment(Long id) {
        Equipment equipment = getEquipmentById(id);
        equipmentRepository.delete(equipment);
    }
}