package pe.edu.upc.nutritionist_service.nutritionist.interfaces.rest.resources;

public record ChatContactResource(
        Long contactUserId,
        Long relationshipId,
        Integer nutritionistId,
        Long nutritionistUserId,
        Long patientUserId,
        Boolean accepted
) {
}
