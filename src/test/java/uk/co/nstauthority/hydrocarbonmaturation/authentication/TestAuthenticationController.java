package uk.co.nstauthority.hydrocarbonmaturation.authentication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class TestAuthenticationController {

  @GetMapping("/secured")
  public ModelAndView renderSecured() {
    return new ModelAndView();
  }

}