package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.Employee;
import com.example.lodgingresto.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employees";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Employee employee, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "employees";
        employeeService.createEmployee(employee);
        ra.addFlashAttribute("successMessage", "Employee added");
        return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        employeeService.deleteEmployee(id);
        ra.addFlashAttribute("successMessage", "Employee removed");
        return "redirect:/employees";
    }
}

