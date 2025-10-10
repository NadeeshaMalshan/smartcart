package com.group35.smartcart.service;

import com.group35.smartcart.entity.AssignmentStatus;
import com.group35.smartcart.entity.DeliveryAssignment;
import com.group35.smartcart.entity.DeliveryPerson;
import com.group35.smartcart.entity.Order;
import com.group35.smartcart.repository.DeliveryAssignmentRepository;
import com.group35.smartcart.repository.DeliverPersonRepository;
import com.group35.smartcart.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeliveryAssignmentService {

    private final DeliveryAssignmentRepository deliveryAssignmentRepository;
    private final DeliverPersonRepository deliverPersonRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public DeliveryAssignmentService(DeliveryAssignmentRepository deliveryAssignmentRepository,
                                   DeliverPersonRepository deliverPersonRepository,
                                   OrderRepository orderRepository) {
        this.deliveryAssignmentRepository = deliveryAssignmentRepository;
        this.deliverPersonRepository = deliverPersonRepository;
        this.orderRepository = orderRepository;
    }

    // Create a new delivery assignment
    public DeliveryAssignment createAssignment(Long orderId, Long deliveryPersonId, String notes) {
        // Validate order exists and doesn't already have an assignment
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }

        Order order = orderOpt.get();
        if (deliveryAssignmentRepository.existsByOrder(order)) {
            throw new IllegalStateException("Order already has a delivery assignment");
        }

        // Validate delivery person exists and is active
        Optional<DeliveryPerson> deliveryPersonOpt = deliverPersonRepository.findById(deliveryPersonId);
        if (deliveryPersonOpt.isEmpty()) {
            throw new IllegalArgumentException("Delivery person not found with ID: " + deliveryPersonId);
        }

        DeliveryPerson deliveryPerson = deliveryPersonOpt.get();
        if (!deliveryPerson.getIsActive() || deliveryPerson.getIsDeleted()) {
            throw new IllegalStateException("Delivery person is not active or has been deleted");
        }

        // Create new assignment
        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrder(order);
        assignment.setDeliveryPerson(deliveryPerson);
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setNotes(notes);
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());

        return deliveryAssignmentRepository.save(assignment);
    }

    // Get all assignments
    public List<DeliveryAssignment> getAllAssignments() {
        return deliveryAssignmentRepository.findAllByOrderByAssignedAtDesc();
    }

    // Get assignment by ID
    public Optional<DeliveryAssignment> getAssignmentById(Long id) {
        return deliveryAssignmentRepository.findById(id);
    }

    // Get assignment by order ID
    public Optional<DeliveryAssignment> getAssignmentByOrderId(Long orderId) {
        return deliveryAssignmentRepository.findByOrderPaymentId(orderId);
    }

    // Get assignments by delivery person ID
    public List<DeliveryAssignment> getAssignmentsByDeliveryPersonId(Long deliveryPersonId) {
        return deliveryAssignmentRepository.findByDeliveryPersonIdOrderByAssignedAtDesc(deliveryPersonId);
    }

    // Get assignments by status
    public List<DeliveryAssignment> getAssignmentsByStatus(AssignmentStatus status) {
        return deliveryAssignmentRepository.findByStatus(status);
    }

    // Get active assignments for a delivery person
    public List<DeliveryAssignment> getActiveAssignmentsByDeliveryPersonId(Long deliveryPersonId) {
        return deliveryAssignmentRepository.findActiveAssignmentsByDeliveryPersonId(deliveryPersonId);
    }

    // Update assignment status to PICKED_UP
    public DeliveryAssignment markAsPickedUp(Long assignmentId, String notes) {
        Optional<DeliveryAssignment> assignmentOpt = deliveryAssignmentRepository.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }

        DeliveryAssignment assignment = assignmentOpt.get();
        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new IllegalStateException("Assignment must be in ASSIGNED status to mark as picked up");
        }

        assignment.setStatus(AssignmentStatus.PICKED_UP);
        assignment.setPickedUpAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        if (notes != null && !notes.trim().isEmpty()) {
            assignment.setNotes(assignment.getNotes() + "\n[PICKUP] " + notes);
        }

        return deliveryAssignmentRepository.save(assignment);
    }

    // Update assignment status to DELIVERED
    public DeliveryAssignment markAsDelivered(Long assignmentId, String notes) {
        Optional<DeliveryAssignment> assignmentOpt = deliveryAssignmentRepository.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }

        DeliveryAssignment assignment = assignmentOpt.get();
        if (assignment.getStatus() != AssignmentStatus.PICKED_UP) {
            throw new IllegalStateException("Assignment must be in PICKED_UP status to mark as delivered");
        }

        assignment.setStatus(AssignmentStatus.DELIVERED);
        assignment.setDeliveredAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        if (notes != null && !notes.trim().isEmpty()) {
            assignment.setNotes(assignment.getNotes() + "\n[DELIVERY] " + notes);
        }

        return deliveryAssignmentRepository.save(assignment);
    }

    // Update assignment status to CANCELLED
    public DeliveryAssignment cancelAssignment(Long assignmentId, String reason) {
        Optional<DeliveryAssignment> assignmentOpt = deliveryAssignmentRepository.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }

        DeliveryAssignment assignment = assignmentOpt.get();
        if (assignment.getStatus() == AssignmentStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered assignment");
        }

        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignment.setUpdatedAt(LocalDateTime.now());
        if (reason != null && !reason.trim().isEmpty()) {
            assignment.setNotes(assignment.getNotes() + "\n[CANCELLED] " + reason);
        }

        return deliveryAssignmentRepository.save(assignment);
    }

    // Reassign to different delivery person
    public DeliveryAssignment reassignDeliveryPerson(Long assignmentId, Long newDeliveryPersonId, String reason) {
        Optional<DeliveryAssignment> assignmentOpt = deliveryAssignmentRepository.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }

        DeliveryAssignment assignment = assignmentOpt.get();
        if (assignment.getStatus() == AssignmentStatus.DELIVERED) {
            throw new IllegalStateException("Cannot reassign a delivered assignment");
        }

        // Validate new delivery person
        Optional<DeliveryPerson> newDeliveryPersonOpt = deliverPersonRepository.findById(newDeliveryPersonId);
        if (newDeliveryPersonOpt.isEmpty()) {
            throw new IllegalArgumentException("New delivery person not found with ID: " + newDeliveryPersonId);
        }

        DeliveryPerson newDeliveryPerson = newDeliveryPersonOpt.get();
        if (!newDeliveryPerson.getIsActive() || newDeliveryPerson.getIsDeleted()) {
            throw new IllegalStateException("New delivery person is not active or has been deleted");
        }

        assignment.setDeliveryPerson(newDeliveryPerson);
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setPickedUpAt(null); // Reset pickup time
        assignment.setUpdatedAt(LocalDateTime.now());
        if (reason != null && !reason.trim().isEmpty()) {
            assignment.setNotes(assignment.getNotes() + "\n[REASSIGNED] " + reason);
        }

        return deliveryAssignmentRepository.save(assignment);
    }

    // Update assignment notes
    public DeliveryAssignment updateNotes(Long assignmentId, String notes) {
        Optional<DeliveryAssignment> assignmentOpt = deliveryAssignmentRepository.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }

        DeliveryAssignment assignment = assignmentOpt.get();
        assignment.setNotes(notes);
        assignment.setUpdatedAt(LocalDateTime.now());

        return deliveryAssignmentRepository.save(assignment);
    }

    // Get overdue assignments (assigned more than specified hours ago)
    public List<DeliveryAssignment> getOverdueAssignments(int hoursAgo) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hoursAgo);
        return deliveryAssignmentRepository.findOverdueAssignments(cutoffTime);
    }

    // Get assignments within date range
    public List<DeliveryAssignment> getAssignmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return deliveryAssignmentRepository.findByAssignedAtBetween(startDate, endDate);
    }

    // Get delivered assignments within date range
    public List<DeliveryAssignment> getDeliveredAssignmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return deliveryAssignmentRepository.findByDeliveredAtBetween(startDate, endDate);
    }

    // Search assignments by notes
    public List<DeliveryAssignment> searchAssignmentsByNotes(String searchTerm) {
        return deliveryAssignmentRepository.findByNotesContaining(searchTerm);
    }

    // Get assignment statistics by status
    public long getAssignmentCountByStatus(AssignmentStatus status) {
        return deliveryAssignmentRepository.countByStatus(status);
    }

    // Get assignment count for delivery person
    public long getAssignmentCountByDeliveryPersonId(Long deliveryPersonId) {
        return deliveryAssignmentRepository.countByDeliveryPersonId(deliveryPersonId);
    }

    // Get delivery performance statistics for a delivery person
    public Object[] getDeliveryStats(Long deliveryPersonId) {
        return deliveryAssignmentRepository.getDeliveryStats(deliveryPersonId);
    }

    // Delete assignment (soft delete by setting status to CANCELLED)
    public void deleteAssignment(Long assignmentId) {
        cancelAssignment(assignmentId, "Assignment deleted by administrator");
    }

    // Check if order has assignment
    public boolean hasAssignment(Long orderId) {
        return deliveryAssignmentRepository.existsByOrderPaymentId(orderId);
    }

    // Get available delivery persons (active and not deleted)
    public List<DeliveryPerson> getAvailableDeliveryPersons() {
        return deliverPersonRepository.findByIsActiveTrueAndIsDeletedFalse();
    }
}