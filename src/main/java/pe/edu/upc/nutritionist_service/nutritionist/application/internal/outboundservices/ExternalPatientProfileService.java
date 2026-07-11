package pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices;

public interface ExternalPatientProfileService {
    boolean patientProfileExists(Long userId);
}
