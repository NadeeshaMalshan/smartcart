package com.group35.smartcart.service;

import com.group35.smartcart.entity.Employee;
import com.group35.smartcart.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    public Optional<Employee> authenticateEmployee(String empid, String password) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmpidAndIsActiveTrue(empid);
        
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            // Check password (in real app, use BCryptPasswordEncoder)
            if (password.equals(employee.getPassword())) {
                return Optional.of(employee);
            }
        }
        
        return Optional.empty();
    }
    
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public Optional<Employee> getEmployeeByEmpid(String empid) {
        return employeeRepository.findByEmpidAndIsActiveTrue(empid);
    }
    
    public boolean employeeExists(String empid) {
        return employeeRepository.existsByEmpid(empid);
    }
}




