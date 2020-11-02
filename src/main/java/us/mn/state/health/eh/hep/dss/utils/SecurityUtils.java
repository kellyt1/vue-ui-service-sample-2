package us.mn.state.health.eh.hep.dss.utils;

import io.micronaut.http.HttpRequest;
import us.mn.state.health.eh.hep.dss.security.UserSecurityContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class SecurityUtils {

    @Inject protected HttpRequest request;

    public UserSecurityContext getUserContext() {
        Optional<Object> ctx = request.getAttribute("securityContext");
        if (ctx.isPresent()) {
            UserSecurityContext usc = (UserSecurityContext) ctx.get();
            return usc;
        }
        return null;
    }
}
