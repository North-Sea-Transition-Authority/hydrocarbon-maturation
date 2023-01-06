package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;

@ExtendWith(MockitoExtension.class)
class StartDiscoveryServiceTest {

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private StartDiscoveryFormValidator startDiscoveryFormValidator;

  @InjectMocks
  private StartDiscoveryService startDiscoveryService;

  private StartDiscoveryForm startDiscoveryForm;

  @BeforeEach
  void setUp() {
    startDiscoveryForm = new StartDiscoveryForm();
  }

  @Test
  void createNewDiscoveryFromProspect_verifyCreatedDiscovery() {
    startDiscoveryService.createNewDiscoveryFromProspect(startDiscoveryForm);

    verify(submissionCreateService, times(1)).createNewDiscoveryFromProspect(startDiscoveryForm);
  }

  @Test
  void createNewDiscovery_verifyCreatedDiscovery() {
    startDiscoveryService.createNewDiscovery();

    verify(submissionCreateService, times(1)).createDiscovery();
  }

  @Test
  void validate() {
    var bindingResult = startDiscoveryService.validate(startDiscoveryForm, emptyBindingResult());
    assertThat(bindingResult).isEqualTo(emptyBindingResult());
    verify(startDiscoveryFormValidator, times(1)).validate(startDiscoveryForm, bindingResult);
  }
}