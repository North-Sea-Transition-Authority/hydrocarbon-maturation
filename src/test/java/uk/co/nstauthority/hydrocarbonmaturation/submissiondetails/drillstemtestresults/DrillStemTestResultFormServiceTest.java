package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

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
    drillStemTest.setId(UUID.randomUUID());
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new DrillStemTestResultForm();
    var bindingResult = ReverseRouter.emptyBindingResult();
    drillStemTestResultFormService.validate(form, bindingResult, ValidationType.FULL);

    verify(drillStemTestResultFormValidator, times(1))
        .validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void getDrillStemTestResultForm_ExistingEntity_filledForm() {
    var drillStemTestResult = getDrillStemTestResult();

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
            theForm -> theForm.getWhfp().createIntegerOrNull())
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
            drillStemTestResult.getWhfp()
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
            theForm -> theForm.getWhfp().createIntegerOrNull())
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
            theForm -> theForm.getWhfp().createIntegerOrNull())
        .containsOnlyNulls();
  }

  private DrillStemTestResult getDrillStemTestResult() {
    var drillStemTestResult = new DrillStemTestResult();

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

    return drillStemTestResult;
  }
}