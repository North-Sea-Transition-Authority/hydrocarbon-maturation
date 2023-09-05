package uk.co.nstauthority.hydrocarbonmaturation.mvc.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfigurationProperties;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = {
    ErrorConfigurationProperties.class,
    ServiceConfigurationProperties.class,
    CustomerConfigurationProperties.class,
    TechnicalSupportConfigurationProperties.class})
@ContextConfiguration(classes = {
    ErrorConfiguration.class,
    ServiceBrandingConfigurationProperties.class,
    TechnicalSupportConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
class ErrorServiceTest {

  private static final String ERROR_REF_ATTRIBUTE_NAME = "errorRef";
  private static final String SERVICE_HOME_URLS_ATTRIBUTE_NAME = "serviceHomeUrl";
  private static final String ACCESSIBILITY_STATEMENT_URL = "accessibilityStatementUrl";
  private static final String FEEDBACK_PAGE_URL = "feedbackPageUrl";
  private static final String CONTACT_URL = "contactInformationUrl";
  private static final String STACK_TRACE_ATTRIBUTE = "stackTrace";
  private static final String SERVICE_BRANDING_ATTRIBUTE = "serviceBranding";
  private static final String CUSTOMER_BRANDING_ATTRIBUTE = "customerBranding";
  private static final String TECHNICAL_SUPPORT_ATTRIBUTE = "technicalSupport";
  private static final String UNSAFE_CHARACTERS = "0125AEIOULNSZ";

  @Autowired
  private ErrorConfiguration errorConfiguration;

  @Autowired
  private ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties;

  @Autowired
  private TechnicalSupportConfiguration technicalSupportConfiguration;

  private ErrorService errorService;

  @BeforeEach
  public void setup() {
    errorService = new ErrorService(
        errorConfiguration,
        serviceBrandingConfigurationProperties,
        technicalSupportConfiguration);
  }

  @Test
  void addErrorAttributesToModel_whenThrowableError_assertExpectedModelAttributes() {
    final var resultingModelMap =  errorService.addErrorAttributesToModel(
        new ModelAndView(),
        new NullPointerException(),
        null,
        null
    ).getModelMap();

    assertThat(resultingModelMap).containsOnlyKeys(
        ERROR_REF_ATTRIBUTE_NAME,
        SERVICE_HOME_URLS_ATTRIBUTE_NAME,
        SERVICE_BRANDING_ATTRIBUTE,
        CUSTOMER_BRANDING_ATTRIBUTE,
        TECHNICAL_SUPPORT_ATTRIBUTE,
        ACCESSIBILITY_STATEMENT_URL,
        FEEDBACK_PAGE_URL,
        STACK_TRACE_ATTRIBUTE,
        CONTACT_URL
    );
    Object errorRef = resultingModelMap.get(ERROR_REF_ATTRIBUTE_NAME);
    assertThat(errorRef).isNotNull();
    assertThat((String) errorRef).doesNotContainPattern(UNSAFE_CHARACTERS);
    assertThat(resultingModelMap.get(STACK_TRACE_ATTRIBUTE)).isNotNull();
  }

  @Test
  void addErrorAttributesToModel_whenNoThrowableError_assertExpectedModelAttributes() {
    final var resultingModelMap =  errorService.addErrorAttributesToModel(
        new ModelAndView(),
        null,
        null,
        null
    ).getModelMap();

    assertThat(resultingModelMap).containsOnlyKeys(
        SERVICE_HOME_URLS_ATTRIBUTE_NAME,
        SERVICE_BRANDING_ATTRIBUTE,
        CUSTOMER_BRANDING_ATTRIBUTE,
        TECHNICAL_SUPPORT_ATTRIBUTE,
        ACCESSIBILITY_STATEMENT_URL,
        CONTACT_URL,
        FEEDBACK_PAGE_URL
    );
  }
}
