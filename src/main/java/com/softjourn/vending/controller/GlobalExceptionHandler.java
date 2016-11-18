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
public class GlobalExceptionHandler {

    @ExceptionHandler(NotEnoughAmountException.class)
    public ResponseEntity<ErrorDetail> handlePaymentNotEnoughAmount(NotEnoughAmountException e) {
        log.info(e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorDetails(e, 40901,
                "Not enough money to buy product"));
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorDetail> handlePaymentProcessingException(PaymentProcessingException e) {
        log.warn("Error during processing payment. " + e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorDetails(e, 50902,
                "Error during processing payment."));
    }

    @ExceptionHandler(NotFoundException.class)
    public void handleNotFoundException(Exception e) {
        log.warn(e.getLocalizedMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetail> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn(e.getLocalizedMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildErrorDetails(e, 40401, String.format("Endpoint %s not found", e.getRequestURL())));
    }

    @ExceptionHandler(MachineNotFoundException.class)
    public ResponseEntity<ErrorDetail> handleMachineNotFoundException(MachineNotFoundException e) {
        log.warn(e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorDetails(e, 40402,
                e.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDetail> handleProductNotFoundException(ProductNotFoundException e) {
        log.warn(e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorDetails(e, 40403,
                e.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundInMachineException.class)
    public ResponseEntity<ErrorDetail> handleProductNotFoundInMachineException(ProductNotFoundInMachineException e) {
        log.warn(e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorDetails(e, 40404,
                e.getMessage()));
    }

    @ExceptionHandler(ProductAlreadyInFavoritesException.class)
    public ResponseEntity<ErrorDetail> handleProductAlreadyInFavoritesException(ProductAlreadyInFavoritesException e) {
        log.warn(e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorDetails(e, 40902,
                e.getMessage()));
    }

    @ExceptionHandler(ProductIsNotInFavoritesException.class)
    public ResponseEntity<ErrorDetail> handleProductIsNotInFavoritesException(ProductIsNotInFavoritesException e) {
        log.warn(e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorDetails(e, 40903,
                e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetail> handleBadRequestException(Exception e) {
        log.warn(e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorDetails(e, 40000,
                e.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Such item already presented.")
    @ExceptionHandler(AlreadyPresentedException.class)
    public void handleAlreadyPresentedException(Exception e) {
        log.info(e.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error occurred during processing request. Contact ADMIN.")
    @ExceptionHandler(VendingProcessingException.class)
    public void handleVendingProcessingException(Exception e) {
        log.info(e.getLocalizedMessage());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ModelAndView accessDenied() {
        return new ModelAndView("redirect:login.html");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetail> handleDataIntegrityViolationException(Exception e) {
        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
            if (cause.getSQLException().getErrorCode() == SQL_DUPLICATE_ENTRY) {
                log.info(e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorDetails(cause,
                        cause.getSQLException().getErrorCode(), "Duplicate entry"));
            } else if (cause.getSQLException().getErrorCode() == SQL_CANNOT_DELETE_OR_UPDATE_PARENT_ROW) {
                log.info(e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorDetails(cause,
                        cause.getSQLException().getErrorCode(), "Cannot delete or update a parent row"));
            }
        }
        log.info(e.getLocalizedMessage());
        return null;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetail> handle(MethodArgumentNotValidException e) {
        log.info(e.getMessage());
        String message = e.getBindingResult().getAllErrors().stream()
                .findFirst()
                .filter(Objects::nonNull)
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorDetails(e, null, message));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDetail> handleFileUploadBase$SizeLimitExceededException(MaxUploadSizeExceededException e) {
        log.info(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorDetails(e, null, "Image size is too large"));
    }

    @ExceptionHandler(NotImageException.class)
    public ResponseEntity<ErrorDetail> handleNotImageException(NotImageException e) {
        log.info(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorDetails(e, null, "This file is not image or this file format is not supported!"));
    }

    @ExceptionHandler(WrongImageDimensions.class)
    public ResponseEntity<ErrorDetail> handleWrongImageDimensions(WrongImageDimensions e) {
        log.info(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorDetails(e, null, "Image dimensions is too big, try to use 205*205px"));
    }

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<ErrorDetail> handleParseException(ParseException e) {
        log.info(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorDetails(e, null, "Sent data could not be parsed"));
    }

    @ExceptionHandler(ErisAccountNotFoundException.class)
    public ResponseEntity<ErrorDetail> handleErisAccountNotFound(Exception e) {
        log.info("Request for create machine and assign Eris account. " + e.getLocalizedMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildErrorDetails(e, null, e.getLocalizedMessage()));
    }

    @ExceptionHandler(MachineBusyException.class)
    public ResponseEntity<ErrorDetail> handleMachineBusyException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
                .body(buildErrorDetails(e, 50901, "Machine is locked by queue. Try again later."));
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
