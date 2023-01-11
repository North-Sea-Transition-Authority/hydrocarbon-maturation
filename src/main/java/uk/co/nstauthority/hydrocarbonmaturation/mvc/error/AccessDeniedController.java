package uk.co.nstauthority.hydrocarbonmaturation.mvc.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;

@Controller
@Unauthenticated
public class AccessDeniedController {

  @GetMapping("/access-denied")
  public String getAccessDenied() {
    return "/remi/error/403";
  }
}
