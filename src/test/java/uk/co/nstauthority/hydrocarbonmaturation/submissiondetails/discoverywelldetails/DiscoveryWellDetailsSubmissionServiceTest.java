package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DiscoveryWellDetailsSubmissionServiceTest {

  @Mock
  private DiscoveryWellDetailsFormService discoveryWellDetailsFormService;

  @InjectMocks
  private DiscoveryWellDetailsSubmissionService discoveryWellDetailsSubmissionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void isSectionSubmittable_whenNoValidationErrors_assertTrue() {
    var form = new DiscoveryWellDetailsForm();
    when(discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail)).thenReturn(form);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(discoveryWellDetailsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail))
        .thenReturn(bindingResult);

    assertTrue(discoveryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenValidationErrors_assertFalse() {
    var form = new DiscoveryWellDetailsForm();
    when(discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail)).thenReturn(form);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.reject("testerror");
    when(discoveryWellDetailsFormService.validate(eq(form), any(), eq(ValidationType.FULL), eq(submissionDetail)))
        .thenReturn(bindingResult);

    assertFalse(discoveryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail));
  }
}