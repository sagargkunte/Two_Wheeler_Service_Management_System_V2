package com.twowheeler.repository;

import com.twowheeler.model.Appointment;
import com.twowheeler.model.Bike;
import com.twowheeler.model.Mechanic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByBikeIdInOrderByCreatedAtDesc(List<Bike> bikes);
    List<Appointment> findByMechanicIdOrderByCreatedAtDesc(Mechanic mechanic);
    List<Appointment> findByMechanicId_IdOrderByCreatedAtDesc(Long mechanicId);
}
