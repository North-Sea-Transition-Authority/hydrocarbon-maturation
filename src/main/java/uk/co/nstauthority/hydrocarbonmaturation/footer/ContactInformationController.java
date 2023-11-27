package uk.co.nstauthority.hydrocarbonmaturation.footer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;
import uk.co.nstauthority.hydrocarbonmaturation.businesssupport.BusinessSupportConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;

@Controller
@RequestMapping("/contact-information")
@Unauthenticated
public class ContactInformationController {

  private final BusinessSupportConfigurationProperties businessSupportConfigurationProperties;
  private final TechnicalSupportConfiguration technicalSupportConfiguration;

  @Autowired
  public ContactInformationController(BusinessSupportConfigurationProperties businessSupportConfigurationProperties,
                                      TechnicalSupportConfiguration technicalSupportConfiguration) {
    this.businessSupportConfigurationProperties = businessSupportConfigurationProperties;
    this.technicalSupportConfiguration = technicalSupportConfiguration;
  }

  @GetMapping
  public ModelAndView getContactInformationPage() {
    return new ModelAndView("remi/footer/contactInformation")
        .addObject("businessSupport",
            businessSupportConfigurationProperties)
        .addObject("technicalSupport",
            technicalSupportConfiguration.getTechnicalSupportConfigurationProperties());
  }
}
