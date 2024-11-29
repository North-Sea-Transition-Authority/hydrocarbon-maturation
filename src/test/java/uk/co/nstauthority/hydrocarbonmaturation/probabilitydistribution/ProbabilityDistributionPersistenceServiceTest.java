package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class ProbabilityDistributionPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionRepository probabilityDistributionRepository;

  @InjectMocks
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  
  @Captor
  private ArgumentCaptor<ProbabilityDistribution> probabilityDistributionArgumentCaptor;

  @Test
  void saveProbabilityDistributionWhenNotNull() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(probabilityDistributionRepository.findBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance))
        .thenReturn(Optional.empty());

    var probabilityDistributionForm = getProbabilityDistributionForm();
    
    probabilityDistributionPersistenceService
        .saveProbabilityDistributionWhenNotNull(probabilityDistributionFieldInstance, probabilityDistributionForm, submissionDetail);

    verify(probabilityDistributionRepository).save(probabilityDistributionArgumentCaptor.capture());
    assertThat(probabilityDistributionArgumentCaptor.getValue())
        .extracting(
            ProbabilityDistribution::getSubmissionDetail,
            ProbabilityDistribution::getReservoirSubmissionDetail,
            ProbabilityDistribution::getDistributionType,
            ProbabilityDistribution::getInstance,
            ProbabilityDistribution::getMinValue,
            ProbabilityDistribution::getP99,
            ProbabilityDistribution::getP90,
            ProbabilityDistribution::getModeValue,
            ProbabilityDistribution::getP50,
            ProbabilityDistribution::getMeanValue,
            ProbabilityDistribution::getP10,
            ProbabilityDistribution::getP1,
            ProbabilityDistribution::getMaxValue,
            ProbabilityDistribution::getConstantValue,
            ProbabilityDistribution::getComments
        )
        .containsExactly(
            submissionDetail,
            null,
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionFieldInstance,
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getMin().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP99().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP90().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getMode().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP50().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getMean().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP10().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP1().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getMax().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getConstant().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getComments()
        );
  }

  @Test
  void saveProbabilityDistributionWhenNotNull_reservoirSubmissionDetail() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

    when(probabilityDistributionRepository.findByReservoirSubmissionDetailAndInstance(reservoirSubmissionDetail, probabilityDistributionFieldInstance))
        .thenReturn(Optional.empty());

    var probabilityDistributionForm = getProbabilityDistributionForm();

    probabilityDistributionPersistenceService
        .saveProbabilityDistributionWhenNotNull(probabilityDistributionFieldInstance, probabilityDistributionForm,
            submissionDetail, reservoirSubmissionDetail);

    verify(probabilityDistributionRepository).save(probabilityDistributionArgumentCaptor.capture());
    assertThat(probabilityDistributionArgumentCaptor.getValue())
        .extracting(
            ProbabilityDistribution::getSubmissionDetail,
            ProbabilityDistribution::getReservoirSubmissionDetail,
            ProbabilityDistribution::getDistributionType,
            ProbabilityDistribution::getInstance,
            ProbabilityDistribution::getMinValue,
            ProbabilityDistribution::getP99,
            ProbabilityDistribution::getP90,
            ProbabilityDistribution::getModeValue,
            ProbabilityDistribution::getP50,
            ProbabilityDistribution::getMeanValue,
            ProbabilityDistribution::getP10,
            ProbabilityDistribution::getP1,
            ProbabilityDistribution::getMaxValue,
            ProbabilityDistribution::getConstantValue,
            ProbabilityDistribution::getComments
        )
        .containsExactly(
            submissionDetail,
            reservoirSubmissionDetail,
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionFieldInstance,
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getMin().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP99().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP90().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getMode().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP50().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getMean().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP10().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getP1().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getMax().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getConstant().createBigDecimalOrNull(),
            probabilityDistributionForm.getProbabilityOfExceedanceForm().getComments()
        );
  }

  @Test
  void saveProbabilityDistributionWhenNotNull_whenNull() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var submissionDetail = new SubmissionDetail();

    var emptyProbabilityDistributionForm = new ProbabilityDistributionForm();

    probabilityDistributionPersistenceService
        .saveProbabilityDistributionWhenNotNull(probabilityDistributionFieldInstance, emptyProbabilityDistributionForm, submissionDetail);

    verify(probabilityDistributionRepository, never()).save(any());
  }

  @Test
  void saveProbabilityDistributionWhenNotNull_reservoirSubmissionDetail_whenNull() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var submissionDetail = new SubmissionDetail();
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();

    var emptyProbabilityDistributionForm = new ProbabilityDistributionForm();

    probabilityDistributionPersistenceService
        .saveProbabilityDistributionWhenNotNull(probabilityDistributionFieldInstance, emptyProbabilityDistributionForm,
            submissionDetail, reservoirSubmissionDetail);

    verify(probabilityDistributionRepository, never()).save(any());
  }

  @Test
  void saveProbabilityDistributionWhenNotNull_withNullDistributionType() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var submissionDetail = new SubmissionDetail();

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.NORMAL);
    var probabilityDistribution = new ProbabilityDistribution();
    probabilityDistribution.setDistributionType(null);
    when(probabilityDistributionRepository.findBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance))
        .thenReturn(Optional.of(probabilityDistribution));

    probabilityDistributionPersistenceService
        .saveProbabilityDistributionWhenNotNull(probabilityDistributionFieldInstance, probabilityDistributionForm, submissionDetail);

    verify(probabilityDistributionRepository, times(1)).delete(probabilityDistribution);
    verify(probabilityDistributionRepository, times(1)).save(any());
  }

  @Test
  void saveProbabilityDistributionWhenNotNull_reservoirSubmissionDetail_withNullDistributionType() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var submissionDetail = new SubmissionDetail();
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.NORMAL);
    var probabilityDistribution = new ProbabilityDistribution();
    probabilityDistribution.setDistributionType(null);
    when(probabilityDistributionRepository.findByReservoirSubmissionDetailAndInstance(reservoirSubmissionDetail,
        probabilityDistributionFieldInstance))
        .thenReturn(Optional.of(probabilityDistribution));

    probabilityDistributionPersistenceService
        .saveProbabilityDistributionWhenNotNull(probabilityDistributionFieldInstance, probabilityDistributionForm,
            submissionDetail, reservoirSubmissionDetail);

    verify(probabilityDistributionRepository, times(1)).delete(probabilityDistribution);
    verify(probabilityDistributionRepository, times(1)).save(any());
  }

  @Test
  void saveSummedProbabilityDistribution_reservoirSubmissionDetail() {
    var instance = ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT;
    var submissionDetail = new SubmissionDetail();
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    reservoirSubmissionDetail.setSubmissionDetail(submissionDetail);

    probabilityDistributionPersistenceService.saveSummedProbabilityDistribution(
        new ProbabilityDistribution(),
        instance,
        reservoirSubmissionDetail
    );

    verify(probabilityDistributionRepository).save(probabilityDistributionArgumentCaptor.capture());

    assertThat(probabilityDistributionArgumentCaptor.getValue()).extracting(
        ProbabilityDistribution::getInstance,
        ProbabilityDistribution::getReservoirSubmissionDetail,
        ProbabilityDistribution::getSubmissionDetail
    ).containsExactly(
        instance,
        reservoirSubmissionDetail,
        submissionDetail
    );
  }

  @Test
  void saveSummedProbabilityDistribution_submissionDetail() {
    var instance = ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT;
    var submissionDetail = new SubmissionDetail();

    probabilityDistributionPersistenceService.saveSummedProbabilityDistribution(
        new ProbabilityDistribution(),
        instance,
        submissionDetail
    );

    verify(probabilityDistributionRepository).save(probabilityDistributionArgumentCaptor.capture());

    assertThat(probabilityDistributionArgumentCaptor.getValue()).extracting(
        ProbabilityDistribution::getInstance,
        ProbabilityDistribution::getReservoirSubmissionDetail,
        ProbabilityDistribution::getSubmissionDetail
    ).containsExactly(
        instance,
        null,
        submissionDetail
    );
  }
  
  @Test
  void deleteProbabilityDistribution_verifyMethodCall() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    probabilityDistributionPersistenceService.deleteProbabilityDistribution(probabilityDistributionFieldInstance, submissionDetail);

    verify(probabilityDistributionRepository, times(1))
        .deleteBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance);
  }

  @Test
  void deleteProbabilityDistribution_reservoirSubmissionDetail_verifyMethodCall() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();

    probabilityDistributionPersistenceService.deleteProbabilityDistribution(probabilityDistributionFieldInstance, reservoirSubmissionDetail);

    verify(probabilityDistributionRepository, times(1))
        .deleteByReservoirSubmissionDetailAndInstance(reservoirSubmissionDetail, probabilityDistributionFieldInstance);
  }

  @Test
  void getProbabilityDistribution_verifyMethodCall() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    probabilityDistributionPersistenceService
        .getProbabilityDistribution(submissionDetail, probabilityDistributionFieldInstance);

    verify(probabilityDistributionRepository)
        .findBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance);
  }

  @Test
  void getProbabilityDistribution_reservoirSubmissionDetail_verifyMethodCall() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();

    probabilityDistributionPersistenceService
        .getProbabilityDistribution(reservoirSubmissionDetail, probabilityDistributionFieldInstance);

    verify(probabilityDistributionRepository)
        .findByReservoirSubmissionDetailAndInstance(reservoirSubmissionDetail, probabilityDistributionFieldInstance);
  }

  @Test
  void getAllProbabilityDistributionsForInstances_verifyMethodCall() {
    var probabilityDistributionFieldInstances = Set.of(
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT
    );
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    probabilityDistributionPersistenceService
        .getAllProbabilityDistributionsForInstances(submissionDetail, probabilityDistributionFieldInstances);

    verify(probabilityDistributionRepository)
        .findBySubmissionDetailAndInstanceIn(submissionDetail, probabilityDistributionFieldInstances);
  }

  @Test
  void getAllProbabilityDistributionsForInstances_reservoirSubmissionDetails_verifyMethodCall() {
    var probabilityDistributionFieldInstances = Set.of(
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT
    );
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();

    probabilityDistributionPersistenceService
        .getAllProbabilityDistributionsForInstances(reservoirSubmissionDetail, probabilityDistributionFieldInstances);

    verify(probabilityDistributionRepository)
        .findByReservoirSubmissionDetailAndInstanceIn(reservoirSubmissionDetail, probabilityDistributionFieldInstances);
  }

  private ProbabilityDistributionForm getProbabilityDistributionForm() {
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setMin(new DecimalInput("1"));
    probabilityOfExceedanceForm.setP99(new DecimalInput("2"));
    probabilityOfExceedanceForm.setP90(new DecimalInput("3"));
    probabilityOfExceedanceForm.setMode(new DecimalInput("4"));
    probabilityOfExceedanceForm.setP50(new DecimalInput("5"));
    probabilityOfExceedanceForm.setMean(new DecimalInput("6"));
    probabilityOfExceedanceForm.setP10(new DecimalInput("7"));
    probabilityOfExceedanceForm.setP1(new DecimalInput("8"));
    probabilityOfExceedanceForm.setMax(new DecimalInput("9"));
    probabilityOfExceedanceForm.setConstant(new DecimalInput("10"));
    probabilityOfExceedanceForm.setComments("Comments");

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.NORMAL);
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);

    return probabilityDistributionForm;
  }
}