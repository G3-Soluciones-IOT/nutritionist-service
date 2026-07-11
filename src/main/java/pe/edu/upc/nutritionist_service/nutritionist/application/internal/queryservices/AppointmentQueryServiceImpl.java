package pe.edu.upc.nutritionist_service.nutritionist.application.internal.queryservices;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates.Appointment;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.queries.GetAppointmentsForUserQuery;
import pe.edu.upc.nutritionist_service.nutritionist.domain.services.AppointmentQueryService;
import pe.edu.upc.nutritionist_service.nutritionist.infrastructure.persistence.jpa.repositories.AppointmentRepository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentQueryServiceImpl implements AppointmentQueryService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentQueryServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Optional<Appointment> findById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    @Override
    public List<Appointment> findForUser(GetAppointmentsForUserQuery query) {
        var appointments = new LinkedHashMap<Long, Appointment>();
        appointmentRepository.findByPatientUserIdOrderByScheduledAtAsc(query.userId())
                .forEach(appointment -> appointments.put(appointment.getId(), appointment));
        appointmentRepository.findByNutritionistUserIdOrderByScheduledAtAsc(query.userId())
                .forEach(appointment -> appointments.put(appointment.getId(), appointment));

        return appointments.values().stream()
                .filter(appointment -> query.status() == null || appointment.getStatus() == query.status())
                .filter(appointment -> query.from() == null || !appointment.getScheduledAt().isBefore(query.from()))
                .filter(appointment -> query.to() == null || !appointment.getScheduledAt().isAfter(query.to()))
                .sorted(Comparator.comparing(Appointment::getScheduledAt))
                .toList();
    }

    @Override
    public Appointment findAccessibleById(Long appointmentId, Long currentUserId) {
        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!appointment.getPatientUserId().equals(currentUserId)
                && !appointment.getNutritionistUserId().equals(currentUserId)) {
            throw new AccessDeniedException("User is not allowed to access this appointment");
        }
        return appointment;
    }
}
