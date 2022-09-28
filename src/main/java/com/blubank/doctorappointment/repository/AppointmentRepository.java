package com.blubank.doctorappointment.repository;

import com.blubank.doctorappointment.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    void deleteAllByDate(LocalDate date);
}
