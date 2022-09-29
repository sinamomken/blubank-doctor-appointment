package com.blubank.doctorappointment.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorDto {
    @JsonProperty
    String errorCode;

    @JsonProperty
    String errorDesc;
}
