package uk.co.nstauthority.hydrocarbonmaturation.authentication;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.security.core.AuthenticatedPrincipal;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;

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

  public String displayNameIncludingAnyProxyUser() {
    var userDisplayName = displayName();
    return proxyWuaId != null ? String.format("%s as %s", proxyUsername, userDisplayName) : userDisplayName;
  }

  public static User from(EnergyPortalUserDto energyPortalUser) {
    return new User(
        energyPortalUser.webUserAccountId(),
        null,
        energyPortalUser.forename(),
        energyPortalUser.surname(),
        energyPortalUser.emailAddress(),
        null,
        null
    );
  }
}
