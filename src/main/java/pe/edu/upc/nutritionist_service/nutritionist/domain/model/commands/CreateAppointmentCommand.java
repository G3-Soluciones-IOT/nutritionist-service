package pe.edu.upc.nutritionist_service.nutritionist.domain.model.commands;

import java.time.Instant;

public record CreateAppointmentCommand(
        Long nutritionistPatientId,
        Instant scheduledAt,
        Integer durationMinutes,
        String reason,
        String notes,
        String meetingUrl
) {
}
