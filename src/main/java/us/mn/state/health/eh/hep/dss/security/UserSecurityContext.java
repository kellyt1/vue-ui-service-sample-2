package us.mn.state.health.eh.hep.dss.security;

import be.looorent.micronaut.security.SecurityContext;
import io.micronaut.http.HttpRequest;

import java.util.*;
import java.util.stream.Collectors;

public class UserSecurityContext implements SecurityContext {
    private String userName;
    private List<String> roles;

    public UserSecurityContext(HttpRequest httpRequest) {
        this.userName = "anonymous";
        this.roles = new ArrayList<>();

        Optional<Object> ctx = httpRequest.getAttribute("securityContext");
        if (ctx.isPresent()) {
            UserSecurityContext usc1 = (UserSecurityContext) ctx.get();
            this.userName = usc1.getUserName();
            this.roles = usc1.getRoles();
        }
    }

    public UserSecurityContext(String userName, List<String> roles) {
        this.userName = userName;
        this.roles = roles;
    }

    public void setUserName(String s) { this.userName = s; }
    public String getUserName() {
        return this.userName;
    }

    public void setRoles(List<String> roles) { this.roles = roles; }
    public List<String> getRoles() {
        return this.roles;
    }

    public boolean hasRole(String... roleName) {
        boolean result = false;
        if (roles != null && !roles.isEmpty()) {
            Set<String> intersection = Arrays.asList(roleName)
                    .stream()
                    .distinct()
                    .filter(this.roles::contains)
                    .collect(Collectors.toSet());
            return !intersection.isEmpty();
        }
        return result;
    }

    public void requireRole(String... roleName) {
        if (!hasRole(roleName)) {
            throw new SecurityException("API Credentials not authorized for this request.");
        }
    }

    public String toString() {
        return "username [" + this.userName + "] roles " + this.roles;
    }
}
