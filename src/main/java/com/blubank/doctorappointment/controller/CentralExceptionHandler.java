package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.model.dto.ErrorDto;
import com.blubank.doctorappointment.model.exception.BluException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class CentralExceptionHandler {
    private final static String defaultErrorMessage = "Unknown error occurred.";
    private final static String defaultErrorStatus = "500";
    private final static String defaultValidationErrorStatus = "400";
    private final MessageSource messageSource;

    @ExceptionHandler(BluException.class)
    public ResponseEntity<?> handleError(BluException bluException){
        ErrorDto errorDto = new ErrorDto();
        errorDto.setErrorCode(bluException.getErrorCode());
        errorDto.setErrorDesc(messageSource.getMessage("error.code."+bluException.getErrorCode()+".message",null, defaultErrorMessage, Locale.ENGLISH));
        log.error("Blu exception "+errorDto.getErrorCode()+": "+errorDto.getErrorDesc());
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(messageSource.getMessage("error.code."+bluException.getErrorCode()+".status",null, defaultErrorStatus, Locale.ENGLISH)));
        return new ResponseEntity<>(errorDto, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleError(MethodArgumentNotValidException exception){
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String errorCode = fieldErrors.get(0).getDefaultMessage();
        ErrorDto errorDto = new ErrorDto();
        errorDto.setErrorCode(errorCode);
        errorDto.setErrorDesc(messageSource.getMessage("error.code."+errorCode+".message",null, defaultErrorMessage, Locale.ENGLISH));
        log.error("Validation exception "+errorDto.getErrorCode()+": "+errorDto.getErrorDesc());
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(messageSource.getMessage("error.code."+errorCode+".status",null, defaultValidationErrorStatus, Locale.ENGLISH)));
        return new ResponseEntity<>(errorDto, httpStatus);
    }
}
