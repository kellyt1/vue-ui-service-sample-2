package us.mn.state.health.eh.hep.dss.security;

import be.looorent.micronaut.security.DefaultTokenValidator;
import be.looorent.micronaut.security.TokenValidator;
import io.jsonwebtoken.Claims;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Replaces(DefaultTokenValidator.class)
class UserValidator implements TokenValidator {

    @Value("${keycloak.user-attribute:clientId}")
    private String USER_ATTRIBUTE;

    @Value("${keycloak.client-id}")
    private String CLIENT_ID;

    @Value("${keycloak.admin-app-client-id}")
    private String CLIENT_ID_ADMIN_APP;

    @Value("${keycloak.permitted-roles}")
    private List<String> ROLE_REQUIRED;

    private static final String ROLES_ATTRIBUTE = "roles";
    private static final String RESOURCE_ACCESS_ATTRIBUTE = "resource_access";
    private static final String REALM_ACCESS_ATTRIBUTE = "realm_access";

    @Override
    public void validate(Claims tokenContent) {
        validateRoleIn(tokenContent);
    }

    private boolean containsAnyRole(List<String> tokenRoles) {
        return tokenRoles.stream().allMatch(t -> this.ROLE_REQUIRED.stream().anyMatch(t::contains));
    }

    private void validateRoleIn(Claims tokenContent) {
        Map<String, LinkedHashMap> roles = (Map<String, LinkedHashMap>) tokenContent.get(RESOURCE_ACCESS_ATTRIBUTE);
        boolean found = false;
        // Check the client roles for fhv-ingest-service
        if (roles.containsKey(CLIENT_ID)) {
            LinkedHashMap<String, List<String>> rolesInClient = roles.get(CLIENT_ID);
            found = containsAnyRole(rolesInClient.get(ROLES_ATTRIBUTE));
        }

        // Check the client roles for fhv-admin-app
        if (!found) {
            if (roles.containsKey(CLIENT_ID_ADMIN_APP)) {
                LinkedHashMap<String, List<String>> rolesInClient = roles.get(CLIENT_ID_ADMIN_APP);
                found = containsAnyRole(rolesInClient.get(ROLES_ATTRIBUTE));
            }
        }

        if (!found) {
            // Check Realm Access Roles
            Map<String, List<String>> rroles = (Map<String, List<String>>) tokenContent.get(REALM_ACCESS_ATTRIBUTE);
            if (rroles.containsKey(ROLES_ATTRIBUTE)) {
                found = containsAnyRole(rroles.get(ROLES_ATTRIBUTE));
            }
        }

        if (!found) {
            throw new SecurityException("API Credentials not authorized for this request.");
        }
    }
}
