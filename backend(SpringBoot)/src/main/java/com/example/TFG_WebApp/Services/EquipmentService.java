package com.example.TFG_WebApp.Services;

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

    public Equipment createEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(Long id, Equipment updatedEquipment) {
        return equipmentRepository.findById(id).map(existingEquipment -> {
            existingEquipment.setName(updatedEquipment.getName());
            existingEquipment.setDescription(updatedEquipment.getDescription());
            existingEquipment.setImageLink(updatedEquipment.getImageLink());
            existingEquipment.setDisciplines(updatedEquipment.getDisciplines());
            return equipmentRepository.save(existingEquipment);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found"));
    }

    public void deleteEquipment(Long id) {
        try {
            equipmentRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found");
        }
    }

    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found"));
    }

    public Page<Equipment> getPaginatedAndFilteredEquipment(String name, String disciplineName, Pageable pageable) {
        return equipmentRepository.findAll((Specification<Equipment>) (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (name != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (disciplineName != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.join("disciplines").get("name"), "%" + disciplineName + "%"));
            }

            return predicates;
        }, pageable);
    }
}
