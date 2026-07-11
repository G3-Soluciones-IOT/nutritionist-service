package pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.transform;

import pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates.Appointment;
import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources.AppointmentResource;

public class AppointmentResourceFromEntityAssembler {

    public static AppointmentResource toResourceFromEntity(Appointment appointment) {
        return new AppointmentResource(
                appointment.getId(),
                appointment.getNutritionistPatientId(),
                appointment.getNutritionistId(),
                appointment.getNutritionistUserId(),
                appointment.getPatientUserId(),
                appointment.getScheduledAt(),
                appointment.getDurationMinutes(),
                appointment.getStatus(),
                appointment.getReason(),
                appointment.getNotes(),
                appointment.getMeetingUrl(),
                appointment.getRequestedAt(),
                appointment.getConfirmedAt(),
                appointment.getCancelledAt()
        );
    }
}
