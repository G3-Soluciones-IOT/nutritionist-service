package pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.nutritionist_service.nutritionist.application.internal.currentuser.CurrentUserService;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.queries.GetAppointmentsForUserQuery;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.valueobjects.AppointmentStatus;
import pe.edu.upc.nutritionist_service.nutritionist.domain.services.AppointmentCommandService;
import pe.edu.upc.nutritionist_service.nutritionist.domain.services.AppointmentQueryService;
import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources.AppointmentResource;
import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources.CreateAppointmentResource;
import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.transform.AppointmentResourceFromEntityAssembler;
import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.transform.CreateAppointmentCommandFromResourceAssembler;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/appointments", produces = "application/json")
@Tag(name = "Appointments", description = "Patient and nutritionist appointments")
public class AppointmentsController {

    private final AppointmentCommandService commandService;
    private final AppointmentQueryService queryService;
    private final CurrentUserService currentUserService;

    public AppointmentsController(
            AppointmentCommandService commandService,
            AppointmentQueryService queryService,
            CurrentUserService currentUserService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "Create an appointment for an accepted nutritionist-patient relationship")
    public ResponseEntity<AppointmentResource> create(
            Authentication authentication,
            @RequestBody CreateAppointmentResource resource) {
        var currentUserId = currentUserService.currentUserId(authentication);
        var command = CreateAppointmentCommandFromResourceAssembler.toCommandFromResource(resource);
        var appointment = commandService.handle(command, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment));
    }

    @GetMapping("/me")
    @Operation(summary = "List current user's appointments")
    public ResponseEntity<List<AppointmentResource>> getMyAppointments(
            Authentication authentication,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        var currentUserId = currentUserService.currentUserId(authentication);
        var appointments = queryService.findForUser(new GetAppointmentsForUserQuery(currentUserId, status, from, to))
                .stream()
                .map(AppointmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment detail")
    public ResponseEntity<AppointmentResource> getById(Authentication authentication, @PathVariable Long id) {
        var currentUserId = currentUserService.currentUserId(authentication);
        var appointment = queryService.findAccessibleById(id, currentUserId);
        return ResponseEntity.ok(AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResource> confirm(Authentication authentication, @PathVariable Long id) {
        var currentUserId = currentUserService.currentUserId(authentication);
        var appointment = commandService.confirm(id, currentUserId);
        return ResponseEntity.ok(AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<AppointmentResource> reject(Authentication authentication, @PathVariable Long id) {
        var currentUserId = currentUserService.currentUserId(authentication);
        var appointment = commandService.reject(id, currentUserId);
        return ResponseEntity.ok(AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResource> cancel(Authentication authentication, @PathVariable Long id) {
        var currentUserId = currentUserService.currentUserId(authentication);
        var appointment = commandService.cancel(id, currentUserId);
        return ResponseEntity.ok(AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResource> complete(Authentication authentication, @PathVariable Long id) {
        var currentUserId = currentUserService.currentUserId(authentication);
        var appointment = commandService.complete(id, currentUserId);
        return ResponseEntity.ok(AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment));
    }
}
