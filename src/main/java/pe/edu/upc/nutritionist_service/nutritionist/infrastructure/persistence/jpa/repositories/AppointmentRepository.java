package pe.edu.upc.nutritionist_service.nutritionist.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates.Appointment;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientUserIdOrderByScheduledAtAsc(Long patientUserId);
    List<Appointment> findByNutritionistUserIdOrderByScheduledAtAsc(Long nutritionistUserId);
}
