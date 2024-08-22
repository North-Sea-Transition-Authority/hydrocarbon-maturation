package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;

@ExtendWith(MockitoExtension.class)
class UpdateDiscoveryServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private UpdateDiscoveryFormValidator updateDiscoveryFormValidator;

  @InjectMocks
  private UpdateDiscoveryService updateDiscoveryService;

  @Test
  void createUpdateDiscovery_verifyMethodCall() {
    var form = new UpdateDiscoveryForm();

    updateDiscoveryService.createUpdateDiscovery(form, USER);
    verify(submissionCreateService, times(1)).createUpdateDiscovery(form, USER);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new UpdateDiscoveryForm();
    var bindingResult = ReverseRouter.emptyBindingResult();
    updateDiscoveryService.validate(form, bindingResult);
    verify(updateDiscoveryFormValidator, times(1)).validate(form, bindingResult);
  }
}