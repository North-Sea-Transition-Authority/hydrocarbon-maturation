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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class DiscoveryRelinquishmentServiceTest {

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private DiscoveryRelinquishmentFormValidator discoveryRelinquishmentFormValidator;

  @InjectMocks
  private DiscoveryRelinquishmentService discoveryRelinquishmentService;

  @Test
  void createDiscoveryRelinquishment_verifyMethodCall() {
    var form = new DiscoveryRelinquishmentForm();

    discoveryRelinquishmentService.createDiscoveryRelinquishment(form);

    verify(submissionCreateService).createDiscoveryRelinquishment(form);
  }

  @Test
  void createDiscoveryRelinquishment_assertReturn() {
    var form = new DiscoveryRelinquishmentForm();
    var submissionDetail = mock(SubmissionDetail.class);
    when(discoveryRelinquishmentService.createDiscoveryRelinquishment(form))
        .thenReturn(submissionDetail);

    var result = discoveryRelinquishmentService.createDiscoveryRelinquishment(form);

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