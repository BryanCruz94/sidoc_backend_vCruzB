package ec.mil.ejercito.cedmt.sidoc.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class SidocRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String ROLES_CLAIM = "https://sidoc/roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object claim = jwt.getClaim(ROLES_CLAIM);
        if (claim == null) return List.of();

        if (claim instanceof Collection<?> roles) {
            return roles.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(r -> new SimpleGrantedAuthority(ROLE_PREFIX + r))
                    .collect(Collectors.toUnmodifiableSet());
        }

        String single = claim.toString().trim();
        if (single.isEmpty()) return List.of();
        return Set.of(new SimpleGrantedAuthority(ROLE_PREFIX + single));
    }
}