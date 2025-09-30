package com.group35.smartcart.repository;

import com.group35.smartcart.entity.AssignmentStatus;
import com.group35.smartcart.entity.DeliveryAssignment;
import com.group35.smartcart.entity.DeliveryPerson;
import com.group35.smartcart.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {
    
    // Find assignments by delivery person
    List<DeliveryAssignment> findByDeliveryPerson(DeliveryPerson deliveryPerson);
    
    // Find assignments by delivery person ID
    List<DeliveryAssignment> findByDeliveryPersonId(Long deliveryPersonId);
    
    // Find assignment by order
    Optional<DeliveryAssignment> findByOrder(Order order);
    
    // Find assignment by order ID
    Optional<DeliveryAssignment> findByOrderPaymentId(Long orderId);
    
    // Find assignments by status
    List<DeliveryAssignment> findByStatus(AssignmentStatus status);
    
    // Find assignments by delivery person and status
    List<DeliveryAssignment> findByDeliveryPersonAndStatus(DeliveryPerson deliveryPerson, AssignmentStatus status);
    
    // Find assignments by delivery person ID and status
    List<DeliveryAssignment> findByDeliveryPersonIdAndStatus(Long deliveryPersonId, AssignmentStatus status);
    
    // Find active assignments for a delivery person (ASSIGNED or PICKED_UP)
    @Query("SELECT da FROM DeliveryAssignment da WHERE da.deliveryPerson.id = :deliveryPersonId AND da.status IN (com.group35.smartcart.entity.AssignmentStatus.ASSIGNED, com.group35.smartcart.entity.AssignmentStatus.PICKED_UP)")
    List<DeliveryAssignment> findActiveAssignmentsByDeliveryPersonId(@Param("deliveryPersonId") Long deliveryPersonId);
    
    // Find assignments created within a date range
    List<DeliveryAssignment> findByAssignedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find assignments delivered within a date range
    List<DeliveryAssignment> findByDeliveredAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Count assignments by status
    long countByStatus(AssignmentStatus status);
    
    // Count assignments by delivery person
    long countByDeliveryPerson(DeliveryPerson deliveryPerson);
    
    // Count assignments by delivery person ID
    long countByDeliveryPersonId(Long deliveryPersonId);
    
    // Find overdue assignments (assigned more than specified hours ago and not delivered)
    @Query("SELECT da FROM DeliveryAssignment da WHERE da.assignedAt < :cutoffTime AND da.status IN (com.group35.smartcart.entity.AssignmentStatus.ASSIGNED, com.group35.smartcart.entity.AssignmentStatus.PICKED_UP)")
    List<DeliveryAssignment> findOverdueAssignments(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // Find assignments ordered by assignment date (newest first)
    List<DeliveryAssignment> findAllByOrderByAssignedAtDesc();
    
    // Find assignments by delivery person ordered by assignment date (newest first)
    List<DeliveryAssignment> findByDeliveryPersonOrderByAssignedAtDesc(DeliveryPerson deliveryPerson);
    
    // Find assignments by delivery person ID ordered by assignment date (newest first)
    List<DeliveryAssignment> findByDeliveryPersonIdOrderByAssignedAtDesc(Long deliveryPersonId);
    
    // Check if order has an assignment
    boolean existsByOrder(Order order);
    
    // Check if order ID has an assignment
    boolean existsByOrderPaymentId(Long orderId);
    
    // Find assignments with notes containing specific text
    @Query("SELECT da FROM DeliveryAssignment da WHERE da.notes LIKE %:searchTerm%")
    List<DeliveryAssignment> findByNotesContaining(@Param("searchTerm") String searchTerm);
    
    // Get delivery performance statistics for a delivery person
    @Query(value = "SELECT COUNT(da.id), AVG(TIMESTAMPDIFF(HOUR, da.assigned_at, da.delivered_at)) FROM delivery_assignments da WHERE da.delivery_person_id = :deliveryPersonId AND da.status = 'DELIVERED'", nativeQuery = true)
    Object[] getDeliveryStats(@Param("deliveryPersonId") Long deliveryPersonId);
}