package pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.valueobjects.AppointmentStatus;
import pe.edu.upc.nutritionist_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.Instant;
import java.util.EnumSet;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "appointments")
public class Appointment extends AuditableAbstractAggregateRoot<Appointment> {

    @NotNull
    @Column(name = "nutritionist_patient_id", nullable = false)
    private Long nutritionistPatientId;

    @NotNull
    @Column(name = "nutritionist_id", nullable = false)
    private Integer nutritionistId;

    @NotNull
    @Column(name = "nutritionist_user_id", nullable = false)
    private Long nutritionistUserId;

    @NotNull
    @Column(name = "patient_user_id", nullable = false)
    private Long patientUserId;

    @NotNull
    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @NotNull
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AppointmentStatus status;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "meeting_url", length = 500)
    private String meetingUrl;

    @NotNull
    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    public Appointment(
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
            Instant confirmedAt) {
        this.nutritionistPatientId = nutritionistPatientId;
        this.nutritionistId = nutritionistId;
        this.nutritionistUserId = nutritionistUserId;
        this.patientUserId = patientUserId;
        this.scheduledAt = scheduledAt;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.reason = reason;
        this.notes = notes;
        this.meetingUrl = meetingUrl;
        this.requestedAt = requestedAt;
        this.confirmedAt = confirmedAt;
    }

    public void confirm(Instant confirmedAt) {
        ensureNotTerminal();
        if (this.status != AppointmentStatus.REQUESTED) {
            throw new IllegalStateException("Only requested appointments can be confirmed");
        }
        this.status = AppointmentStatus.CONFIRMED;
        this.confirmedAt = confirmedAt;
    }

    public void reject() {
        ensureNotTerminal();
        if (this.status != AppointmentStatus.REQUESTED) {
            throw new IllegalStateException("Only requested appointments can be rejected");
        }
        this.status = AppointmentStatus.REJECTED;
    }

    public void cancel(Instant cancelledAt) {
        ensureNotTerminal();
        this.status = AppointmentStatus.CANCELLED;
        this.cancelledAt = cancelledAt;
    }

    public void complete() {
        ensureNotTerminal();
        if (this.status != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed appointments can be completed");
        }
        this.status = AppointmentStatus.COMPLETED;
    }

    private void ensureNotTerminal() {
        if (EnumSet.of(
                AppointmentStatus.REJECTED,
                AppointmentStatus.CANCELLED,
                AppointmentStatus.COMPLETED
        ).contains(this.status)) {
            throw new IllegalStateException("Appointment is already in a terminal status");
        }
    }
}
