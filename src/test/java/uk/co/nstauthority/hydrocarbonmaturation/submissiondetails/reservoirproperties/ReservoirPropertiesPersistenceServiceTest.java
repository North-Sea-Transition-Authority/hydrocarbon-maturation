package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesPersistenceServiceTest {

  @Mock
  private ReservoirPropertiesRepository reservoirPropertiesRepository;
  @Mock
  private static ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private static ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    reservoirPropertiesPersistenceService = new ReservoirPropertiesPersistenceService(reservoirPropertiesRepository,
        probabilityDistributionPersistenceService);

    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveReservoirProperties_whenGivenAForm_verifyCreated_Entity() {
    var form = getValidForm();

    var reservoirPropertiesArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);
    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);

    verifyProbabilityTablesSaved(form);
    verify(reservoirPropertiesRepository, times(1)).save(reservoirPropertiesArgumentCaptor.capture());

    var savedReservoirProperty = reservoirPropertiesArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getSubmissionDetail,
            ReservoirProperty::getFormationName,
            ReservoirProperty::getAgeStage,
            ReservoirProperty::getLithology
        )
        .containsExactly(
            submissionDetail,
            form.getFormationName(),
            form.getAgeStageOrSeries(),
            form.getLithology()
        );
  }

  @Test
  void getTrapProperty_verifyMethodCall() {
    reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail);
    verify(reservoirPropertiesRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  private void verifyProbabilityTablesSaved(ReservoirPropertiesForm form) {
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT_DISCOVERY,
        form.getAverageNetPayThickness(),
        submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY,
        form.getNetToGross(),
        submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY,
        form.getNetPorosity(),
        submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING,
        form.getPercentOfGrvOilBearing(),
        submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME,
        form.getGrossRockVolume(),
        submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT,
        form.getDepthToHydrocarbonContact(),
        submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY,
        form.getGrossReservoirThickness(),
        submissionDetail);
  }

  private ReservoirPropertiesForm getValidForm() {
    var form = new ReservoirPropertiesForm();
    form.setFormationName("formation name");
    form.setAgeStageOrSeries("age stage or series");
    form.setLithology("lithology");
    form.setPercentOfGrvOilBearing(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setNetToGross(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setNetPorosity(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setAverageNetPayThickness(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setGrossRockVolume(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setDepthToHydrocarbonContact(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setGrossReservoirThickness(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    return form;
  }
}