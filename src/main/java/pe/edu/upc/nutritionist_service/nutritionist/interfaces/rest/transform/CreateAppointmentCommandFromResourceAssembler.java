package pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.transform;

import pe.edu.upc.nutritionist_service.nutritionist.domain.model.commands.CreateAppointmentCommand;
import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources.CreateAppointmentResource;

public class CreateAppointmentCommandFromResourceAssembler {

    public static CreateAppointmentCommand toCommandFromResource(CreateAppointmentResource resource) {
        return new CreateAppointmentCommand(
                resource.nutritionistPatientId(),
                resource.scheduledAt(),
                resource.durationMinutes(),
                resource.reason(),
                resource.notes(),
                resource.meetingUrl()
        );
    }
}
