package uk.co.nstauthority.hydrocarbonmaturation.footer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.AccessibilityStatementConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;

@Controller
@RequestMapping("/accessibility-statement")
@Unauthenticated
public class AccessibilityStatementController {

  private final TechnicalSupportConfiguration technicalSupportConfiguration;
  private final AccessibilityStatementConfiguration accessibilityStatementConfiguration;

  @Autowired
  public AccessibilityStatementController(TechnicalSupportConfiguration technicalSupportConfiguration,
                                          AccessibilityStatementConfiguration accessibilityStatementConfiguration) {
    this.technicalSupportConfiguration = technicalSupportConfiguration;
    this.accessibilityStatementConfiguration = accessibilityStatementConfiguration;
  }

  @GetMapping
  public ModelAndView getAccessibilityStatement() {
    return new ModelAndView("remi/footer/accessibilityStatement")
        .addObject("technicalSupport",
            technicalSupportConfiguration.getTechnicalSupportConfigurationProperties())
        .addObject("accessibilityConfig",
            accessibilityStatementConfiguration.getAccessibilityStatementConfigurationProperties());
  }
}
