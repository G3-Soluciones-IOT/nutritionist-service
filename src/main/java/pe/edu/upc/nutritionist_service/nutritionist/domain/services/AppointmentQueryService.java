package pe.edu.upc.nutritionist_service.nutritionist.domain.services;

import pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates.Appointment;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.queries.GetAppointmentsForUserQuery;

import java.util.List;
import java.util.Optional;

public interface AppointmentQueryService {
    Optional<Appointment> findById(Long appointmentId);
    Appointment findAccessibleById(Long appointmentId, Long currentUserId);
    List<Appointment> findForUser(GetAppointmentsForUserQuery query);
}
