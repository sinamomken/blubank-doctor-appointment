package com.blubank.doctorappointment.model.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BluException extends RuntimeException{
    public BluException(String errorCode){
        this.errorCode = errorCode;
    }

    private String errorCode;
    private Throwable cause;
}
