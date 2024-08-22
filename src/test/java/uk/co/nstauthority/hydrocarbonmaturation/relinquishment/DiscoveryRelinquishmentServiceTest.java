package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class DiscoveryRelinquishmentServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private DiscoveryRelinquishmentFormValidator discoveryRelinquishmentFormValidator;

  @InjectMocks
  private DiscoveryRelinquishmentService discoveryRelinquishmentService;

  @Test
  void createDiscoveryRelinquishment_verifyMethodCall() {
    var form = new DiscoveryRelinquishmentForm();

    discoveryRelinquishmentService.createDiscoveryRelinquishment(form, USER);

    verify(submissionCreateService).createDiscoveryRelinquishment(form, USER);
  }

  @Test
  void createDiscoveryRelinquishment_assertReturn() {
    var form = new DiscoveryRelinquishmentForm();
    var submissionDetail = mock(SubmissionDetail.class);
    when(discoveryRelinquishmentService.createDiscoveryRelinquishment(form, USER))
        .thenReturn(submissionDetail);

    var result = discoveryRelinquishmentService.createDiscoveryRelinquishment(form, USER);

    assertThat(result).isEqualTo(submissionDetail);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new DiscoveryRelinquishmentForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryRelinquishmentService.validate(form, bindingResult);

    verify(discoveryRelinquishmentFormValidator).validate(form, bindingResult);
  }
}