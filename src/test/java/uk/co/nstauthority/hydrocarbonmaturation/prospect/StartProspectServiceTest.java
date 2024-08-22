package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class StartProspectServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private ResourceDetailsFormValidator resourceDetailsFormValidator;

  @Mock
  private TeamQueryService teamQueryService;

  @InjectMocks
  private StartProspectService startProspectService;

  private final ResourceDetailsForm form = new ResourceDetailsForm();

  @Test
  void createInitialProspect_verifyMethodCall() {
    startProspectService.createInitialProspect(form, USER);
    verify(submissionCreateService, times(1)).createInitialProspect(form, USER);
  }


  @Test
  void validate_verifyMethodCall() {
    var bindingResult = ReverseRouter.emptyBindingResult();
    var captor = ArgumentCaptor.forClass(ResourceDetailsFormValidatorHints.class);
    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    startProspectService.validate(form, bindingResult, USER);
    verify(resourceDetailsFormValidator).validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ResourceDetailsFormValidatorHints::resourceType,
            ResourceDetailsFormValidatorHints::isRegulator)
        .containsExactly(ResourceType.PROSPECT, true);
  }
}