package com.example.lodgingresto.controller;

import com.example.lodgingresto.dto.ApiResponse;
import com.example.lodgingresto.model.Employee;
import com.example.lodgingresto.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class ApiEmployeeController {

    private final EmployeeService employeeService;

    public ApiEmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> listAll() {
        List<Employee> data = employeeService.getAllEmployees();
        return ResponseEntity.ok(new ApiResponse<>("success", "Employees fetched", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getById(@PathVariable Long id) {
        Employee data = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Employee not found with id " + id));
        return ResponseEntity.ok(new ApiResponse<>("success", "Employee found", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> create(@RequestBody Employee employee) {
        Employee saved = employeeService.createEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Employee created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> update(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updated = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(new ApiResponse<>("success", "Employee updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Employee deleted"));
    }
}
