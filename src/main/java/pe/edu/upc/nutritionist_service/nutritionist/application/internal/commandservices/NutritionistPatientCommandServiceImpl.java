package pe.edu.upc.nutritionist_service.nutritionist.application.internal.commandservices;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices.ExternalPatientProfileService;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.aggregates.NutritionistPatient;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.commands.ApprovePatientCommand;
import pe.edu.upc.nutritionist_service.nutritionist.domain.model.commands.CreateNutritionistPatientCommand;
import pe.edu.upc.nutritionist_service.nutritionist.domain.services.NutritionistPatientCommandService;
import pe.edu.upc.nutritionist_service.nutritionist.infrastructure.persistence.jpa.repositories.NutritionistPatientRepository;

import java.util.Optional;

@Service
@Transactional
public class NutritionistPatientCommandServiceImpl implements NutritionistPatientCommandService {

    private final NutritionistPatientRepository repository;
    private final ExternalPatientProfileService externalPatientProfileService;

    public NutritionistPatientCommandServiceImpl(
            NutritionistPatientRepository repository,
            ExternalPatientProfileService externalPatientProfileService) {
        this.repository = repository;
        this.externalPatientProfileService = externalPatientProfileService;
    }

    @Override
    public NutritionistPatient handle(CreateNutritionistPatientCommand command) {
        if (!externalPatientProfileService.patientProfileExists(command.patientUserId())) {
            throw new IllegalArgumentException(
                    "No patient user profile found for userId: " + command.patientUserId());
        }

        var relation = new NutritionistPatient(
                command.nutritionistId(),
                command.patientUserId(),
                command.serviceType(),
                command.startDate(),
                command.scheduledAt()
        );
        return repository.save(relation);
    }

    @Override
    public Optional<NutritionistPatient> handle(ApprovePatientCommand command) {
        return repository.findById(command.relationId())
                .map(r -> {
                    r.approve();
                    return repository.save(r);
                });
    }
}
