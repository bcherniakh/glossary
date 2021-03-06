package io.github.solomkinmv.glossary.web.security.auth;

import io.jsonwebtoken.lang.Assert;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link RequestMatcher}, which matches path except list of excluded paths.
 */
public class SkipPathRequestMatcher implements RequestMatcher {
    private final OrRequestMatcher skipMatcher;
    private final RequestMatcher processingMatcher;

    public SkipPathRequestMatcher(List<String> pathsToSkip, String processingPath) {
        Assert.notNull(pathsToSkip);
        List<RequestMatcher> skipMatcherList = pathsToSkip.stream()
                                                          .map(AntPathRequestMatcher::new)
                                                          .collect(Collectors.toList());
        skipMatcher = new OrRequestMatcher(skipMatcherList);
        processingMatcher = new AntPathRequestMatcher(processingPath);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !skipMatcher.matches(request) && processingMatcher.matches(request);

    }
}
