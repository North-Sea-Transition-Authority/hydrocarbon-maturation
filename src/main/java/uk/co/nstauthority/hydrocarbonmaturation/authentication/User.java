package uk.co.nstauthority.hydrocarbonmaturation.authentication;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.security.core.AuthenticatedPrincipal;

public record User(Long wuaId,
                   Long personId,
                   String forename,
                   String surname,
                   String emailAddress,
                   Long proxyWuaId,
                   String proxyUsername) implements AuthenticatedPrincipal, Serializable {

  @Override
  public String getName() {
    return Objects.nonNull(proxyWuaId) ? proxyWuaId.toString() : wuaId.toString();
  }

  public String displayName() {
    return "%s %s".formatted(forename, surname);
  }
}
