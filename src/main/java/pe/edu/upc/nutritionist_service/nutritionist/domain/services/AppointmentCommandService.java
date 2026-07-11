package pe.edu.upc.nutritionist_service.nutritionist.domain.services;

import pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates.Appointment;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.commands.CreateAppointmentCommand;

public interface AppointmentCommandService {
    Appointment handle(CreateAppointmentCommand command, Long currentUserId);
    Appointment confirm(Long appointmentId, Long currentUserId);
    Appointment reject(Long appointmentId, Long currentUserId);
    Appointment cancel(Long appointmentId, Long currentUserId);
    Appointment complete(Long appointmentId, Long currentUserId);
}
