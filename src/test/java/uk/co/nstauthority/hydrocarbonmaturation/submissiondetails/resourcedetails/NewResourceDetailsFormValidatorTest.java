package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class NewResourceDetailsFormValidatorTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private InternalTeamService internalTeamService;

  @InjectMocks
  private NewResourceDetailsFormValidator newResourceDetailsFormValidator;

  @Test
  void isValid_asIndustryUser() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    var form = new ResourceDetailsForm();
    form.setResourceName("test");
    form.setResponsibleOrganisationId(String.valueOf(1));
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(newResourceDetailsFormValidator.isValid(bindingResult, ResourceType.PROSPECT, USER));
  }

  @Test
  void isValid_asRegulatorUser() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var form = new ResourceDetailsForm();
    form.setResourceName("test");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(newResourceDetailsFormValidator.isValid(bindingResult, ResourceType.PROSPECT, USER));
  }

  @Test
  void isInvalid_asIndustryUser_assertAllErrors() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    var form = new ResourceDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(newResourceDetailsFormValidator.isValid(bindingResult, ResourceType.PROSPECT, USER));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );

    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    assertThat(extractedErrorMessages)
        .contains(
            entry("resourceName", Set.of("Enter the prospect name")),
            entry("responsibleOrganisationId", Set.of("Enter the responsible organisation")),
            entry("onshoreOffshore", Set.of("Select onshore or offshore"))
        );
  }

  @Test
  void isInvalid_asRegulatorUser_assertAllErrors() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var form = new ResourceDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(newResourceDetailsFormValidator.isValid(bindingResult, ResourceType.PROSPECT, USER));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceName", Set.of("resourceName.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );

    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    assertThat(extractedErrorMessages)
        .contains(
            entry("resourceName", Set.of("Enter the prospect name")),
            entry("onshoreOffshore", Set.of("Select onshore or offshore"))
        );
  }
}
