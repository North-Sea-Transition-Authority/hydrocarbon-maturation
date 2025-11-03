package uk.co.nstauthority.hydrocarbonmaturation.authentication;

import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.audit.AuditRevisionUtil;

@Service
public class UserService {

  public User getUser() {
    if (SecurityContextHolder.getContext().getAuthentication() instanceof ServiceSaml2Authentication authentication) {
      if (authentication.getPrincipal() instanceof User user) {
        return user;
      } else {
        throw new InvalidAuthenticationException("User not found in ServiceSaml2Authentication principal");
      }
    } else {
      throw new InvalidAuthenticationException("ServiceSaml2Authentication not found in authentication context");
    }
  }

  public Optional<User> getOptionalUserDetail() {
    if (SecurityContextHolder.getContext().getAuthentication() instanceof ServiceSaml2Authentication authentication
        && authentication.getPrincipal() instanceof User serviceUserDetail) {
      return Optional.of(serviceUserDetail);
    }

    return Optional.ofNullable(AuditRevisionUtil.getFallbackAuditUser());
  }

  public Integer getUserWuaId() {
    var user = getUser();
    return user.wuaId().intValue();
  }
}
