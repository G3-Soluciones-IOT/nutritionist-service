package pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources;

import java.time.Instant;

public record CreateAppointmentResource(
        Long nutritionistPatientId,
        Instant scheduledAt,
        Integer durationMinutes,
        String reason,
        String notes,
        String meetingUrl
) {
}
