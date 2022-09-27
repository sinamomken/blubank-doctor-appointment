package com.blubank.doctorappointment.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "tbl_appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_tbl_appointment")
    @SequenceGenerator(name = "seq_tbl_appointment", sequenceName = "seq_tbl_appointment", allocationSize = 1)
    private Long id;

    // ############## //
    // ### FIELDS ### //
    // ############## //
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_taken", nullable = false)
    private Boolean isTaken = false;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "patient_phone")
    private String patientPhone;
}
