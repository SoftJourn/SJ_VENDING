package com.softjourn.vending.controller;


import com.softjourn.vending.dto.ErrorDetail;
import com.softjourn.vending.exceptions.AlreadyPresentedException;
import com.softjourn.vending.exceptions.BadRequestException;
import com.softjourn.vending.exceptions.NotEnoughAmountException;
import com.softjourn.vending.exceptions.NotFoundException;
import com.softjourn.vending.exceptions.PaymentProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
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

import static com.softjourn.vending.utils.Constants.SQL_CANNOT_DELETE_OR_UPDATE_PARENT_ROW;
import static com.softjourn.vending.utils.Constants.SQL_DUPLICATE_ENTRY;

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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorDetails(e,
                null, e.getBindingResult().getAllErrors().stream().findFirst().get().getDefaultMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDetail> handleFileUploadBase$SizeLimitExceededException(MaxUploadSizeExceededException e) {
        log.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorDetails(e,
                null, "Image size is too large"));
    }

    private ErrorDetail buildErrorDetails(RuntimeException e, Integer code, String message) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTitle("Error");
        errorDetail.setDetail(message);
        errorDetail.setCode(code);
        errorDetail.setDeveloperMessage(e.getClass().getName());
        return errorDetail;
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
