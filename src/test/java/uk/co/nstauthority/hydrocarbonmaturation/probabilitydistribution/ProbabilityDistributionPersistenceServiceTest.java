package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class ProbabilityDistributionPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionRepository probabilityDistributionRepository;

  @InjectMocks
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Test
  void saveProbabilityDistribution() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA;
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(probabilityDistributionRepository.findBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance))
        .thenReturn(Optional.empty());

    var probabilityDistributionForm = getProbabilityDistributionForm();

    var argumentCaptor = ArgumentCaptor.forClass(ProbabilityDistribution.class);
    probabilityDistributionPersistenceService.saveProbabilityDistribution(probabilityDistributionFieldInstance, probabilityDistributionForm, submissionDetail);

    verify(probabilityDistributionRepository).save(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue())
        .extracting(
            ProbabilityDistribution::getSubmissionDetail,
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
  void deleteProbabilityDistribution_verifyMethodCall() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA;
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    probabilityDistributionPersistenceService.deleteProbabilityDistribution(probabilityDistributionFieldInstance, submissionDetail);

    verify(probabilityDistributionRepository, times(1)).deleteBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance);
  }

  @Test
  void getProbabilityDistribution_verifyMethodCall() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA;
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    probabilityDistributionPersistenceService.getProbabilityDistribution(submissionDetail, probabilityDistributionFieldInstance);

    verify(probabilityDistributionRepository).findBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance);
  }

  @Test
  void getAllProbabilityDistributionsForInstances_verifyMethodCall() {
    var probabilityDistributionFieldInstances = Set.of(
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA,
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT_DISCOVERY
    );
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(submissionDetail, probabilityDistributionFieldInstances);

    verify(probabilityDistributionRepository).findBySubmissionDetailAndInstanceIn(submissionDetail, probabilityDistributionFieldInstances);
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