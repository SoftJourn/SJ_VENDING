package com.softjourn.vending.controller;


import com.softjourn.vending.dto.ErrorDetail;
import com.softjourn.vending.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.text.ParseException;
import java.util.Objects;

import static com.softjourn.vending.utils.Constants.SQL_CANNOT_DELETE_OR_UPDATE_PARENT_ROW;
import static com.softjourn.vending.utils.Constants.SQL_DUPLICATE_ENTRY;

@ControllerAdvice
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {

    // 400 BAD_REQUEST

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorDetail handleBadRequestException(Exception e) {
        log.warn(e.getLocalizedMessage());
        return buildErrorDetails(e, 40000,
            e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDetail handle(MethodArgumentNotValidException e) {
        log.info(e.getMessage());
        String message = e.getBindingResult().getAllErrors().stream()
            .findFirst()
            .filter(Objects::nonNull)
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .orElse("");

        return buildErrorDetails(e, null, message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ErrorDetail handleFileUploadBase$SizeLimitExceededException(MaxUploadSizeExceededException e) {
        log.info(e.getMessage());
        return buildErrorDetails(e, null, "Image size is too large");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotImageException.class)
    public ErrorDetail handleNotImageException(NotImageException e) {
        log.info(e.getMessage());
        return buildErrorDetails(e, null, "This file is not image or this file format is not supported!");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WrongImageDimensions.class)
    public ErrorDetail handleWrongImageDimensions(WrongImageDimensions e) {
        log.info(e.getMessage());
        return buildErrorDetails(e, null, "Image dimensions is too big, try to use 205*205px");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParseException.class)
    public ErrorDetail handleParseException(ParseException e) {
        log.info(e.getMessage());
        return buildErrorDetails(e, null, "Sent data could not be parsed");
    }

    // 404 NOT_FOUND

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorDetail handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn(e.getLocalizedMessage());
        return buildErrorDetails(e, 40401, String.format("Endpoint %s not found", e.getRequestURL()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MachineNotFoundException.class)
    public ErrorDetail handleMachineNotFoundException(MachineNotFoundException e) {
        log.warn(e.getLocalizedMessage());
        return buildErrorDetails(e, 40402, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorDetail handleProductNotFoundException(ProductNotFoundException e) {
        log.warn(e.getLocalizedMessage());
        return buildErrorDetails(e, 40403, e.getMessage());
    }

    @ExceptionHandler(ProductNotFoundInMachineException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetail handleProductNotFoundInMachineException(ProductNotFoundInMachineException e) {
        log.warn(e.getLocalizedMessage());
        return buildErrorDetails(e, 40404,
            e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorDetail handleNotFoundException(Exception e) {
        log.warn(e.getLocalizedMessage());
        return buildErrorDetails(e, 40405, "Record does not exists");
    }

    // 409 CONFLICT

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorDetail handleDataIntegrityViolationException(Exception e) {
        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
            if (cause.getSQLException().getErrorCode() == SQL_DUPLICATE_ENTRY) {
                log.info(e.getMessage());
                return buildErrorDetails(cause,
                    cause.getSQLException().getErrorCode(), "Duplicate entry");
            } else if (cause.getSQLException().getErrorCode() == SQL_CANNOT_DELETE_OR_UPDATE_PARENT_ROW) {
                log.info(e.getMessage());
                return buildErrorDetails(cause,
                    cause.getSQLException().getErrorCode(), "Cannot delete or update a parent row");
            }
        }
        log.info(e.getLocalizedMessage());
        return null;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ErisAccountNotFoundException.class)
    public ErrorDetail handleErisAccountNotFound(Exception e) {
        log.info("Request for create machine and assign Eris account. " + e.getLocalizedMessage());
        return buildErrorDetails(e, null, e.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Such item already presented.")
    @ExceptionHandler(AlreadyPresentedException.class)
    public void handleAlreadyPresentedException(Exception e) {
        log.info(e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(NotEnoughAmountException.class)
    public ErrorDetail handlePaymentNotEnoughAmount(NotEnoughAmountException e) {
        log.info(e.getLocalizedMessage());
        return buildErrorDetails(e, 40901, "Not enough money to buy product");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProductAlreadyInFavoritesException.class)
    public ErrorDetail handleProductAlreadyInFavoritesException(ProductAlreadyInFavoritesException e) {
        log.warn(e.getLocalizedMessage());
        return buildErrorDetails(e, 40902,
            e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProductIsNotInFavoritesException.class)
    public ErrorDetail handleProductIsNotInFavoritesException(ProductIsNotInFavoritesException e) {
        log.warn(e.getLocalizedMessage());
        return buildErrorDetails(e, 40903,
            e.getMessage());
    }

    // 500 INTERNAL_SERVER_ERROR

    @ResponseStatus(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
    @ExceptionHandler(MachineBusyException.class)
    public ErrorDetail handleMachineBusyException(Exception e) {
        return buildErrorDetails(e, 50901, "Machine is locked by queue. Try again later.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PaymentProcessingException.class)
    public ErrorDetail handlePaymentProcessingException(PaymentProcessingException e) {
        log.warn("Error during processing payment. " + e.getLocalizedMessage());
        return buildErrorDetails(e, 50902, "Error during processing payment.");
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error occurred during processing request. Contact ADMIN.")
    @ExceptionHandler(VendingProcessingException.class)
    public void handleVendingProcessingException(Exception e) {
        log.info(e.getLocalizedMessage());
    }

    // NO STATUS

    @ExceptionHandler(value = AccessDeniedException.class)
    public ModelAndView accessDenied() {
        return new ModelAndView("redirect:login.html");
    }

    private ErrorDetail buildErrorDetails(Exception e, Integer code, String message) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTitle("Error");
        errorDetail.setDetail(message);
        errorDetail.setCode(code);
        errorDetail.setDeveloperMessage(e.getClass().getName());
        return errorDetail;
    }

}
