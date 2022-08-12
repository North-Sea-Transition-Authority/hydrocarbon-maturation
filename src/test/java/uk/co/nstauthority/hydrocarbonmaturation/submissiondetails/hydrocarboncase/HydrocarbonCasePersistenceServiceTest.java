package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInput;

@ExtendWith(MockitoExtension.class)
class HydrocarbonCasePersistenceServiceTest {

  @Mock
  private static HydrocarbonCaseRepository hydrocarbonCaseRepository;
  private static HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    hydrocarbonCasePersistenceService = new HydrocarbonCasePersistenceService(hydrocarbonCaseRepository);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveHydrocarbonCaseDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new HydrocarbonCaseForm();
    form.setOilOnlyCaseType(true);
    form.setOilOnlyProbability(new DecimalInput("100"));
    form.setGasOnlyProbability(new DecimalInput());
    form.setOilAndGasProbability(new DecimalInput());

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
  void getHydrocarbonCaseDetail_verifyMethodCall() {
    hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail);
    verify(hydrocarbonCaseRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}