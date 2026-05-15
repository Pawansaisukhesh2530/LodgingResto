package com.example.lodgingresto.repository;

import com.example.lodgingresto.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

