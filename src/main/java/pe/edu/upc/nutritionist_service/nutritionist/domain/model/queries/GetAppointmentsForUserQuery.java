package pe.edu.upc.nutritionist_service.nutritionist.domain.model.queries;

import pe.edu.upc.nutritionist_service.nutritionist.domain.model.valueobjects.AppointmentStatus;

import java.time.Instant;

public record GetAppointmentsForUserQuery(
        Long userId,
        AppointmentStatus status,
        Instant from,
        Instant to
) {
}
