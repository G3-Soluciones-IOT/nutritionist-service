package pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.nutritionist_service.nutritionist.domain.services.NutritionistPatientCommandService;
import pe.edu.upc.nutritionist_service.nutritionist.domain.services.NutritionistPatientQueryService;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.commands.ApprovePatientCommand;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.queries.GetNutritionistByUserIdQuery;
import pe.edu.upc.nutritionist_service.nutritionist.domain.services.NutritionistQueryService;

import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources.CreateNutritionistPatientResource;
import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources.ChatContactResource;
import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources.NutritionistPatientResource;

import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.transform.CreateNutritionistPatientCommandFromResourceAssembler;
import pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.transform.NutritionistPatientResourceFromEntityAssembler;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = "/api/v1/nutritionist-patients", produces = "application/json")
@Tag(name = "NutritionistPatients", description = "Relación Nutricionista–Paciente")
public class NutritionistPatientsController {

    private final NutritionistPatientCommandService commandService;
    private final NutritionistPatientQueryService queryService;
    private final NutritionistQueryService nutritionistQueryService;

    public NutritionistPatientsController(
            NutritionistPatientCommandService commandService,
            NutritionistPatientQueryService queryService,
            NutritionistQueryService nutritionistQueryService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.nutritionistQueryService = nutritionistQueryService;
    }

    @PostMapping
    public ResponseEntity<NutritionistPatientResource> create(@RequestBody CreateNutritionistPatientResource r) {
        var command = CreateNutritionistPatientCommandFromResourceAssembler.toCommandFromResource(r);
        var created = commandService.handle(command);
        return ResponseEntity.ok(NutritionistPatientResourceFromEntityAssembler.toResourceFromEntity(created));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        var updated = commandService.handle(new ApprovePatientCommand(id));
        return updated
                .map(NutritionistPatientResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nutritionist/{id}")
    public List<NutritionistPatientResource> getPatients(@PathVariable Integer id) {
        return queryService.findPatientsByNutritionist(id)
                .stream()
                .map(NutritionistPatientResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }

    @GetMapping("/patient/{id}")
    public List<NutritionistPatientResource> getNutritionists(@PathVariable Long id) {
        return queryService.findNutritionistsOfPatient(id)
                .stream()
                .map(NutritionistPatientResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }

    @GetMapping("/chat-contacts/{userId}")
    public List<ChatContactResource> getChatContacts(@PathVariable Long userId) {
        var contactsByUserId = new LinkedHashMap<Long, ChatContactResource>();

        queryService.findNutritionistsOfPatient(userId).stream()
                .filter(relationship -> Boolean.TRUE.equals(relationship.getAccepted()))
                .forEach(relationship -> nutritionistQueryService.findById(relationship.getNutritionistId().longValue())
                        .ifPresent(nutritionist -> contactsByUserId.put(
                                nutritionist.getUserId(),
                                new ChatContactResource(
                                        nutritionist.getUserId(),
                                        relationship.getId(),
                                        relationship.getNutritionistId(),
                                        nutritionist.getUserId(),
                                        relationship.getPatientUserId(),
                                        relationship.getAccepted()
                                ))));

        nutritionistQueryService.handle(new GetNutritionistByUserIdQuery(userId))
                .ifPresent(nutritionist -> queryService.findPatientsByNutritionist(nutritionist.getId().intValue()).stream()
                        .filter(relationship -> Boolean.TRUE.equals(relationship.getAccepted()))
                        .forEach(relationship -> contactsByUserId.put(
                                relationship.getPatientUserId(),
                                new ChatContactResource(
                                        relationship.getPatientUserId(),
                                        relationship.getId(),
                                        relationship.getNutritionistId(),
                                        nutritionist.getUserId(),
                                        relationship.getPatientUserId(),
                                        relationship.getAccepted()
                                ))));

        return new ArrayList<>(contactsByUserId.values());
    }
}

