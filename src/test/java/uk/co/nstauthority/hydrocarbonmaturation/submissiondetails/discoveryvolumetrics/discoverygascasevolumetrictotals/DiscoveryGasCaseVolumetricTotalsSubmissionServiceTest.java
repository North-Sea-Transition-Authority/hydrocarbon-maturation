package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DiscoveryGasCaseVolumetricTotalsSubmissionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService;

  @Mock
  private DiscoveryGasCaseVolumetricTotalsFormValidator discoveryGasCaseVolumetricTotalsFormValidator;

  @InjectMocks
  private DiscoveryGasCaseVolumetricTotalsSubmissionService discoveryGasCaseVolumetricTotalsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = new GasCaseOutputsForm();

    when(discoveryGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(discoveryGasCaseVolumetricTotalsFormValidator.isValid(
        eq(form), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(true);

    assertTrue(discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = new GasCaseOutputsForm();

    when(discoveryGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(discoveryGasCaseVolumetricTotalsFormValidator.isValid(
        eq(form), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(false);

    assertFalse(discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }
}
