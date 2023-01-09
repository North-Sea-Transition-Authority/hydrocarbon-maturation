package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class HydrocarbonCasePersistenceServiceTest {

  @Mock
  private static HydrocarbonCaseRepository hydrocarbonCaseRepository;
  private static HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  private static SubmissionDetail submissionDetail;

  private HydrocarbonCaseDetail hydrocarbonCase;

  @BeforeEach
  void setup() {
    hydrocarbonCasePersistenceService = new HydrocarbonCasePersistenceService(hydrocarbonCaseRepository);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
    hydrocarbonCase = new HydrocarbonCaseDetail();
  }

  @Test
  void saveHydrocarbonCaseDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new HydrocarbonCaseForm();
    form.setOilOnlyCaseType(true);
    form.setGasOnlyCaseType(true);
    form.setOilAndGasCaseType(true);

    form.setOilOnlyProbability(new DecimalInput("100"));
    form.setGasOnlyProbability(new DecimalInput("20"));
    form.setOilAndGasProbability(new DecimalInput("30"));

    var hydrocarbonCaseArgumentCaptor = ArgumentCaptor.forClass(HydrocarbonCaseDetail.class);

    hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);

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

  @Test
  void saveHydrocarbonCaseDetails_givenEmptyForm_verifyCreatedEntity() {
    var form = new HydrocarbonCaseForm();

    var hydrocarbonCaseArgumentCaptor = ArgumentCaptor.forClass(HydrocarbonCaseDetail.class);

    hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);

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
  void getHydrocarbonCaseDetail_verifyMethodCall() {
    hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail);
    verify(hydrocarbonCaseRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  @Test
  void isGasOnlyPresent_whenGasOnly_isTrue() {
    hydrocarbonCase.setGasOnly(true);
    assertTrue(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isGasOnlyPresent_whenOilAndGas_isFalse() {
    hydrocarbonCase.setOilAndGas(true);
    hydrocarbonCase.setOilAndGasProbability(BigDecimal.valueOf(1.5));
    assertFalse(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isGasOnlyPresent_whenNoGas_isFalse() {
    assertFalse(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isMixedPresent_whenGasAndOil_isTrue() {
    hydrocarbonCase.setOilAndGas(true);
    assertTrue(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase));
  }

  @Test
  void isMixedPresent_whenOilOnlyAndGasOnly_isFalse() {
    hydrocarbonCase.setOilOnly(true);
    hydrocarbonCase.setGasOnly(true);
    assertFalse(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase));
  }

  @Test
  void isOilOnlyPresent_whenGasOnly_isTrue() {
    hydrocarbonCase.setOilOnly(true);
    assertTrue(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isOilOnlyPresent_whenOilAndGas_isFalse() {
    hydrocarbonCase.setOilAndGas(true);
    assertFalse(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isOilOnlyPresent_whenNoGas_isFalse() {
    assertFalse(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase));
  }
}