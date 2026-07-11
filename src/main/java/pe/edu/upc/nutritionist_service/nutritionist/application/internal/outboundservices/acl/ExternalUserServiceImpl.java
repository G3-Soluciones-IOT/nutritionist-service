package pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices.acl;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices.ExternalUserService;
import pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices.acl.rest.IamIntegrationClient;
import pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices.acl.rest.resource.SignUpResource;

import java.util.List;

/**
 * Implementation of ExternalUserService that communicates with iam-service
 * via Feign Client to validate and create users.
 */
@Service
public class ExternalUserServiceImpl implements ExternalUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalUserServiceImpl.class);

    private final IamIntegrationClient iamClient;

    public ExternalUserServiceImpl(IamIntegrationClient iamClient) {
        this.iamClient = iamClient;
    }

    /**
     * Creates a nutritionist user in the iam-service.
     *
     * @param username the username
     * @param password the password
     * @return the created user ID
     */
    @Override
    public Long createNutritionistUser(String username, String password) {
        try {
            var signUpResource = new SignUpResource(
                    username,
                    password,
                    List.of("ROLE_NUTRITIONIST")
            );
            var createdUser = iamClient.signUp(signUpResource);
            return createdUser.id();
        } catch (Exception e) {
            System.err.println("Error creating nutritionist user: " + e.getMessage());
            throw new IllegalArgumentException("Failed to create user in IAM service");
        }
    }

    /**
     * Checks if a user exists by ID in the iam-service.
     *
     * @param userId the user ID
     * @return true if the user exists, false otherwise
     */
    @Override
    public boolean userExists(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }

        try {
            var user = iamClient.getUserById(userId);
            return user != null && user.username() != null && !user.username().isBlank();
        } catch (FeignException.NotFound e) {
            return false;
        } catch (FeignException.Unauthorized | FeignException.Forbidden e) {
            LOGGER.error("IAM rejected internal user validation request for userId {} with status {}", userId, e.status(), e);
            throw new IllegalStateException("IAM rejected internal user validation request", e);
        } catch (FeignException e) {
            LOGGER.error("IAM user validation request failed for userId {} with status {}", userId, e.status(), e);
            throw new IllegalStateException("IAM user validation request failed", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error checking user existence for userId {}", userId, e);
            throw new IllegalStateException("Unexpected error checking user existence in IAM service", e);
        }
    }

    /**
     * Validates that a user does NOT exist before creating a nutritionist.
     * This prevents duplicate nutritionist registrations for existing users.
     *
     * @param userId the user ID
     * @throws IllegalArgumentException if the user already exists
     */
    @Override
    public void validateUserNotExists(Long userId) {
        if (userExists(userId)) {
            throw new IllegalArgumentException(
                    "User with ID " + userId + " is already registered in the system");
        }
    }
}

