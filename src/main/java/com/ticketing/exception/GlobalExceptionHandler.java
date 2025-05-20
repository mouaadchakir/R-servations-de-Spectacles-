package com.ticketing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        logger.severe("Resource not found exception: " + ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "404");
        return "error/error";
    }

    @ExceptionHandler(SeatAlreadyReservedException.class)
    public String handleSeatAlreadyReservedException(SeatAlreadyReservedException ex, RedirectAttributes redirectAttributes) {
        logger.warning("Seat already reserved: " + ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/shows";
    }

    @ExceptionHandler(PaymentException.class)
    public String handlePaymentException(PaymentException ex, RedirectAttributes redirectAttributes) {
        logger.severe("Payment exception: " + ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "Erreur de paiement: " + ex.getMessage());
        return "redirect:/payment/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalException(Exception ex, Model model) {
        logger.severe("Global exception: " + ex.getMessage());
        model.addAttribute("errorMessage", "Une erreur inattendue s'est produite: " + ex.getMessage());
        model.addAttribute("errorCode", "500");
        return "error/error";
    }
}
