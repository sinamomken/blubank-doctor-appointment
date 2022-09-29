package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.model.dto.AppointmentsAddRequestDto;
import com.blubank.doctorappointment.model.entity.Appointment;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AppointmentService {
    private static final long DURATION = 30;

    private final AppointmentRepository appointmentRepository;

    public List<Appointment> addStartAndEnd(AppointmentsAddRequestDto requestDto){
        log.debug("Start of addStartAndEnd() ...");
        if(requestDto.getEndTime().isBefore(requestDto.getStartTime())){
            //log
            throw new IllegalArgumentException("End time must not be before start time.");
        }

        //log.
        appointmentRepository.deleteAllByDate(requestDto.getDate());

        List<Appointment> appointmentList = new ArrayList<>();
        LocalTime appointmentStart = requestDto.getStartTime();
        LocalTime appointmentEnd = appointmentStart.plusMinutes(DURATION);
        while(appointmentEnd.isBefore(requestDto.getEndTime()) || appointmentEnd.equals(requestDto.getEndTime())){
            Appointment appointment = new Appointment()
                    .setDate(requestDto.getDate())
                    .setEndTime(appointmentEnd)
                    .setIsTaken(false)
                    .setPatientName(null)
                    .setPatientPhone(null)
                    .setStartTime(appointmentStart);
            appointmentList.add(appointment);

            appointmentStart = appointmentEnd;
            appointmentEnd = appointmentStart.plusMinutes(DURATION);
        }
        appointmentRepository.saveAll(appointmentList);
        //log.

        return appointmentList;
    }
}
