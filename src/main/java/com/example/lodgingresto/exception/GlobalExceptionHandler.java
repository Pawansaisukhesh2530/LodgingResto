package com.example.lodgingresto.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/dashboard";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, RedirectAttributes ra) {
        String message = ex.getMostSpecificCause().getMessage();
        ra.addFlashAttribute("errorMessage", "Unable to complete the operation because related records already exist. " + (message == null ? "" : message));
        return "redirect:/dashboard";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/500";
    }
}
