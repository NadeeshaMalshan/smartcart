package com.group35.smartcart.controller;

import com.group35.smartcart.entity.DeliveryPerson;
import com.group35.smartcart.service.DeliveryPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/delivery-persons")
public class DeliveryPersonController {

    private final DeliveryPersonService deliveryPersonService;

    @Autowired
    public DeliveryPersonController(DeliveryPersonService deliveryPersonService) {
        this.deliveryPersonService = deliveryPersonService;
    }

    // Get all delivery persons
    @GetMapping
    public ResponseEntity<?> getAllDeliveryPersons() {
        try {
            List<DeliveryPerson> deliveryPersons = deliveryPersonService.getAllDeliveryPersons();
            return ResponseEntity.ok(deliveryPersons);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve delivery persons");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get all active delivery persons
    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveDeliveryPersons() {
        try {
            List<DeliveryPerson> activeDeliveryPersons = deliveryPersonService.getAllActiveDeliveryPersons();
            return ResponseEntity.ok(activeDeliveryPersons);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve active delivery persons");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get delivery person by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getDeliveryPersonById(@PathVariable Long id) {
        try {
            Optional<DeliveryPerson> deliveryPerson = deliveryPersonService.getDeliveryPersonById(id);
            if (deliveryPerson.isPresent()) {
                return ResponseEntity.ok(deliveryPerson.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Delivery person not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve delivery person");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get delivery persons by vehicle type
    @GetMapping("/vehicle-type/{vehicleType}")
    public ResponseEntity<?> getDeliveryPersonsByVehicleType(@PathVariable String vehicleType) {
        try {
            DeliveryPerson.VehicleType type = DeliveryPerson.VehicleType.valueOf(vehicleType.toUpperCase());
            List<DeliveryPerson> deliveryPersons = deliveryPersonService.getDeliveryPersonsByVehicleType(type);
            return ResponseEntity.ok(deliveryPersons);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid vehicle type: " + vehicleType);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve delivery persons by vehicle type");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Search delivery persons by name
    @GetMapping("/search")
    public ResponseEntity<?> searchDeliveryPersonsByName(@RequestParam String name) {
        try {
            List<DeliveryPerson> deliveryPersons = deliveryPersonService.searchDeliveryPersonsByName(name);
            return ResponseEntity.ok(deliveryPersons);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to search delivery persons");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Create a new delivery person
    @PostMapping
    public ResponseEntity<?> createDeliveryPerson(@Valid @RequestBody DeliveryPerson deliveryPerson) {
        try {
            DeliveryPerson createdDeliveryPerson = deliveryPersonService.createDeliveryPerson(deliveryPerson);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDeliveryPerson);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to create delivery person");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Update delivery person
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDeliveryPerson(@PathVariable Long id, @Valid @RequestBody DeliveryPerson deliveryPerson) {
        try {
            DeliveryPerson updatedDeliveryPerson = deliveryPersonService.updateDeliveryPerson(id, deliveryPerson);
            if (updatedDeliveryPerson != null) {
                return ResponseEntity.ok(updatedDeliveryPerson);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Delivery person not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to update delivery person");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Soft delete delivery person
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteDeliveryPerson(@PathVariable Long id) {
        try {
            boolean deleted = deliveryPersonService.softDeleteDeliveryPerson(id);
            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Delivery person soft deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Delivery person not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to delete delivery person");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Hard delete delivery person
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<?> hardDeleteDeliveryPerson(@PathVariable Long id) {
        try {
            boolean deleted = deliveryPersonService.hardDeleteDeliveryPerson(id);
            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Delivery person permanently deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Delivery person not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to permanently delete delivery person");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Activate delivery person
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateDeliveryPerson(@PathVariable Long id) {
        try {
            boolean activated = deliveryPersonService.activateDeliveryPerson(id);
            if (activated) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Delivery person activated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Delivery person not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to activate delivery person");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Deactivate delivery person
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateDeliveryPerson(@PathVariable Long id) {
        try {
            boolean deactivated = deliveryPersonService.deactivateDeliveryPerson(id);
            if (deactivated) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Delivery person deactivated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Delivery person not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to deactivate delivery person");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/deliver-person-management")
    public String deliveryPersonManagement(){
        return "deliver-person-management";
    }
}
