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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesPersistenceServiceTest {

  @Mock
  private ReservoirPropertiesRepository reservoirPropertiesRepository;

  @Mock
  private static ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private static ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  private static SubmissionDetail submissionDetail;
  private static ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirPropertiesPersistenceService = new ReservoirPropertiesPersistenceService(reservoirPropertiesRepository,
        probabilityDistributionPersistenceService);

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @Test
  void saveReservoirProperties_whenProspectSubmission_givenAForm_verifyCreated_Entity() {
    var form = getValidForm();

    var reservoirPropertiesArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);
    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);


    verifyProspectSubmissionTypesProbabilityTablesSaved(form, reservoirSubmissionDetail);

    // Prospect specific
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
        form.getAverageNetPayThickness(),
        submissionDetail,
        reservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT,
        form.getDepthToHydrocarbonContact(),
        submissionDetail,
        reservoirSubmissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirPropertiesArgumentCaptor.capture());

    var savedReservoirProperty = reservoirPropertiesArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getReservoirSubmissionDetail,
            ReservoirProperty::getGroupName,
            ReservoirProperty::getLithology,
            ReservoirProperty::getLithologyOther,
            ReservoirProperty::getReservoirConnectivity,
            ReservoirProperty::getReservoirFacies
        )
        .containsExactly(
            reservoirSubmissionDetail,
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
    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);

    verifyProspectSubmissionTypesProbabilityTablesSaved(form, reservoirSubmissionDetail);

    // Dry well specific
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL,
        form.getAverageNetPayThickness(),
        submissionDetail,
        reservoirSubmissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirPropertiesArgumentCaptor.capture());

    var savedReservoirProperty = reservoirPropertiesArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getReservoirSubmissionDetail,
            ReservoirProperty::getGroupName,
            ReservoirProperty::getLithology,
            ReservoirProperty::getLithologyOther,
            ReservoirProperty::getReservoirConnectivity,
            ReservoirProperty::getReservoirFacies
        )
        .containsExactly(
            reservoirSubmissionDetail,
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
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    var reservoirPropertiesArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);
    reservoirPropertiesPersistenceService.saveReservoirProperty(form, discoveryReservoirSubmissionDetail);

    verifyDiscoverySubmissionTypesProbabilityTablesSaved(form, discoveryReservoirSubmissionDetail);

    // Discovery specific
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY,
        form.getAverageNetPayThickness(),
        discoverySubmissionDetail,
        discoveryReservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY,
            discoveryReservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY,
            discoveryReservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .deleteProbabilityDistribution(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY,
            discoveryReservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY,
            form.getDepthToGasWaterContact(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY,
            form.getDepthToOilWaterContact(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY,
            form.getDepthToGasOilContact(),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirPropertiesArgumentCaptor.capture());

    var savedReservoirProperty = reservoirPropertiesArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getReservoirSubmissionDetail,
            ReservoirProperty::getGroupName,
            ReservoirProperty::getLithology,
            ReservoirProperty::getLithologyOther,
            ReservoirProperty::getReservoirConnectivity,
            ReservoirProperty::getReservoirFacies
        )
        .containsExactly(
            discoveryReservoirSubmissionDetail,
            form.getGroupName(),
            form.getLithology(),
            form.getLithologyOther(),
            form.getReservoirConnectivity(),
            form.getReservoirFacies()
        );
  }

  @Test
  void getTrapProperty_verifyMethodCall() {
    reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail);
    verify(reservoirPropertiesRepository, times(1)).findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  private void verifyProspectSubmissionTypesProbabilityTablesSaved(ReservoirPropertiesForm form, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        form.getNetToGross(),
        submissionDetail,
        reservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT,
        form.getNetPorosity(),
        submissionDetail,
        reservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
        form.getPercentOfGrvOilBearing(),
        submissionDetail,
        reservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        form.getGrossRockVolume(),
        submissionDetail,
        reservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        form.getGrossReservoirThickness(),
        submissionDetail,
        reservoirSubmissionDetail);
  }

  private void verifyDiscoverySubmissionTypesProbabilityTablesSaved(ReservoirPropertiesForm form, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY,
        form.getNetToGross(),
        submissionDetail,
        reservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY,
        form.getNetPorosity(),
        submissionDetail,
        reservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY,
        form.getPercentOfGrvOilBearing(),
        submissionDetail,
        reservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY,
        form.getGrossRockVolume(),
        submissionDetail,
        reservoirSubmissionDetail);
    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY,
        form.getGrossReservoirThickness(),
        submissionDetail,
        reservoirSubmissionDetail);
  }

  @Test
  void saveReservoirProperties_whenGivenAForm_NoFormationProvided_verifyCreatedEntity() {
    var form = new ReservoirPropertiesForm();
    var reservoirArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);

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

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);

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

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);

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

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);

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

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);

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

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);

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

  @Test
  void saveReservoirProperties_whenGivenAForm_NoAgeStageProvided_verifyCreatedEntity() {
    var form = new ReservoirPropertiesForm();
    var reservoirArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirArgumentCaptor.capture());
    var savedReservoirProperty = reservoirArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getAgeStage,
            ReservoirProperty::getAgeStageManual
        )
        .containsExactly(
            null,
            null
        );
  }

  @Test
  void saveReservoirProperties_whenGivenAForm_ageStageEnumProvided_verifyCreatedEntity() {
    var form = new ReservoirPropertiesForm();
    form.setAgeStageOrSeries(AgeSeries.MIDDLE_JURASSIC);
    var reservoirArgumentCaptor = ArgumentCaptor.forClass(ReservoirProperty.class);

    reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);

    verify(reservoirPropertiesRepository, times(1)).save(reservoirArgumentCaptor.capture());
    var savedReservoirProperty = reservoirArgumentCaptor.getValue();

    assertThat(savedReservoirProperty)
        .extracting(
            ReservoirProperty::getAgeStage,
            ReservoirProperty::getAgeStageManual
        )
        .containsExactly(
            AgeSeries.MIDDLE_JURASSIC,
            null
        );
  }

  private ReservoirPropertiesForm getValidForm() {
    var form = new ReservoirPropertiesForm();
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