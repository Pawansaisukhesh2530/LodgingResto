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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String list(@RequestParam(name = "search", required = false) String search, Model model) {
        model.addAttribute("employees", employeeService.searchEmployees(search));
        model.addAttribute("search", search);
        model.addAttribute("employee", new Employee());
        return "employees";
    }

    @GetMapping("/new")
    public String newEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        return employeeService.getEmployeeById(id)
                .map(employee -> {
                    model.addAttribute("employee", employee);
                    model.addAttribute("employees", employeeService.getAllEmployees());
                    return "edit-employee";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "Employee not found.");
                    return "redirect:/employees";
                });
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("employee") Employee employee, BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "employees";
        }
        employeeService.createEmployee(employee);
        ra.addFlashAttribute("successMessage", "Employee added");
        return "redirect:/employees";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("employee") Employee employee,
                         BindingResult br,
                         Model model,
                         RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "edit-employee";
        }
        employeeService.updateEmployee(id, employee);
        ra.addFlashAttribute("successMessage", "Employee updated");
        return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        employeeService.deleteEmployee(id);
        ra.addFlashAttribute("successMessage", "Employee removed");
        return "redirect:/employees";
    }
}

