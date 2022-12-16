package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
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
class DrillStemTestInitialFormServiceTest {

  @Mock
  private DrillStemTestInitialFormValidator drillStemTestInitialFormValidator;

  @Mock
  private DrillStemTestSummaryFormValidator drillStemTestSummaryFormValidator;

  @Mock
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @InjectMocks
  private DrillStemTestFormService drillStemTestFormService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new DrillStemTestInitialForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(drillStemTestInitialFormValidator, times(1))
        .validate(form, bindingResult);
  }

  @Test
  void validateSummaryForm_verifyMethodCall() {
    var form = new DrillStemTestSummaryForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(drillStemTestSummaryFormValidator, times(1))
        .validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void validateDrillStemTestInitialForm_verifyMethodCall() {
    var form = new DrillStemTestInitialForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestFormService.validateDrillStemTestInitialForm(form, bindingResult);
    verify(drillStemTestInitialFormValidator, times(1))
        .validate(form, bindingResult);
  }

  @Test
  void mapDrillStemTestEntityToForm_newEntity_thenEmptyForm() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(drillStemTestFormService.getDrillStemTestInitialForm(submissionDetail))
        .hasAllNullFieldsOrProperties();
  }

  @Test
  void mapDrillStemTestEntityToForm_existingEntity_thenFilledForm() {
    var drillStem = new DrillStemTest();
    drillStem.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.YES_NOW);
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(drillStem));

    var initialForm = drillStemTestFormService.getDrillStemTestInitialForm(submissionDetail);

    assertThat(initialForm)
        .hasAllNullFieldsOrProperties();
  }

  @Test
  void getDrillStemTestSummaryForm_existingEntityAndNoOption_thenFilledForm() {
    var drillStemTest = new DrillStemTest();
    drillStemTest.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.NO);
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    var result = drillStemTestFormService.getDrillStemTestSummaryForm(submissionDetail);

    assertThat(result.getDrillStemTestSummaryOption()).isEqualTo(DrillStemTestSummaryOptions.NO);
  }

  @Test
  void getDrillStemTestSummaryForm_existingEntityAndYesOption_thenEmptyForm() {
    var drillStemTest = new DrillStemTest();
    drillStemTest.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.YES_NOW);
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    var result = drillStemTestFormService.getDrillStemTestSummaryForm(submissionDetail);

    assertThat(result.getDrillStemTestSummaryOption()).isEqualTo(DrillStemTestSummaryOptions.YES_NOW);
  }

  @Test
  void getDrillStemTestSummaryForm_missingEntity_thenEmptyForm() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var result = drillStemTestFormService.getDrillStemTestSummaryForm(submissionDetail);

    assertThat(result).hasAllNullFieldsOrProperties();
  }
}