package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycombinedcasevolumetrictotals;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedVolTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DiscoveryCombinedVolTotalsSubmissionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService;

  @Mock
  private DiscoveryCombinedCaseVolTotalsFormValidator discoveryCombinedCaseVolTotalsFormValidator;

  @InjectMocks
  private DiscoveryCombinedVolTotalsSubmissionService discoveryCombinedVolTotalsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = new MixedCaseOutputsForm();

    when(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(discoveryCombinedCaseVolTotalsFormValidator.isValid(
        eq(form), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(true);

    assertTrue(discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var form = new MixedCaseOutputsForm();

    when(discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(discoveryCombinedCaseVolTotalsFormValidator.isValid(
        eq(form), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(false);

    assertFalse(discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }
}
