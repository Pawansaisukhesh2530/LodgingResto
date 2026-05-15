package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee createEmployee(Employee e);
    Employee updateEmployee(Long id, Employee e);
    void deleteEmployee(Long id);
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(Long id);
}

