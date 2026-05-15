package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Employee;
import com.example.lodgingresto.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee createEmployee(Employee e) {
        return employeeRepository.save(e);
    }

    @Override
    public Employee updateEmployee(Long id, Employee e) {
        Employee existing = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        existing.setName(e.getName());
        existing.setDepartment(e.getDepartment());
        existing.setPosition(e.getPosition());
        existing.setPhoneNumber(e.getPhoneNumber());
        existing.setSalary(e.getSalary());
        return employeeRepository.save(existing);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
}

