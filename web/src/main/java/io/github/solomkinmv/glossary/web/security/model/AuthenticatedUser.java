package io.github.solomkinmv.glossary.web.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Represents authenticated user. Used by Spring Security.
 */
public class AuthenticatedUser implements UserDetails {

    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(String username, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
