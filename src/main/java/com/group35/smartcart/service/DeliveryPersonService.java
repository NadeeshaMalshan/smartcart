package com.group35.smartcart.service;

import com.group35.smartcart.entity.DeliveryPerson;
import com.group35.smartcart.repository.DeliverPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryPersonService {

    private final DeliverPersonRepository deliverPersonRepository;

    @Autowired
    public DeliveryPersonService(DeliverPersonRepository deliverPersonRepository) {
        this.deliverPersonRepository = deliverPersonRepository;
    }

    // Create a new delivery person
    public DeliveryPerson createDeliveryPerson(DeliveryPerson deliveryPerson) {
        // Set creation and update timestamps
        deliveryPerson.setCreatedAt(LocalDateTime.now());
        deliveryPerson.setUpdatedAt(LocalDateTime.now());
        deliveryPerson.setIsActive(true);
        deliveryPerson.setIsDeleted(false);

        return deliverPersonRepository.save(deliveryPerson);
    }

    // Get all delivery persons (non-deleted)
    public List<DeliveryPerson> getAllDeliveryPersons() {
        return deliverPersonRepository.findByIsDeletedFalse();
    }

    // Get all active delivery persons
    public List<DeliveryPerson> getAllActiveDeliveryPersons() {
        return deliverPersonRepository.findByIsActiveTrueAndIsDeletedFalse();
    }

    // Get delivery person by ID
    public Optional<DeliveryPerson> getDeliveryPersonById(Long id) {
        return deliverPersonRepository.findById(id);
    }

    // Get delivery person by email
    public Optional<DeliveryPerson> getDeliveryPersonByEmail(String email) {
        return deliverPersonRepository.findByEmail(email);
    }

    // Get delivery persons by vehicle type
    public List<DeliveryPerson> getDeliveryPersonsByVehicleType(DeliveryPerson.VehicleType vehicleType) {
        return deliverPersonRepository.findByVehicleTypeAndIsDeletedFalse(vehicleType);
    }

    // Search delivery persons by name
    public List<DeliveryPerson> searchDeliveryPersonsByName(String searchTerm) {
        return deliverPersonRepository.searchByName(searchTerm);
    }

    // Update delivery person
    public DeliveryPerson updateDeliveryPerson(Long id, DeliveryPerson updatedDeliveryPerson) {
        Optional<DeliveryPerson> existingDeliveryPersonOpt = deliverPersonRepository.findById(id);

        if (existingDeliveryPersonOpt.isPresent()) {
            DeliveryPerson existingDeliveryPerson = existingDeliveryPersonOpt.get();

            // Update fields
            existingDeliveryPerson.setName(updatedDeliveryPerson.getName());
            existingDeliveryPerson.setEmail(updatedDeliveryPerson.getEmail());
            existingDeliveryPerson.setContactNumber(updatedDeliveryPerson.getContactNumber());
            existingDeliveryPerson.setVehicleType(updatedDeliveryPerson.getVehicleType());
            existingDeliveryPerson.setVehicleNumber(updatedDeliveryPerson.getVehicleNumber());
            existingDeliveryPerson.setIsActive(updatedDeliveryPerson.getIsActive());

            // Update timestamp
            existingDeliveryPerson.setUpdatedAt(LocalDateTime.now());

            return deliverPersonRepository.save(existingDeliveryPerson);
        }

        return null; // Or throw an exception
    }

    // Soft delete delivery person
    public boolean softDeleteDeliveryPerson(Long id) {
        Optional<DeliveryPerson> deliveryPersonOpt = deliverPersonRepository.findById(id);

        if (deliveryPersonOpt.isPresent()) {
            DeliveryPerson deliveryPerson = deliveryPersonOpt.get();
            deliveryPerson.setIsDeleted(true);
            deliveryPerson.setUpdatedAt(LocalDateTime.now());
            deliverPersonRepository.save(deliveryPerson);
            return true;
        }

        return false;
    }

    // Hard delete delivery person
    public boolean hardDeleteDeliveryPerson(Long id) {
        if (deliverPersonRepository.existsById(id)) {
            deliverPersonRepository.deleteById(id);
            return true;
        }

        return false;
    }

    // Activate delivery person
    public boolean activateDeliveryPerson(Long id) {
        Optional<DeliveryPerson> deliveryPersonOpt = deliverPersonRepository.findById(id);

        if (deliveryPersonOpt.isPresent()) {
            DeliveryPerson deliveryPerson = deliveryPersonOpt.get();
            deliveryPerson.setIsActive(true);
            deliveryPerson.setUpdatedAt(LocalDateTime.now());
            deliverPersonRepository.save(deliveryPerson);
            return true;
        }

        return false;
    }

    // Deactivate delivery person
    public boolean deactivateDeliveryPerson(Long id) {
        Optional<DeliveryPerson> deliveryPersonOpt = deliverPersonRepository.findById(id);

        if (deliveryPersonOpt.isPresent()) {
            DeliveryPerson deliveryPerson = deliveryPersonOpt.get();
            deliveryPerson.setIsActive(false);
            deliveryPerson.setUpdatedAt(LocalDateTime.now());
            deliverPersonRepository.save(deliveryPerson);
            return true;
        }

        return false;
    }
}
