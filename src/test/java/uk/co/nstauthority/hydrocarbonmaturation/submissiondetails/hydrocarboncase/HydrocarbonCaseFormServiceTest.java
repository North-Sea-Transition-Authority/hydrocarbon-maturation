package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class HydrocarbonCaseFormServiceTest {

  private static HydrocarbonCaseFormValidator hydrocarbonCaseFormValidator;
  private static HydrocarbonCaseFormService hydrocarbonCaseFormService;
  private static HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    hydrocarbonCaseFormValidator = mock(HydrocarbonCaseFormValidator.class);
    hydrocarbonCasePersistenceService = mock(HydrocarbonCasePersistenceService.class);
    hydrocarbonCaseFormService = new HydrocarbonCaseFormService(hydrocarbonCaseFormValidator, hydrocarbonCasePersistenceService);

    submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new HydrocarbonCaseForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    hydrocarbonCaseFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(hydrocarbonCaseFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue().getValidationType())
        .isEqualTo(ValidationType.FULL);
  }

  @Test
  void sourceAndSealEntityToForm_newEntity_thenEmptyForm() {
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail)).thenReturn(Optional.empty());

    assertThat(hydrocarbonCaseFormService.getHydrocarbonCaseForm(submissionDetail))
        .extracting(
          HydrocarbonCaseForm::getGasOnlyCaseType,
          HydrocarbonCaseForm::getGasOnlyComments,
          HydrocarbonCaseForm::getGasOnlyProbability,
          HydrocarbonCaseForm::getOilOnlyCaseType,
          HydrocarbonCaseForm::getOilOnlyComments,
          HydrocarbonCaseForm::getOilOnlyProbability,
          HydrocarbonCaseForm::getOilAndGasCaseType,
          HydrocarbonCaseForm::getOilAndGasComments,
          HydrocarbonCaseForm::getOilAndGasProbability
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void hydrocarbonCaseEntityToForm_existingEntity_thenPopulatedForm() {
    var hydrocarbonCaseDetail = getHydrocarbonCaseDetail(submissionDetail);
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail)).thenReturn(Optional.of(hydrocarbonCaseDetail));

    assertThat(hydrocarbonCaseFormService.getHydrocarbonCaseForm(submissionDetail))
        .extracting(
            HydrocarbonCaseForm::getGasOnlyCaseType,
            HydrocarbonCaseForm::getGasOnlyComments,
            HydrocarbonCaseForm::getGasOnlyProbability,
            HydrocarbonCaseForm::getOilOnlyCaseType,
            HydrocarbonCaseForm::getOilOnlyComments,
            (form) -> form.getOilOnlyProbability().createBigDecimalOrNull(),
            HydrocarbonCaseForm::getOilAndGasCaseType,
            HydrocarbonCaseForm::getOilAndGasComments,
            HydrocarbonCaseForm::getOilAndGasProbability
        )
        .containsExactly(
            hydrocarbonCaseDetail.getGasOnly(),
            hydrocarbonCaseDetail.getGasOnlyComments(),
            hydrocarbonCaseDetail.getGasOnlyProbability(),
            hydrocarbonCaseDetail.getOilOnly(),
            hydrocarbonCaseDetail.getOilOnlyComments(),
            hydrocarbonCaseDetail.getOilOnlyProbability(),
            hydrocarbonCaseDetail.getOilAndGas(),
            hydrocarbonCaseDetail.getOilAndGasComments(),
            hydrocarbonCaseDetail.getOilAndGasProbability()
        );
  }

  private HydrocarbonCaseDetail getHydrocarbonCaseDetail(SubmissionDetail submissionDetail) {
    var hydrocarbonCaseDetail =  new HydrocarbonCaseDetail();
    hydrocarbonCaseDetail.setSubmissionDetail(submissionDetail);
    hydrocarbonCaseDetail.setOilOnly(true);
    hydrocarbonCaseDetail.setOilOnlyComments("comment");
    hydrocarbonCaseDetail.setOilOnlyProbability(BigDecimal.valueOf(100));
    hydrocarbonCaseDetail.setGasOnly(false);
    hydrocarbonCaseDetail.setOilAndGas(false);

    return hydrocarbonCaseDetail;
  }
}