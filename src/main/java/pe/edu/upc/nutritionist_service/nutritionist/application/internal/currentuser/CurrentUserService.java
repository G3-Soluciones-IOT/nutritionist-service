package pe.edu.upc.nutritionist_service.nutritionist.application.internal.currentuser;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices.acl.rest.IamIntegrationClient;

@Service
public class CurrentUserService {

    private final IamIntegrationClient iamIntegrationClient;

    public CurrentUserService(IamIntegrationClient iamIntegrationClient) {
        this.iamIntegrationClient = iamIntegrationClient;
    }

    public Long currentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authenticated user is required");
        }
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            var token = jwtAuthentication.getToken().getTokenValue();
            return iamIntegrationClient.getCurrentUser("Bearer " + token).id();
        }
        return Long.valueOf(authentication.getName());
    }
}
