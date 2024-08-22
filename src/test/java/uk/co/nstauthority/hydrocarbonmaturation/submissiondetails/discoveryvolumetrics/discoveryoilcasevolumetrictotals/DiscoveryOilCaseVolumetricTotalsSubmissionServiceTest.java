package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DiscoveryOilCaseVolumetricTotalsSubmissionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private DiscoveryOilCaseVolumetricTotalsService discoveryOilCaseVolumetricTotalsService;

  @Mock
  private DiscoveryOilCaseVolumetricTotalsFormValidator discoveryOilCaseVolumetricTotalsFormValidator;

  @InjectMocks
  private DiscoveryOilCaseVolumetricTotalsSubmissionService discoveryOilCaseVolumetricTotalsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = new OilCaseOutputsForm();

    when(discoveryOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(discoveryOilCaseVolumetricTotalsFormValidator.isValid(
        eq(form), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(true);

    assertTrue(discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = new OilCaseOutputsForm();

    when(discoveryOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(discoveryOilCaseVolumetricTotalsFormValidator.isValid(
        eq(form), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(false);

    assertFalse(discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }
}
