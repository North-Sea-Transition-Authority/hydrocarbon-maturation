package uk.co.nstauthority.hydrocarbonmaturation.authentication;

import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    try {
      return Optional.of(getUser());
    } catch (InvalidAuthenticationException exception) {
      return Optional.empty();
    }
  }

  public Integer getUserWuaId() {
    var user = getUser();
    return user.wuaId().intValue();
  }
}
