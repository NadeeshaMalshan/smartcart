package com.group35.smartcart.repository;

import com.group35.smartcart.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliverPersonRepository extends JpaRepository<DeliveryPerson, Long> {

    // Find delivery person by email
    Optional<DeliveryPerson> findByEmail(String email);

    // Find delivery person by vehicle number
    Optional<DeliveryPerson> findByVehicleNumber(String vehicleNumber);

    // Find all active delivery persons
    List<DeliveryPerson> findByIsActiveTrue();

    // Find all non-deleted delivery persons
    List<DeliveryPerson> findByIsDeletedFalse();

    // Find all active and non-deleted delivery persons
    List<DeliveryPerson> findByIsActiveTrueAndIsDeletedFalse();

    // Find delivery persons by vehicle type
    List<DeliveryPerson> findByVehicleTypeAndIsDeletedFalse(DeliveryPerson.VehicleType vehicleType);

    // Custom query to search delivery persons by name
    @Query("SELECT d FROM DeliveryPerson d WHERE d.name LIKE %:searchTerm% AND d.isDeleted = false")
    List<DeliveryPerson> searchByName(String searchTerm);
}
