package us.mn.state.health.eh.hep.dss.filters;

import be.looorent.micronaut.security.SecurityFilter;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Filter(value = {"/**"})
public class UserSecurityFilter implements HttpServerFilter {

    private static final Logger logger = LoggerFactory.getLogger(UserSecurityFilter.class);

    private SecurityFilter securityFilter;

    public UserSecurityFilter(SecurityFilter sf) {
        this.securityFilter = sf;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        return securityFilter.doFilter(request, chain);
    }
}
