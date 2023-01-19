package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
class RelinquishmentServiceTest {

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private RelinquishmentFormValidator relinquishmentFormValidator;

  @InjectMocks
  private RelinquishmentService relinquishmentService;

  @Test
  void createRelinquishment_verifyMethodCall() {
    var form = new RelinquishmentForm();

    relinquishmentService.createRelinquishment(form);

    verify(submissionCreateService).createRelinquishment(form);
  }

  @Test
  void createRelinquishment_assertReturn() {
    var form = new RelinquishmentForm();
    var submissionDetail = mock(SubmissionDetail.class);
    when(relinquishmentService.createRelinquishment(form))
        .thenReturn(submissionDetail);

    var result = relinquishmentService.createRelinquishment(form);

    assertThat(result).isEqualTo(submissionDetail);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new RelinquishmentForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    relinquishmentService.validate(form, bindingResult);

    verify(relinquishmentFormValidator).validate(form, bindingResult);
  }
}