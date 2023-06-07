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
class ProspectRelinquishmentServiceTest {

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private ProspectRelinquishmentFormValidator relinquishmentFormValidator;

  @InjectMocks
  private ProspectRelinquishmentService prospectRelinquishmentService;

  @Test
  void createProspectRelinquishment_verifyMethodCall() {
    var form = new ProspectRelinquishmentForm();

    prospectRelinquishmentService.createProspectRelinquishment(form);

    verify(submissionCreateService).createProspectRelinquishment(form);
  }

  @Test
  void createProspectRelinquishment_assertReturn() {
    var form = new ProspectRelinquishmentForm();
    var submissionDetail = mock(SubmissionDetail.class);
    when(prospectRelinquishmentService.createProspectRelinquishment(form))
        .thenReturn(submissionDetail);

    var result = prospectRelinquishmentService.createProspectRelinquishment(form);

    assertThat(result).isEqualTo(submissionDetail);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new ProspectRelinquishmentForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    prospectRelinquishmentService.validate(form, bindingResult);

    verify(relinquishmentFormValidator).validate(form, bindingResult);
  }
}