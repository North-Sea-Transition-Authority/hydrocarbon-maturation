package uk.co.nstauthority.hydrocarbonmaturation.authentication;

import java.util.Collection;
import java.util.HashSet;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class SamlAuthenticationUtil {

  public SamlAuthenticationUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Builder Builder() {
    return new Builder();
  }

  public static class Builder {

    private Builder() {
    }

    private User user = UserTestUtil.Builder().build();
    private final Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();

    public void setSecurityContext() {
      var authentication = build();
      SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }

    public ServiceSaml2Authentication build() {
      return new ServiceSaml2Authentication(user, grantedAuthorities);
    }

    public Builder withGrantedAuthorities(Collection<GrantedAuthority> grantedAuthorities) {
      this.grantedAuthorities.addAll(grantedAuthorities);
      return this;
    }

    public Builder withGrantedAuthority(GrantedAuthority grantedAuthority) {
      this.grantedAuthorities.add(grantedAuthority);
      return this;
    }

    public Builder withGrantedAuthority(String grantedAuthority) {
      this.grantedAuthorities.add(new SimpleGrantedAuthority(grantedAuthority));
      return this;
    }

    public Builder withUser(User user) {
      this.user = user;
      return this;
    }
  }
}
