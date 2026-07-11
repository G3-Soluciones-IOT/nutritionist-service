package pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices.ExternalPatientProfileService;

@Service
public class ExternalPatientProfileServiceImpl implements ExternalPatientProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalPatientProfileServiceImpl.class);
    private static final String INTERNAL_HEADER = "X-Internal-Request";

    private final RestClient profilesClient;
    private final String internalSecret;

    public ExternalPatientProfileServiceImpl(
            @Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder,
            @Value("${services.profiles.base-url}") String profilesBaseUrl,
            @Value("${authorization.internal-service.secret}") String internalSecret) {
        this.profilesClient = restClientBuilder.baseUrl(profilesBaseUrl).build();
        this.internalSecret = internalSecret;
    }

    @Override
    public boolean patientProfileExists(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }

        try {
            profilesClient.get()
                    .uri("/api/v1/user-profiles/exists/by-user/{userId}", userId)
                    .header(INTERNAL_HEADER, internalSecret)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound exception) {
            return false;
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED
                    || exception.getStatusCode() == HttpStatus.FORBIDDEN) {
                LOGGER.error("profiles-service rejected internal profile validation for userId {} with status {}",
                        userId, exception.getStatusCode(), exception);
                throw new IllegalStateException("profiles-service rejected internal profile validation request", exception);
            }
            LOGGER.error("profiles-service profile validation failed for userId {} with status {}",
                    userId, exception.getStatusCode(), exception);
            throw new IllegalStateException("profiles-service profile validation request failed", exception);
        } catch (RestClientException exception) {
            LOGGER.error("profiles-service profile validation failed for userId {}", userId, exception);
            throw new IllegalStateException("profiles-service profile validation request failed", exception);
        }
    }
}
