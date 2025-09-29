package com.group35.smartcart.controller;

import com.group35.smartcart.entity.AssignmentStatus;
import com.group35.smartcart.entity.DeliveryAssignment;
import com.group35.smartcart.entity.DeliveryPerson;
import com.group35.smartcart.service.DeliveryAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/delivery-assignments")
public class DeliveryAssignmentController {

    private final DeliveryAssignmentService deliveryAssignmentService;

    @Autowired
    public DeliveryAssignmentController(DeliveryAssignmentService deliveryAssignmentService) {
        this.deliveryAssignmentService = deliveryAssignmentService;
    }

    // Get all delivery assignments
    @GetMapping
    public ResponseEntity<?> getAllAssignments() {
        try {
            List<DeliveryAssignment> assignments = deliveryAssignmentService.getAllAssignments();
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            System.out.println(e.toString());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve delivery assignments");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get assignment by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable Long id) {
        try {
            Optional<DeliveryAssignment> assignment = deliveryAssignmentService.getAssignmentById(id);
            if (assignment.isPresent()) {
                return ResponseEntity.ok(assignment.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Assignment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve assignment");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Create new delivery assignment
    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = Long.valueOf(request.get("orderId").toString());
            Long deliveryPersonId = Long.valueOf(request.get("deliveryPersonId").toString());
            String notes = request.get("notes") != null ? request.get("notes").toString() : "";

            DeliveryAssignment assignment = deliveryAssignmentService.createAssignment(orderId, deliveryPersonId, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Assignment created successfully");
            response.put("assignment", assignment);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to create assignment");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get assignments by delivery person ID
    @GetMapping("/delivery-person/{deliveryPersonId}")
    public ResponseEntity<?> getAssignmentsByDeliveryPersonId(@PathVariable Long deliveryPersonId) {
        try {
            List<DeliveryAssignment> assignments = deliveryAssignmentService.getAssignmentsByDeliveryPersonId(deliveryPersonId);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve assignments for delivery person");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get active assignments by delivery person ID
    @GetMapping("/delivery-person/{deliveryPersonId}/active")
    public ResponseEntity<?> getActiveAssignmentsByDeliveryPersonId(@PathVariable Long deliveryPersonId) {
        try {
            List<DeliveryAssignment> assignments = deliveryAssignmentService.getActiveAssignmentsByDeliveryPersonId(deliveryPersonId);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve active assignments for delivery person");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get assignment by order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getAssignmentByOrderId(@PathVariable Long orderId) {
        try {
            Optional<DeliveryAssignment> assignment = deliveryAssignmentService.getAssignmentByOrderId(orderId);
            if (assignment.isPresent()) {
                return ResponseEntity.ok(assignment.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "No assignment found for this order");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve assignment for order");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get assignments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAssignmentsByStatus(@PathVariable String status) {
        try {
            AssignmentStatus assignmentStatus = AssignmentStatus.valueOf(status.toUpperCase());
            List<DeliveryAssignment> assignments = deliveryAssignmentService.getAssignmentsByStatus(assignmentStatus);
            return ResponseEntity.ok(assignments);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid status: " + status);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve assignments by status");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Mark assignment as picked up
    @PutMapping("/{id}/pickup")
    public ResponseEntity<?> markAsPickedUp(@PathVariable Long id, @RequestBody(required = false) Map<String, String> request) {
        try {
            String notes = request != null && request.get("notes") != null ? request.get("notes") : "";
            DeliveryAssignment assignment = deliveryAssignmentService.markAsPickedUp(id, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Assignment marked as picked up");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to mark assignment as picked up");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Mark assignment as delivered
    @PutMapping("/{id}/deliver")
    public ResponseEntity<?> markAsDelivered(@PathVariable Long id, @RequestBody(required = false) Map<String, String> request) {
        try {
            String notes = request != null && request.get("notes") != null ? request.get("notes") : "";
            DeliveryAssignment assignment = deliveryAssignmentService.markAsDelivered(id, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Assignment marked as delivered");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to mark assignment as delivered");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Cancel assignment
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAssignment(@PathVariable Long id, @RequestBody(required = false) Map<String, String> request) {
        try {
            String reason = request != null && request.get("reason") != null ? request.get("reason") : "No reason provided";
            DeliveryAssignment assignment = deliveryAssignmentService.cancelAssignment(id, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Assignment cancelled successfully");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to cancel assignment");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Reassign to different delivery person
    @PutMapping("/{id}/reassign")
    public ResponseEntity<?> reassignDeliveryPerson(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long newDeliveryPersonId = Long.valueOf(request.get("newDeliveryPersonId").toString());
            String reason = request.get("reason") != null ? request.get("reason").toString() : "Reassigned by administrator";
            
            DeliveryAssignment assignment = deliveryAssignmentService.reassignDeliveryPerson(id, newDeliveryPersonId, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Assignment reassigned successfully");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to reassign assignment");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Update assignment notes
    @PutMapping("/{id}/notes")
    public ResponseEntity<?> updateNotes(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String notes = request.get("notes");
            if (notes == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Notes field is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            DeliveryAssignment assignment = deliveryAssignmentService.updateNotes(id, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notes updated successfully");
            response.put("assignment", assignment);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to update notes");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get overdue assignments
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueAssignments(@RequestParam(defaultValue = "24") int hoursAgo) {
        try {
            List<DeliveryAssignment> assignments = deliveryAssignmentService.getOverdueAssignments(hoursAgo);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve overdue assignments");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get assignments by date range
    @GetMapping("/date-range")
    public ResponseEntity<?> getAssignmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<DeliveryAssignment> assignments = deliveryAssignmentService.getAssignmentsByDateRange(startDate, endDate);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve assignments by date range");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Search assignments by notes
    @GetMapping("/search")
    public ResponseEntity<?> searchAssignmentsByNotes(@RequestParam String searchTerm) {
        try {
            List<DeliveryAssignment> assignments = deliveryAssignmentService.searchAssignmentsByNotes(searchTerm);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to search assignments");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get assignment statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getAssignmentStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("assigned", deliveryAssignmentService.getAssignmentCountByStatus(AssignmentStatus.ASSIGNED));
            stats.put("pickedUp", deliveryAssignmentService.getAssignmentCountByStatus(AssignmentStatus.PICKED_UP));
            stats.put("delivered", deliveryAssignmentService.getAssignmentCountByStatus(AssignmentStatus.DELIVERED));
            stats.put("cancelled", deliveryAssignmentService.getAssignmentCountByStatus(AssignmentStatus.CANCELLED));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve assignment statistics");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get delivery performance statistics for a delivery person
    @GetMapping("/delivery-person/{deliveryPersonId}/stats")
    public ResponseEntity<?> getDeliveryPersonStats(@PathVariable Long deliveryPersonId) {
        try {
            Object[] stats = deliveryAssignmentService.getDeliveryStats(deliveryPersonId);
            long assignmentCount = deliveryAssignmentService.getAssignmentCountByDeliveryPersonId(deliveryPersonId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalAssignments", assignmentCount);
            response.put("deliveredCount", stats[0]);
            response.put("averageDeliveryTime", stats[1]);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve delivery person statistics");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get available delivery persons
    @GetMapping("/available-delivery-persons")
    public ResponseEntity<?> getAvailableDeliveryPersons() {
        try {
            List<DeliveryPerson> deliveryPersons = deliveryAssignmentService.getAvailableDeliveryPersons();
            return ResponseEntity.ok(deliveryPersons);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve available delivery persons");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Check if order has assignment
    @GetMapping("/order/{orderId}/has-assignment")
    public ResponseEntity<?> checkOrderHasAssignment(@PathVariable Long orderId) {
        try {
            boolean hasAssignment = deliveryAssignmentService.hasAssignment(orderId);
            return ResponseEntity.ok(Map.of("hasAssignment", hasAssignment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check assignment: " + e.getMessage()));
        }
    }

    // Delete assignment (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id) {
        try {
            deliveryAssignmentService.deleteAssignment(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Assignment deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to delete assignment");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

  
   
}