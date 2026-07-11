package pe.edu.upc.nutritionist_service.nutritionist.application.internal.commandservices;

import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates.Appointment;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates.Nutritionist;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates.NutritionistPatient;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.commands.CreateAppointmentCommand;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.valueobjects.AppointmentStatus;
import pe.edu.upc.nutritionist_service.nutritionist.domain.services.AppointmentCommandService;
import pe.edu.upc.nutritionist_service.nutritionist.infrastructure.persistence.jpa.repositories.AppointmentRepository;
import pe.edu.upc.nutritionist_service.nutritionist.infrastructure.persistence.jpa.repositories.NutritionistPatientRepository;
import pe.edu.upc.nutritionist_service.nutritionist.infrastructure.persistence.jpa.repositories.NutritionistRepository;

import java.time.Instant;

@Service
@Transactional
public class AppointmentCommandServiceImpl implements AppointmentCommandService {

    private final AppointmentRepository appointmentRepository;
    private final NutritionistPatientRepository nutritionistPatientRepository;
    private final NutritionistRepository nutritionistRepository;

    public AppointmentCommandServiceImpl(
            AppointmentRepository appointmentRepository,
            NutritionistPatientRepository nutritionistPatientRepository,
            NutritionistRepository nutritionistRepository) {
        this.appointmentRepository = appointmentRepository;
        this.nutritionistPatientRepository = nutritionistPatientRepository;
        this.nutritionistRepository = nutritionistRepository;
    }

    @Override
    public Appointment handle(CreateAppointmentCommand command, Long currentUserId) {
        validateCreate(command);
        var relationship = acceptedRelationship(command.nutritionistPatientId());
        var nutritionist = nutritionistOf(relationship);
        var now = Instant.now();
        var creatorIsPatient = relationship.getPatientUserId().equals(currentUserId);
        var creatorIsNutritionist = nutritionist.getUserId().equals(currentUserId);
        if (!creatorIsPatient && !creatorIsNutritionist) {
            throw new AccessDeniedException("User is not allowed to create appointments for this relationship");
        }

        var status = creatorIsNutritionist ? AppointmentStatus.CONFIRMED : AppointmentStatus.REQUESTED;
        var confirmedAt = creatorIsNutritionist ? now : null;

        return appointmentRepository.save(new Appointment(
                relationship.getId(),
                relationship.getNutritionistId(),
                nutritionist.getUserId(),
                relationship.getPatientUserId(),
                command.scheduledAt(),
                command.durationMinutes(),
                status,
                command.reason(),
                command.notes(),
                command.meetingUrl(),
                now,
                confirmedAt
        ));
    }

    @Override
    public Appointment confirm(Long appointmentId, Long currentUserId) {
        var appointment = appointmentForNutritionistAction(appointmentId, currentUserId);
        appointment.confirm(Instant.now());
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment reject(Long appointmentId, Long currentUserId) {
        var appointment = appointmentForNutritionistAction(appointmentId, currentUserId);
        appointment.reject();
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment cancel(Long appointmentId, Long currentUserId) {
        var appointment = appointmentForParticipantAction(appointmentId, currentUserId);
        appointment.cancel(Instant.now());
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment complete(Long appointmentId, Long currentUserId) {
        var appointment = appointmentForNutritionistAction(appointmentId, currentUserId);
        appointment.complete();
        return appointmentRepository.save(appointment);
    }

    private Appointment appointmentForNutritionistAction(Long appointmentId, Long currentUserId) {
        var appointment = appointmentById(appointmentId);
        if (!appointment.getNutritionistUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the assigned nutritionist can perform this action");
        }
        return appointment;
    }

    private Appointment appointmentForParticipantAction(Long appointmentId, Long currentUserId) {
        var appointment = appointmentById(appointmentId);
        if (!appointment.getNutritionistUserId().equals(currentUserId)
                && !appointment.getPatientUserId().equals(currentUserId)) {
            throw new AccessDeniedException("User is not allowed to access this appointment");
        }
        return appointment;
    }

    private Appointment appointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    }

    private NutritionistPatient acceptedRelationship(Long nutritionistPatientId) {
        var relationship = nutritionistPatientRepository.findById(nutritionistPatientId)
                .orElseThrow(() -> new IllegalArgumentException("Nutritionist-patient relationship not found"));
        if (!Boolean.TRUE.equals(relationship.getAccepted())) {
            throw new AccessDeniedException("Appointments require an accepted nutritionist-patient relationship");
        }
        return relationship;
    }

    private Nutritionist nutritionistOf(NutritionistPatient relationship) {
        return nutritionistRepository.findById(relationship.getNutritionistId().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Nutritionist not found"));
    }

    private void validateCreate(CreateAppointmentCommand command) {
        if (command.nutritionistPatientId() == null) {
            throw new IllegalArgumentException("nutritionistPatientId is required");
        }
        if (command.scheduledAt() == null) {
            throw new IllegalArgumentException("scheduledAt is required");
        }
        if (command.durationMinutes() == null || command.durationMinutes() <= 0) {
            throw new IllegalArgumentException("durationMinutes must be greater than zero");
        }
    }
}
