package us.mn.state.health.eh.hep.dss.security;

import be.looorent.micronaut.security.DefaultSecurityContextFactory;
import be.looorent.micronaut.security.SecurityContextFactory;
import io.jsonwebtoken.Claims;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.StringUtils;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Replaces(DefaultSecurityContextFactory.class)
public class UserSecurityContextFactory implements SecurityContextFactory {

    @Value("${keycloak.user-attribute:clientId}")
    private String USER_ATTRIBUTE;

    @Value("${keycloak.client-id}")
    private String CLIENT_ID;

    @Value("${keycloak.admin-app-client-id}")
    private String CLIENT_ID_ADMIN_APP;

    @Override
    public UserSecurityContext createSecurityContext(Claims tokenContent) {
        String userName = tokenContent.get(USER_ATTRIBUTE, String.class);

        if (StringUtils.isEmpty(userName)) {
            userName = tokenContent.get("preferred_username", String.class);
        }

        List<String> roles = new ArrayList<>();
        roles.addAll(getResourceRoles(tokenContent));
        roles.addAll(getRealmRoles(tokenContent));

        return new UserSecurityContext(userName, roles);
    }

    private List<String> getResourceRoles(Claims token) {
        Map<String, LinkedHashMap> roles = (Map<String, LinkedHashMap>) token.get("resource_access");
        List<String> userRoles = new ArrayList<>();
        if (roles.containsKey(CLIENT_ID)) {
            LinkedHashMap<String, List<String>> rolesInClient = roles.get(CLIENT_ID);
            userRoles.addAll(rolesInClient.get("roles"));
        }
        if (roles.containsKey(CLIENT_ID_ADMIN_APP)) {
            LinkedHashMap<String, List<String>> rolesInClient = roles.get(CLIENT_ID_ADMIN_APP);
            userRoles.addAll(rolesInClient.get("roles"));
        }
        return userRoles;
    }

    private List<String> getRealmRoles(Claims token) {
        Map<String, List<String>> roles = (Map<String, List<String>>) token.get("realm_access");
        if (roles.containsKey("roles")) {
            return roles.get("roles");
        } else {
            return new ArrayList<>();
        }
    }
}
