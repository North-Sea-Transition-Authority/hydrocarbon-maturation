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
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @InjectMocks
  private DrillStemTestInitialFormService drillStemTestInitialFormService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new DrillStemTestInitialForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    drillStemTestInitialFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(drillStemTestInitialFormValidator, times(1))
        .validate(form, bindingResult);
  }

  @Test
  void drillStemTestEntityToForm_newEntity_thenEmptyForm() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(drillStemTestInitialFormService.getDrillStemTestInitialForm(submissionDetail))
        .hasAllNullFieldsOrProperties();
  }

  @Test
  void drillStemTestEntityToForm_existingEntity_thenFilledForm() {
    var drillStem = new DrillStemTest();
    drillStem.setHasDrillStemTestResults(true);
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(drillStem));

    var initialForm = drillStemTestInitialFormService.getDrillStemTestInitialForm(submissionDetail);

    assertThat(initialForm)
        .extracting(DrillStemTestInitialForm::getHasDrillStemTestResult)
        .isEqualTo(true);

    assertThat(initialForm).hasNoNullFieldsOrProperties();
  }
}