package pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources;

import pe.edu.upc.nutritionist_service.nutritionist.domain.model.valueobjects.AppointmentStatus;

import java.time.Instant;

public record AppointmentResource(
        Long id,
        Long nutritionistPatientId,
        Integer nutritionistId,
        Long nutritionistUserId,
        Long patientUserId,
        Instant scheduledAt,
        Integer durationMinutes,
        AppointmentStatus status,
        String reason,
        String notes,
        String meetingUrl,
        Instant requestedAt,
        Instant confirmedAt,
        Instant cancelledAt
) {
}
