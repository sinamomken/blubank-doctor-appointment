package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.model.dto.AppointmentsAddRequestDto;
import com.blubank.doctorappointment.model.entity.Appointment;
import com.blubank.doctorappointment.model.exception.BluException;
import com.blubank.doctorappointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AppointmentService {
    private static final long DURATION = 30;

    private final Object deleteOrUpdateLock = new Object();

    private final AppointmentRepository appointmentRepository;

    public List<Appointment> addStartAndEnd(AppointmentsAddRequestDto requestDto){
        log.debug("Start of addStartAndEnd() ...");
        if(requestDto.getEndTime().isBefore(requestDto.getStartTime())){
            throw new BluException("101001");
        }

        log.info("Deleting all existing appointments by date {}", requestDto.getDate());
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
        log.info("Saving {} appointments into database.", appointmentList.size());
        appointmentRepository.saveAll(appointmentList);
        log.debug("End of addStartAndEnd().");

        return appointmentList;
    }

    public List<Appointment> getAll(){
        log.debug("Start of getAll() ...");
        List<Appointment> result = appointmentRepository.findAll();
        log.info("Found {} total appointments", result.size());
        log.debug("End of getAll().");
        return result;
    }

    public void resetAll(){
        log.debug("Start of resetAll() ...");
        appointmentRepository.deleteAll();
        log.debug("End of resetAll().");
    }

    public void delete(Long id){
        log.debug("Start of delete() ...");
        if(!appointmentRepository.existsById(id)){
            throw new BluException("101002");
        }
        //noinspection OptionalGetWithoutIsPresent
        Appointment appointment = appointmentRepository.findById(id).get();
        if(appointment.getIsTaken()) {
            throw new BluException("101003");
        }

        synchronized (deleteOrUpdateLock){
            log.info("Deleting appointment with id="+id);
            appointmentRepository.deleteById(id);
        }
        log.debug("End of delete().");
    }

    public List<Appointment> getOpens(LocalDate date){
        log.debug("Start of getOpens() ...");
        var result = appointmentRepository.findAllByDateAndIsTaken(date, false);
        log.info("Found {} open appointments for day {}", result.size(), date);
        log.debug("End of getOpens().");
        return result;
    }
}
