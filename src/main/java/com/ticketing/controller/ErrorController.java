package com.ticketing.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = "Une erreur s'est produite";
        String errorCode = "Erreur";
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "La page demandée n'existe pas";
                errorCode = "404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessage = "Une erreur interne s'est produite";
                errorCode = "500";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "Accès refusé";
                errorCode = "403";
            }
        }
        
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorCode", errorCode);
        
        return "error/error";
    }
}
