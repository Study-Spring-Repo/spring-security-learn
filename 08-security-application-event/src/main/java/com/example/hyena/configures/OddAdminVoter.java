package com.example.hyena.configures;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OddAdminVoter implements AccessDecisionVoter<FilterInvocation> {

    private static final Pattern PATTERN = Pattern.compile("[0-9]+]$");

    private final RequestMatcher requestMatcher;

    public OddAdminVoter(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation filterInvocation, Collection<ConfigAttribute> collection) {
        HttpServletRequest request = filterInvocation.getRequest();

        // URL이 /admin 인가?
        if (!requiresAuthorization(request)) {
            return ACCESS_GRANTED;
        }

        User user = (User) authentication.getPrincipal();
        String name = user.getUsername();
        Matcher matcher = PATTERN.matcher(name);
        if (matcher.find()) {
            int number = name.charAt(name.length() - 1);
            if (number % 2 == 1) {
                return ACCESS_GRANTED;
            }
        }
        return ACCESS_DENIED;
    }

    private boolean requiresAuthorization(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }
}
