package uk.co.nstauthority.hydrocarbonmaturation.footer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.businesssupport.BusinessSupportConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfigurationProperties;

@ContextConfiguration(classes = {
    ContactInformationController.class,
    TechnicalSupportConfiguration.class})
@EnableConfigurationProperties(value = {
    BusinessSupportConfigurationProperties.class,
    TechnicalSupportConfigurationProperties.class})
class ContactInformationControllerTest extends RealSecurityAbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Autowired
  private BusinessSupportConfigurationProperties businessSupportConfigurationProperties;

  @Autowired
  private TechnicalSupportConfiguration technicalSupportConfiguration;

  @SecurityTest
  void getContactInformationPage_assertModelProperties() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(ContactInformationController.class).getContactInformationPage()))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/footer/contactInformation"))
        .andExpect(model().attribute("businessSupport",
            businessSupportConfigurationProperties))
        .andExpect(model().attribute("technicalSupport",
            technicalSupportConfiguration.getTechnicalSupportConfigurationProperties()));;
  }

  @SecurityTest
  void getContactInformationPage_withNoUser_assertStatusOk() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(ContactInformationController.class).getContactInformationPage())))
        .andExpect(status().isOk());
  }
}