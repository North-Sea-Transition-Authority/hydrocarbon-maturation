package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class DrillStemTestResultFormServiceTest {

  @Mock
  private DrillStemTestResultFormValidator drillStemTestResultFormValidator;

  @InjectMocks
  private DrillStemTestResultFormService drillStemTestResultFormService;

  private DrillStemTest drillStemTest;

  @BeforeEach
  void setUp() {
    drillStemTest = new DrillStemTest();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    drillStemTest.setId(UUID.randomUUID());
    drillStemTest.setReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new DrillStemTestResultForm();
    var bindingResult = ReverseRouter.emptyBindingResult();

    var captor = ArgumentCaptor.forClass(
        DrillStemTestResultFormValidatorHints.class);

    drillStemTestResultFormService.validate(form, bindingResult, List.of("1", "2"));
    verify(drillStemTestResultFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());
    assertThat(captor.getValue())
        .extracting(
            DrillStemTestResultFormValidatorHints::getDrillStemTestTestNumbers)
        .isEqualTo(List.of("1", "2"));
  }

  @Test
  void getDrillStemTestResultForm_ExistingEntity_filledForm() {
    var drillStemTestResult = getDrillStemTestResult(drillStemTest);

    var form = drillStemTestResultFormService.getDrillStemTestResultForm(drillStemTestResult);

    assertThat(form)
        .extracting(
            DrillStemTestResultForm::getCommentsInterpretation,
            theForm -> theForm.getBhfp().createIntegerOrNull(),
            theForm -> theForm.getBottomDepth().createBigDecimalOrNull(),
            theForm -> theForm.getFlowDuration().createBigDecimalOrNull(),
            DrillStemTestResultForm::getFormationName,
            theForm -> theForm.getGasFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getTopDepth().createBigDecimalOrNull(),
            theForm -> theForm.getOilFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getWaterFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getWhfp().createIntegerOrNull(),
            DrillStemTestResultForm::getTestNumber)
        .containsExactly(
            "comment",
            drillStemTestResult.getBhfp(),
            drillStemTestResult.getBottomDepth(),
            drillStemTestResult.getFlowDuration(),
            drillStemTestResult.getFormationName(),
            drillStemTestResult.getGasFlowrate(),
            drillStemTestResult.getTopDepth(),
            drillStemTestResult.getOilFlowrate(),
            drillStemTestResult.getWaterFlowrate(),
            drillStemTestResult.getWhfp(),
            drillStemTestResult.getTestNumber()
        );
  }

  @Test
  void getDrillStemTestResultForm_ExistingEmptyEntity_filledForm() {
    var drillStemTestResult = new DrillStemTestResult();

    var form = drillStemTestResultFormService.getDrillStemTestResultForm(drillStemTestResult);

    assertThat(form)
        .extracting(
            DrillStemTestResultForm::getCommentsInterpretation,
            theForm -> theForm.getBhfp().createIntegerOrNull(),
            theForm -> theForm.getBottomDepth().createBigDecimalOrNull(),
            theForm -> theForm.getFlowDuration().createBigDecimalOrNull(),
            DrillStemTestResultForm::getFormationName,
            theForm -> theForm.getGasFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getTopDepth().createBigDecimalOrNull(),
            theForm -> theForm.getOilFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getWaterFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getWhfp().createIntegerOrNull(),
            DrillStemTestResultForm::getTestNumber)
        .containsOnlyNulls();
  }

  @Test
  void getDrillStemTestResultForm_newEntity_emptyForm() {

    var form = drillStemTestResultFormService.getDrillStemTestResultForm(new DrillStemTestResult());

    assertThat(form)
        .extracting(
            DrillStemTestResultForm::getCommentsInterpretation,
            theForm -> theForm.getBhfp().createIntegerOrNull(),
            theForm -> theForm.getBottomDepth().createBigDecimalOrNull(),
            theForm -> theForm.getFlowDuration().createBigDecimalOrNull(),
            DrillStemTestResultForm::getFormationName,
            theForm -> theForm.getGasFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getTopDepth().createBigDecimalOrNull(),
            theForm -> theForm.getOilFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getWaterFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getWhfp().createIntegerOrNull(),
            DrillStemTestResultForm::getTestNumber)
        .containsOnlyNulls();
  }

  @Test
  void validateDrillStemTestResults_whenAllValid() {
    var drillStemTest = mock(DrillStemTest.class);
    var drillStemTestResult = getDrillStemTestResult(drillStemTest);
    var drillStemTestResult1 = getDrillStemTestResult(drillStemTest);
    drillStemTestResult1.setTestNumber("2a");
    drillStemTestResult1.setFormationName("otherName");
    var captor = ArgumentCaptor.forClass(DrillStemTestResultForm.class);
    var hintsCaptor = ArgumentCaptor
        .forClass(DrillStemTestResultFormValidatorHints.class);
    var hasErrors = drillStemTestResultFormService
        .validateDrillStemTestResults(List.of(drillStemTestResult1, drillStemTestResult));

    assertThat(hasErrors).isFalse();
    verify(drillStemTestResultFormValidator, times(2))
        .validate(captor.capture(), any(), hintsCaptor.capture());

    var values = captor.getAllValues();

    assertThat(values.get(1))
        .extracting(
            DrillStemTestResultForm::getCommentsInterpretation,
            theForm -> theForm.getBhfp().createIntegerOrNull(),
            theForm -> theForm.getBottomDepth().createBigDecimalOrNull(),
            theForm -> theForm.getFlowDuration().createBigDecimalOrNull(),
            DrillStemTestResultForm::getFormationName,
            theForm -> theForm.getGasFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getTopDepth().createBigDecimalOrNull(),
            theForm -> theForm.getOilFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getWaterFlowrate().createBigDecimalOrNull(),
            theForm -> theForm.getWhfp().createIntegerOrNull(),
            DrillStemTestResultForm::getTestNumber)
        .containsExactly(
            "comment",
            drillStemTestResult.getBhfp(),
            drillStemTestResult.getBottomDepth(),
            drillStemTestResult.getFlowDuration(),
            drillStemTestResult.getFormationName(),
            drillStemTestResult.getGasFlowrate(),
            drillStemTestResult.getTopDepth(),
            drillStemTestResult.getOilFlowrate(),
            drillStemTestResult.getWaterFlowrate(),
            drillStemTestResult.getWhfp(),
            drillStemTestResult.getTestNumber()
        );

    assertThat(values.get(0))
        .extracting(
            DrillStemTestResultForm::getFormationName)
        .isEqualTo("otherName");
    assertThat(hintsCaptor.getAllValues())
        .extracting(DrillStemTestResultFormValidatorHints::getDrillStemTestTestNumbers)
        .containsExactly(List.of(), List.of());
  }

  @Test
  void validateForUpdate_assertRemovesNumberBeingUpdated() {
    var form = new DrillStemTestResultForm();
    var bindingResult = ReverseRouter.emptyBindingResult();

    var captor = ArgumentCaptor.forClass(
        DrillStemTestResultFormValidatorHints.class);
    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setTestNumber("1");
    var drillStemTestResult2 = new DrillStemTestResult();
    drillStemTestResult2.setTestNumber("2");

    drillStemTestResultFormService.validateForUpdate(form, bindingResult, List.of(drillStemTestResult, drillStemTestResult2), "1");

    verify(drillStemTestResultFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(
            DrillStemTestResultFormValidatorHints::getDrillStemTestTestNumbers)
        .isEqualTo(
            List.of("2"));
  }

  @Test
  void getDrillStemTestResultNumbers() {
    var drillStemTestResult = getDrillStemTestResult(drillStemTest);
    drillStemTestResult.setTestNumber("1");
    var drillStemTestResult2 = getDrillStemTestResult(drillStemTest);
    drillStemTestResult2.setTestNumber("2");
    var resultsNumbers = drillStemTestResultFormService.getDrillStemTestResultNumbers(
        List.of(drillStemTestResult, drillStemTestResult2));

    assertThat(resultsNumbers)
        .isEqualTo(
            List.of(
                drillStemTestResult.getTestNumber(),
                drillStemTestResult2.getTestNumber()));
  }

  public static DrillStemTestResult getDrillStemTestResult(DrillStemTest drillStemTest) {
    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setId(UUID.randomUUID());
    drillStemTestResult.setDrillStemTest(drillStemTest);
    drillStemTestResult.setFormationName("formation");
    drillStemTestResult.setBhfp(1);
    drillStemTestResult.setBottomDepth(BigDecimal.TEN);
    drillStemTestResult.setWhfp(2);
    drillStemTestResult.setCommentsInterpretation("comment");
    drillStemTestResult.setFlowDuration(BigDecimal.TEN);
    drillStemTestResult.setGasFlowrate(BigDecimal.TEN);
    drillStemTestResult.setOilFlowrate(BigDecimal.TEN);
    drillStemTestResult.setTopDepth(BigDecimal.ONE);
    drillStemTestResult.setWaterFlowrate(BigDecimal.valueOf(5));
    drillStemTestResult.setTestNumber("1a");

    return drillStemTestResult;
  }
}