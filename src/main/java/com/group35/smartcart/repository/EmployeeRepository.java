package com.group35.smartcart.repository;

import com.group35.smartcart.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    
    Optional<Employee> findByEmpidAndIsActiveTrue(String empid);
    
    boolean existsByEmpid(String empid);
}




