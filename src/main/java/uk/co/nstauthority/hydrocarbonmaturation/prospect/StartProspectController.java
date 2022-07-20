package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/prospect")
public class StartProspectController {

  @GetMapping("new")
  public ModelAndView getStartPage() {
    return null;
  }
}
