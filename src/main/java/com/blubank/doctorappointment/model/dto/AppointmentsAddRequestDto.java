package com.blubank.doctorappointment.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
public class AppointmentsAddRequestDto {
    @JsonProperty
    private LocalDate date;

    @JsonProperty
    private LocalTime startTime;

    @JsonProperty
    private LocalTime endTime;
}
