package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DrillStemTestSubmissionServiceTest {

  @Mock
  private DrillStemTestFormService dryWellDetailsFormService;

  @Mock
  private DrillStemTestResultFormService drillStemTestResultFormService;

  @Mock
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @InjectMocks
  private DrillStemTestSubmissionService drillStemTestSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

    var form = new DrillStemTestInitialForm();
    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(dryWellDetailsFormService.getDrillStemTestInitialForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(dryWellDetailsFormService.validate(
        eq(form), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail)))
        .thenReturn(emptyBindingResult);

    assertTrue(drillStemTestSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

    var form = new DrillStemTestInitialForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new ObjectError("", ""));

    when(dryWellDetailsFormService.getDrillStemTestInitialForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(dryWellDetailsFormService.validate(
        eq(form), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail)))
        .thenReturn(bindingResult);

    assertFalse(drillStemTestSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_verifyErrorInDrillStemTestResult() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

    var form = new DrillStemTestInitialForm();
    var emptyBindingResult = ReverseRouter.emptyBindingResult();
    var drillStemTestResults = List.of(new DrillStemTestResult());

    when(dryWellDetailsFormService.getDrillStemTestInitialForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(dryWellDetailsFormService.validate(
        eq(form), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail)))
        .thenReturn(emptyBindingResult);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(drillStemTestResults);
    when(drillStemTestResultFormService.validateDrillStemTestResults(
        drillStemTestResults))
        .thenReturn(true);

    assertFalse(drillStemTestSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_verifyDrillStemTestResultsValidated() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

    var form = new DrillStemTestInitialForm();
    var emptyBindingResult = ReverseRouter.emptyBindingResult();
    var drillStemTestResults = List.of(new DrillStemTestResult());

    when(dryWellDetailsFormService.getDrillStemTestInitialForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(dryWellDetailsFormService.validate(
        eq(form), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail)))
        .thenReturn(emptyBindingResult);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(drillStemTestResults);
    when(drillStemTestResultFormService.validateDrillStemTestResults(
        drillStemTestResults))
        .thenReturn(false);

    assertTrue(drillStemTestSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}