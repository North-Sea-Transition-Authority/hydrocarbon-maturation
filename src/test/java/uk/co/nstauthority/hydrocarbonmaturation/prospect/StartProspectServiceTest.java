package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceForm;
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class StartProspectServiceTest {

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private UserDetailService userDetailService;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private NewResourceFormValidator newResourceFormValidator;

  @InjectMocks
  private StartProspectService startProspectService;

  private final NewResourceForm form = new NewResourceForm();

  @Test
  void createInitialProspect_verifyMethodCall() {
    startProspectService.createInitialProspect(form);
    verify(submissionCreateService, times(1)).createInitialProspect(form);
  }


  @Test
  void validate_verifyMethodCall() {
    var bindingResult = ReverseRouter.emptyBindingResult();
    var captor = ArgumentCaptor.forClass(NewResourceFormValidatorHints.class);
    when(regulatorTeamService.isRegulatorUser(any())).thenReturn(true);
    startProspectService.validate(form, bindingResult);
    verify(newResourceFormValidator).validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(
            NewResourceFormValidatorHints::getResourceType,
            NewResourceFormValidatorHints::isRegulator)
        .containsExactly(ResourceType.PROSPECT, true);
  }
}