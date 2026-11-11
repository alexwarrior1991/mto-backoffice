package com.alejandro.mtobackoffice.configuration.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;
import java.util.List;

/**
 * La persona que ha entrado, con lo que el ID token no cuenta: las audiencias del access token
 * (las del ultimo, si se ha renovado con otros permisos: {@link LiveAuthorities}).
 * Sirve para el panel de diagnostico de la pantalla de inicio (que las cinco APIs esten en
 * {@code aud} es lo que hace que un solo token valga en todas).
 */
public class BackofficeUser extends DefaultOidcUser {

    private final String nameAttributeKey;
    private final List<String> accessTokenAudience;

    public BackofficeUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
                          OidcUserInfo userInfo, String nameAttributeKey, List<String> accessTokenAudience) {
        super(authorities, idToken, userInfo, nameAttributeKey);
        this.nameAttributeKey = nameAttributeKey;
        this.accessTokenAudience = accessTokenAudience == null ? List.of() : List.copyOf(accessTokenAudience);
    }

    /** La misma persona con lo que concede un access token renovado: otras autoridades y otras audiencias. */
    public BackofficeUser with(Collection<? extends GrantedAuthority> authorities, List<String> accessTokenAudience) {
        return new BackofficeUser(authorities, getIdToken(), getUserInfo(), nameAttributeKey, accessTokenAudience);
    }

    public List<String> getAccessTokenAudience() {
        return accessTokenAudience;
    }
}
