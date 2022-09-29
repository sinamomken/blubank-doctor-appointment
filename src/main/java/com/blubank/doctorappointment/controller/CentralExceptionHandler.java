package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.model.dto.ErrorDto;
import com.blubank.doctorappointment.model.exception.BluException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class CentralExceptionHandler {
    private final static String defaultErrorMessage = "Unknown error occurred.";
    private final static String defaultErrorStatus = "500";
    private final MessageSource messageSource;

    @ExceptionHandler(BluException.class)
    public ResponseEntity<?> handleError(BluException bluException){
        log.error("Greeting is {}", messageSource.getMessage("greeting", null, Locale.ENGLISH));
        ErrorDto errorDto = new ErrorDto();
        errorDto.setErrorCode(bluException.getErrorCode());
        errorDto.setErrorDesc(messageSource.getMessage("error.code."+bluException.getErrorCode()+".message",null, defaultErrorMessage, Locale.ENGLISH));
        log.error("Blu exception "+errorDto.getErrorCode()+" occurred: "+errorDto.getErrorDesc());
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(messageSource.getMessage("error.code."+bluException.getErrorCode()+".status",null, defaultErrorStatus, Locale.ENGLISH)));
        return new ResponseEntity<>(errorDto, httpStatus);
    }

}
