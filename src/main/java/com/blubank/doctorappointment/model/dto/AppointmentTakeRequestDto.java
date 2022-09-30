package com.blubank.doctorappointment.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Getter
@Accessors(chain = true)
public class AppointmentTakeRequestDto {
    @JsonProperty
    @NotNull(message = "101004")
    private Long id;

    @JsonProperty
    @NotEmpty(message = "101005")
    private String patientName;

    @JsonProperty
    @Pattern(regexp = "\\d{11}", message = "101006")
    private String patientPhone;
}
