package com.softjourn.vending.controller;


import com.softjourn.vending.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Not enough amount of coins in account to by this item.")
    @ExceptionHandler(NotEnoughAmountException.class)
    public void handlePaymentNotEnoughAmount(Exception e) {
        log.info(e.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error during processing payment.")
    @ExceptionHandler(PaymentProcessingException.class)
    public void handlePaymentProcessingException(Exception e) {
        log.warn("Error during processing payment. " + e.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public void handleNotFoundException(Exception e) {
        log.warn(e.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad request.")
    @ExceptionHandler(BadRequestException.class)
    public void handleBadRequestException(Exception e) {
        log.warn(e.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Such item already presented.")
    @ExceptionHandler(AlreadyPresentedException.class)
    public void handleAlreadyPresentedException(Exception e) {
        log.info(e.getLocalizedMessage());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ModelAndView accessDenied() {
        return new ModelAndView("redirect:login.html");
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Can't delete or update item becouse it's used somewhere else.")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleDataIntegrityViolationException(Exception e) {
        log.info(e.getLocalizedMessage());
    }
}
