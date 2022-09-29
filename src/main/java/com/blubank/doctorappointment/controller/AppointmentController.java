package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.model.dto.AppointmentsAddRequestDto;
import com.blubank.doctorappointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/add")
    public ResponseEntity<?> addAppointments(@RequestBody @Valid AppointmentsAddRequestDto requestDto){
        var response = appointmentService.addStartAndEnd(requestDto);
        return ResponseEntity.ok(response);
    }
}
