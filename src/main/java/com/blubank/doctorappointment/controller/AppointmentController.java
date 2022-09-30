package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.model.dto.AppointmentTakeRequestDto;
import com.blubank.doctorappointment.model.dto.AppointmentsAddRequestDto;
import com.blubank.doctorappointment.model.entity.Appointment;
import com.blubank.doctorappointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

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

    @GetMapping("/view-all")
    public ResponseEntity<?> viewAllAppointments(){
        var response = appointmentService.getAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reset")
    public ResponseEntity<?> resetAllAppointments(){
        appointmentService.resetAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAnAppointment(@PathVariable("id") @NotNull Long id){
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/view-opens")
    public ResponseEntity<?> viewOpenAppointments(@RequestParam("date")
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                              LocalDate date) {
        var response = appointmentService.getOpens(date);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/take")
    public ResponseEntity<?> takeAnAppointment(@RequestBody @Valid AppointmentTakeRequestDto requestDto){
        var response = appointmentService.take(requestDto);
        return ResponseEntity.ok(response);
    }
}
