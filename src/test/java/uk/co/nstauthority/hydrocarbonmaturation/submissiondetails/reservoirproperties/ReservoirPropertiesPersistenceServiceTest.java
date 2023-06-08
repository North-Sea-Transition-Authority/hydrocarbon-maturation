package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesPersistenceServiceTest {

  @Mock
  private ReservoirPropertiesRepository reservoirPropertiesRepository;
  @Mock
  private static ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private static ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    reservoirPropertiesPersistenceService = new ReservoirPropertiesPersistenceService(reservoirPropertiesRepository,
        probabilityDistributionPersistenceService);

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void saveReservoirProperties_whenProspectSubmission_givenAForm_verifyCreated_Entity() {
    var form = getValidForm();

    var reservoirPropertiesArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);
    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);


    verifyGenericSubmissionTypesProbabilityTablesSaved(form);

    // Prospect specific
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
        form.getAverageNetPayThickness(),
        submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT,
        form.getDepthToHydrocarbonContact(),
        submissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirPropertiesArgumentCaptor.capture());

    var savedReservoirProperty = reservoirPropertiesArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getSubmissionDetail,
            ReservoirProperty::getAgeStage,
            ReservoirProperty::getGroupName,
            ReservoirProperty::getLithology,
            ReservoirProperty::getLithologyOther,
            ReservoirProperty::getReservoirConnectivity,
            ReservoirProperty::getReservoirFacies
        )
        .containsExactly(
            submissionDetail,
            form.getAgeStageOrSeries(),
            form.getGroupName(),
            form.getLithology(),
            form.getLithologyOther(),
            null,
            null
        );
  }

  @Test
  void saveReservoirProperties_whenDryWellSubmission_givenAForm_verifyCreated_Entity() {
    var form = getValidForm();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    var reservoirPropertiesArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);
    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);

    verifyGenericSubmissionTypesProbabilityTablesSaved(form);

    // Dry well specific
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL,
        form.getAverageNetPayThickness(),
        submissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirPropertiesArgumentCaptor.capture());

    var savedReservoirProperty = reservoirPropertiesArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getSubmissionDetail,
            ReservoirProperty::getAgeStage,
            ReservoirProperty::getGroupName,
            ReservoirProperty::getLithology,
            ReservoirProperty::getLithologyOther,
            ReservoirProperty::getReservoirConnectivity,
            ReservoirProperty::getReservoirFacies
        )
        .containsExactly(
            submissionDetail,
            form.getAgeStageOrSeries(),
            form.getGroupName(),
            form.getLithology(),
            form.getLithologyOther(),
            form.getReservoirConnectivity(),
            form.getReservoirFacies()
        );
  }

  @Test
  void saveReservoirProperties_whenDiscoverySubmission_givenAForm_verifyCreated_Entity() {
    var form = getValidForm();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var reservoirPropertiesArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);
    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);

    verifyGenericSubmissionTypesProbabilityTablesSaved(form);

    // Discovery specific
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistribution(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY,
        form.getAverageNetPayThickness(),
        submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
            submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
            submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
            submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
            form.getDepthToGasWaterContact(),
            submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
            form.getDepthToOilWaterContact(),
            submissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistribution(
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
            form.getDepthToGasOilContact(),
            submissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirPropertiesArgumentCaptor.capture());

    var savedReservoirProperty = reservoirPropertiesArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getSubmissionDetail,
            ReservoirProperty::getAgeStage,
            ReservoirProperty::getGroupName,
            ReservoirProperty::getLithology,
            ReservoirProperty::getLithologyOther,
            ReservoirProperty::getReservoirConnectivity,
            ReservoirProperty::getReservoirFacies
        )
        .containsExactly(
            submissionDetail,
            form.getAgeStageOrSeries(),
            form.getGroupName(),
            form.getLithology(),
            form.getLithologyOther(),
            form.getReservoirConnectivity(),
            form.getReservoirFacies()
        );
  }

  @Test
  void getTrapProperty_verifyMethodCall() {
    reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail);
    verify(reservoirPropertiesRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  private void verifyGenericSubmissionTypesProbabilityTablesSaved(ReservoirPropertiesForm form) {
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
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY,
        form.getGrossReservoirThickness(),
        submissionDetail);
  }

  @Test
  void saveReservoirProperties_whenGivenAForm_NoFormationProvided_verifyCreatedEntity() {
    var form = new ReservoirPropertiesForm();
    var reservoirArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirArgumentCaptor.capture());
    var savedReservoirProperty = reservoirArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getFormationName,
            ReservoirProperty::getFormationNameManual
        )
        .containsExactly(
            null,
            null
        );
  }

  @Test
  void saveReservoirProperties_whenGivenAForm_formationEnumProvided_verifyCreatedEntity() {
    var form = new ReservoirPropertiesForm();
    form.setFormationName(Formation.ASGARD.name());
    var reservoirArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirArgumentCaptor.capture());
    var savedReservoirProperty = reservoirArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getFormationName,
            ReservoirProperty::getFormationNameManual
        )
        .containsExactly(
            Formation.ASGARD,
            null
        );
  }

  @Test
  void saveReservoirProperties_whenGivenAForm_manualFormationProvided_verifyCreatedEntity() {
    var form = new ReservoirPropertiesForm();
    var formation = "formation";
    form.setFormationName(SearchSelectorService.getValueWithManualEntryPrefix(formation));
    var reservoirArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirArgumentCaptor.capture());
    var savedReservoirProperty = reservoirArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getFormationName,
            ReservoirProperty::getFormationNameManual
        )
        .containsExactly(
            null,
            formation
        );
  }



  @Test
  void saveReservoirProperties_whenGivenAForm_NoSubUnitProvided_verifyCreatedEntity() {
    var form = new ReservoirPropertiesForm();
    var reservoirArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirArgumentCaptor.capture());
    var savedReservoirProperty = reservoirArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getSubUnit,
            ReservoirProperty::getSubUnitManual
        )
        .containsExactly(
            null,
            null
        );
  }

  @Test
  void saveReservoirProperties_whenGivenAForm_subUnitEnumProvided_verifyCreatedEntity() {
    var form = new ReservoirPropertiesForm();
    form.setSubUnit(ReservoirSubUnit.ALBA.name());
    var reservoirArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirArgumentCaptor.capture());
    var savedReservoirProperty = reservoirArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getSubUnit,
            ReservoirProperty::getSubUnitManual
        )
        .containsExactly(
            ReservoirSubUnit.ALBA,
            null
        );
  }

  @Test
  void saveReservoirProperties_whenGivenAForm_manualSubUnitProvided_verifyCreatedEntity() {
    var form = new ReservoirPropertiesForm();
    var subUnit = "sub unit";
    form.setSubUnit(SearchSelectorService.getValueWithManualEntryPrefix(subUnit));
    var reservoirArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirArgumentCaptor.capture());
    var savedReservoirProperty = reservoirArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getSubUnit,
            ReservoirProperty::getSubUnitManual
        )
        .containsExactly(
            null,
            subUnit
        );
  }

  private ReservoirPropertiesForm getValidForm() {
    var form = new ReservoirPropertiesForm();
    form.setAgeStageOrSeries(AgeSeries.LOWER_DEVONIAN);
    form.setGroupName(GroupName.CLAIR);
    form.setLithology(ReservoirLithology.OTHER);
    form.setLithologyOther("lithology other");
    form.setReservoirFacies("facies");
    form.setReservoirConnectivity("connectivity");
    form.setPercentOfGrvOilBearing(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setNetToGross(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setNetPorosity(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setAverageNetPayThickness(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.CONSTANT));
    form.setGrossRockVolume(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setDepthToHydrocarbonContact(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setGrossReservoirThickness(
        ProbabilityDistributionTestUtil.getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL));
    form.setDepthToGasOilContact(
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setDepthToGasWaterContact(
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setDepthToOilWaterContact(
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());

    return form;
  }
}