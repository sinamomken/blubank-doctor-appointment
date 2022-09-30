package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    void deleteAllByDate(LocalDate date);
    List<Appointment> findAllByDateAndIsTaken(LocalDate date, Boolean isTaken);
    List<Appointment> findAllByPatientPhone(String patientPhone);
}
