package pe.edu.upc.nutritionist_service.nutritionist.application.internal.outboundservices.acl.rest.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Feign interceptor to add internal service authentication header.
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String INTERNAL_HEADER = "X-Internal-Request";
    private final String internalSecret;

    public FeignClientInterceptor(
            @Value("${authorization.internal-service.secret}") String internalSecret) {
        this.internalSecret = internalSecret;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(INTERNAL_HEADER, internalSecret);
    }
}

