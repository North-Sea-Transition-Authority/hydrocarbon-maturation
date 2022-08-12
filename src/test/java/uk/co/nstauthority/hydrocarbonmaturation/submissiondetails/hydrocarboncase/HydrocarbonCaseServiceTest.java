package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInput;

@ExtendWith(MockitoExtension.class)
class HydrocarbonCaseServiceTest {

  private static HydrocarbonCaseFormValidator hydrocarbonCaseFormValidator;
  private static HydrocarbonCaseService hydrocarbonCaseService;
  private static HydrocarbonCaseRepository hydrocarbonCaseRepository;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    hydrocarbonCaseFormValidator = mock(HydrocarbonCaseFormValidator.class);
    hydrocarbonCaseRepository = mock(HydrocarbonCaseRepository.class);
    hydrocarbonCaseService = new HydrocarbonCaseService(hydrocarbonCaseFormValidator, hydrocarbonCaseRepository);

    submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new HydrocarbonCaseForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    hydrocarbonCaseService.validate(form, bindingResult);
    verify(hydrocarbonCaseFormValidator, times(1)).validate(form, bindingResult);
  }

  @Test
  void saveHydrocarbonCaseDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new HydrocarbonCaseForm();
    form.setOilOnlyCaseType(true);
    form.setOilOnlyProbability(new DecimalInput("100"));
    form.setGasOnlyProbability(new DecimalInput());
    form.setOilAndGasProbability(new DecimalInput());

    var hydrocarbonCaseArgumentCaptor = ArgumentCaptor.forClass(HydrocarbonCaseDetail.class);

    hydrocarbonCaseService.saveHydrocarbonCase(form, submissionDetail);

    verify(hydrocarbonCaseRepository, times(1)).save(hydrocarbonCaseArgumentCaptor.capture());
    var savedHydrocarbonCase = (HydrocarbonCaseDetail) hydrocarbonCaseArgumentCaptor.getValue();

    assertThat(savedHydrocarbonCase)
        .extracting(
            HydrocarbonCaseDetail::getOilOnly,
            HydrocarbonCaseDetail::getGasOnly,
            HydrocarbonCaseDetail::getOilAndGas,
            HydrocarbonCaseDetail::getGasOnlyProbability,
            HydrocarbonCaseDetail::getOilOnlyProbability,
            HydrocarbonCaseDetail::getOilAndGasProbability,
            HydrocarbonCaseDetail::getGasOnlyComments,
            HydrocarbonCaseDetail::getOilAndGasComments,
            HydrocarbonCaseDetail::getOilOnlyComments
        )
        .containsExactly(
            form.getOilOnlyCaseType(),
            form.getGasOnlyCaseType(),
            form.getOilAndGasCaseType(),
            form.getGasOnlyProbability().createBigDecimalOrNull(),
            form.getOilOnlyProbability().createBigDecimalOrNull(),
            form.getOilAndGasProbability().createBigDecimalOrNull(),
            form.getGasOnlyComments(),
            form.getOilAndGasComments(),
            form.getOilOnlyComments()
        );
  }
}