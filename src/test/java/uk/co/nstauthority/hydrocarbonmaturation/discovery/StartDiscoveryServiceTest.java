package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceForm;
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class StartDiscoveryServiceTest {

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private StartDiscoveryFormValidator startDiscoveryFormValidator;

  @Mock
  private NewResourceFormValidator newResourceFormValidator;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private UserDetailService userDetailService;

  @InjectMocks
  private StartDiscoveryService startDiscoveryService;

  private StartDiscoveryForm startDiscoveryForm;
  private NewResourceForm newResourceForm;

  @BeforeEach
  void setUp() {
    startDiscoveryForm = new StartDiscoveryForm();
    newResourceForm = new NewResourceForm();
  }

  @Test
  void createNewDiscoveryFromProspect_verifyCreatedDiscovery() {
    startDiscoveryService.createNewDiscoveryFromProspect(startDiscoveryForm);

    verify(submissionCreateService, times(1)).createNewDiscoveryFromProspect(startDiscoveryForm);
  }

  @Test
  void createNewDiscovery_verifyCreatedDiscovery() {
    startDiscoveryService.createNewDiscovery(newResourceForm);

    verify(submissionCreateService, times(1)).createDiscovery(newResourceForm);
  }

  @Test
  void validateDiscoveryFromProspect() {
    var bindingResult = startDiscoveryService
        .validateDiscoveryFromProspect(startDiscoveryForm, emptyBindingResult());
    assertThat(bindingResult).isEqualTo(emptyBindingResult());
    verify(startDiscoveryFormValidator, times(1)).validate(startDiscoveryForm, bindingResult);
  }

  @Test
  void validateNewDiscovery() {
    var captor = ArgumentCaptor.forClass(NewResourceFormValidatorHints.class);
    when(regulatorTeamService.isRegulatorUser(any())).thenReturn(false);
    var bindingResult = startDiscoveryService
        .validateNewDiscovery(newResourceForm, emptyBindingResult());
    assertThat(bindingResult).isEqualTo(emptyBindingResult());
    verify(newResourceFormValidator, times(1))
        .validate(eq(newResourceForm), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(
            NewResourceFormValidatorHints::getResourceType,
            NewResourceFormValidatorHints::isRegulator)
        .containsExactly(ResourceType.DISCOVERY, false);
  }
}