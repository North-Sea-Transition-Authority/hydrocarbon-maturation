package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesFormServiceTest {

  private static ReservoirPropertiesFormService reservoirPropertiesFormService;

  @Mock
  private static ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private static ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private static HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Mock
  private static GeneralReservoirInformationService generalReservoirInformationService;

  @Mock
  private static BarrierBaffleService barrierBaffleService;

  private static SubmissionDetail submissionDetail;
  private static ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirPropertiesFormService = new ReservoirPropertiesFormService(reservoirPropertiesPersistenceService,
        probabilityDistributionFormService, hydrocarbonCasePersistenceService,
        generalReservoirInformationService, barrierBaffleService);

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @Test
  void reservoirPropertiesEntityToForm_newEntity_thenEmptyForm() {
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.empty());

    var reservoirPropertiesForm = reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail);

    assertThat(reservoirPropertiesForm)
        .extracting(
            ReservoirPropertiesForm::getFormationName,
            ReservoirPropertiesForm::getAgeStageOrSeries,
            ReservoirPropertiesForm::getGroupName,
            ReservoirPropertiesForm::getLithology,
            ReservoirPropertiesForm::getLithologyOther,
            ReservoirPropertiesForm::getReservoirFacies,
            ReservoirPropertiesForm::getReservoirConnectivity,
            form -> form.getAverageNetPayThickness().getProbabilityDistributionType(),
            form -> form.getGrossReservoirThickness().getProbabilityDistributionType(),
            form -> form.getDepthToHydrocarbonContact().getProbabilityDistributionType(),
            form -> form.getNetPorosity().getProbabilityDistributionType(),
            form -> form.getNetToGross().getProbabilityDistributionType(),
            form -> form.getPercentOfGrvOilBearing().getProbabilityDistributionType(),
            form -> form.getGrossRockVolume().getProbabilityDistributionType(),
            form -> form.getFracturePressure().getDecimalInputValue(),
            ReservoirPropertiesForm::getDepositionalSetting,
            ReservoirPropertiesForm::getDepositionalEnvironment,
            form -> form.getPlannedProducers().getDecimalInputValue(),
            form -> form.getPlannedInjectors().getDecimalInputValue(),
            ReservoirPropertiesForm::getIsLateralBarriersBaffles,
            ReservoirPropertiesForm::getIsVerticalBarriersBaffles
        )
        .containsOnlyNulls();

    assertThat(reservoirPropertiesForm.getLateralBarriersBafflesDetail()).isEmpty();
    assertThat(reservoirPropertiesForm.getVerticalBarriersBafflesDetail()).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT", "POST_DRILL_DRY_HOLE"})
  void reservoirPropertiesEntityToForm_existingProspectEntity_thenPopulatedForm(SubmissionType submissionType) {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm();
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        eq(reservoirSubmissionDetail), any())).thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT))
        .thenReturn(probabilityDistributionForm);


    var expectedConnectivity = submissionType == SubmissionType.POST_DRILL_DRY_HOLE ? "connectivity" : null;
    var expectedFacies = submissionType == SubmissionType.POST_DRILL_DRY_HOLE ? "facies" : null;

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail))
        .extracting(
            ReservoirPropertiesForm::getAgeStageOrSeries,
            ReservoirPropertiesForm::getGroupName,
            ReservoirPropertiesForm::getLithology,
            ReservoirPropertiesForm::getLithologyOther,
            ReservoirPropertiesForm::getReservoirFacies,
            ReservoirPropertiesForm::getReservoirConnectivity,
            form -> form.getNetToGross().getProbabilityDistributionType(),
            form -> form.getNetToGross().getNormal().getMin(),
            form -> form.getNetToGross().getNormal().getP99(),
            form -> form.getNetToGross().getNormal().getP90(),
            form -> form.getNetToGross().getNormal().getMode(),
            form -> form.getNetToGross().getNormal().getP50(),
            form -> form.getNetToGross().getNormal().getMean(),
            form -> form.getNetToGross().getNormal().getP10(),
            form -> form.getNetToGross().getNormal().getP1(),
            form -> form.getNetToGross().getNormal().getMax(),
            form -> form.getNetToGross().getNormal().getConstant(),
            form -> form.getNetToGross().getNormal().getComments(),
            form -> form.getAverageNetPayThickness().getProbabilityDistributionType(),
            form -> form.getAverageNetPayThickness().getNormal().getMin(),
            form -> form.getAverageNetPayThickness().getNormal().getP99(),
            form -> form.getAverageNetPayThickness().getNormal().getP90(),
            form -> form.getAverageNetPayThickness().getNormal().getMode(),
            form -> form.getAverageNetPayThickness().getNormal().getP50(),
            form -> form.getAverageNetPayThickness().getNormal().getMean(),
            form -> form.getAverageNetPayThickness().getNormal().getP10(),
            form -> form.getAverageNetPayThickness().getNormal().getP1(),
            form -> form.getAverageNetPayThickness().getNormal().getMax(),
            form -> form.getAverageNetPayThickness().getNormal().getConstant(),
            form -> form.getAverageNetPayThickness().getNormal().getComments()
        )
        .containsExactly(
            reservoirProperty.getAgeStage(),
            reservoirProperty.getGroupName(),
            reservoirProperty.getLithology(),
            reservoirProperty.getLithologyOther(),
            expectedFacies,
            expectedConnectivity,
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getNormal().getMin(),
            probabilityDistributionForm.getNormal().getP99(),
            probabilityDistributionForm.getNormal().getP90(),
            probabilityDistributionForm.getNormal().getMode(),
            probabilityDistributionForm.getNormal().getP50(),
            probabilityDistributionForm.getNormal().getMean(),
            probabilityDistributionForm.getNormal().getP10(),
            probabilityDistributionForm.getNormal().getP1(),
            probabilityDistributionForm.getNormal().getMax(),
            probabilityDistributionForm.getNormal().getConstant(),
            probabilityDistributionForm.getNormal().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getNormal().getMin(),
            probabilityDistributionForm.getNormal().getP99(),
            probabilityDistributionForm.getNormal().getP90(),
            probabilityDistributionForm.getNormal().getMode(),
            probabilityDistributionForm.getNormal().getP50(),
            probabilityDistributionForm.getNormal().getMean(),
            probabilityDistributionForm.getNormal().getP10(),
            probabilityDistributionForm.getNormal().getP1(),
            probabilityDistributionForm.getNormal().getMax(),
            probabilityDistributionForm.getNormal().getConstant(),
            probabilityDistributionForm.getNormal().getComments()
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_DISCOVERY", "UPDATE_DISCOVERY"})
  void reservoirPropertiesEntityToForm_whenCreateOrUpdateDiscovery_thenVerifyDiscoveryDistributionEntityToForm(
      SubmissionType submissionType) {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(submissionType);
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    var reservoirProperty = getReservoirProperty(discoveryReservoirSubmissionDetail);
    discoverySubmissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(discoveryReservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isMixed(reservoirHydrocarbons))
        .thenReturn(true);

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY))
        .thenReturn(probabilityDistributionForm);

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(discoveryReservoirSubmissionDetail))
        .extracting(
            ReservoirPropertiesForm::getDepthToOilWaterContact,
            ReservoirPropertiesForm::getDepthToGasOilContact,
            ReservoirPropertiesForm::getDepthToGasWaterContact,
            ReservoirPropertiesForm::getAverageNetPayThickness
        )
        .containsExactly(
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA"})
  void reservoirPropertiesEntityToForm_whenFieldNotCop_thenVerifyFieldDistributionEntityToForm(
      SubmissionType submissionType) {
    var fieldSubmissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(submissionType);
    var fieldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(fieldSubmissionDetail);
    var reservoirProperty = getReservoirProperty(fieldReservoirSubmissionDetail);
    fieldSubmissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(fieldReservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(fieldReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isMixed(reservoirHydrocarbons))
        .thenReturn(true);

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD))
        .thenReturn(probabilityDistributionForm);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD))
        .thenReturn(probabilityDistributionForm);

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(fieldReservoirSubmissionDetail))
        .extracting(
            ReservoirPropertiesForm::getDepthToOilWaterContact,
            ReservoirPropertiesForm::getDepthToGasOilContact,
            ReservoirPropertiesForm::getDepthToGasWaterContact,
            ReservoirPropertiesForm::getAverageNetPayThickness,
            ReservoirPropertiesForm::getNetPorosity,
            ReservoirPropertiesForm::getNetToGross,
            ReservoirPropertiesForm::getPercentOfGrvOilBearing,
            ReservoirPropertiesForm::getGrossRockVolume,
            ReservoirPropertiesForm::getGrossReservoirThickness,
            ReservoirPropertiesForm::getAverageNetPayThickness
        )
        .containsExactly(
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm
        );
  }

  @Test
  void reservoirPropertiesEntityToForm_whenFieldCop_thenVerifyFieldDistributionEntityToForm() {
    var submissionType = SubmissionType.CREATE_COP;

    var fieldSubmissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(submissionType);
    var fieldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(fieldSubmissionDetail);
    var reservoirProperty = getReservoirProperty(fieldReservoirSubmissionDetail);
    fieldSubmissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(fieldReservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(fieldReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isMixed(reservoirHydrocarbons))
        .thenReturn(true);

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD))
        .thenReturn(probabilityDistributionForm);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD))
        .thenReturn(probabilityDistributionForm);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD))
        .thenReturn(probabilityDistributionForm);

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(fieldReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD))
        .thenReturn(probabilityDistributionForm);

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(fieldReservoirSubmissionDetail))
        .extracting(
            ReservoirPropertiesForm::getOriginalDepthToOilWaterContact,
            ReservoirPropertiesForm::getOriginalDepthToGasWaterContact,
            ReservoirPropertiesForm::getOriginalDepthToGasOilContact,
            ReservoirPropertiesForm::getDepthToMovedGasOilContact,
            ReservoirPropertiesForm::getDepthToMovedGasWaterContact,
            ReservoirPropertiesForm::getDepthToMovedOilWaterContact,
            ReservoirPropertiesForm::getNetPorosity,
            ReservoirPropertiesForm::getNetToGross,
            ReservoirPropertiesForm::getPercentOfGrvOilBearing,
            ReservoirPropertiesForm::getGrossRockVolume,
            ReservoirPropertiesForm::getGrossReservoirThickness,
            ReservoirPropertiesForm::getAverageNetPayThickness
        )
        .containsExactly(
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm,
            probabilityDistributionForm
        );
  }

  @Test
  void reservoirPropertiesEntityToForm_whenPostDrillDryHole_thenVerifyDryWellDistributionEntityToForm() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        eq(reservoirSubmissionDetail), any())).thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL))
        .thenReturn(probabilityDistributionForm);

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail).getAverageNetPayThickness())
        .isEqualTo(probabilityDistributionForm);
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withFormationEnum_thenPopulatedForm() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setFormationNameManual(null);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(
        reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)
            .getFormationName()
    ).isEqualTo(Formation.ASGARD.name());
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withManualFormation_thenNotPopulatedForm() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setFormationName(null);
    reservoirProperty.setFormationNameManual("manual formation name");
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail).getFormationName()).isEqualTo(
        SearchSelectorService.getValueWithManualEntryPrefix(
            reservoirProperty.getFormationNameManual()
        )
    );
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withNullFormationAndNullManual_thenNullFormation() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setFormationName(null);
    reservoirProperty.setFormationNameManual(null);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail).getFormationName())
        .isNull();
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withSubUnitEnum_thenPopulatedForm() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setSubUnit(ReservoirSubUnit.T10);
    reservoirProperty.setSubUnitManual(null);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(
        reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail).getSubUnit()
    ).isEqualTo(ReservoirSubUnit.T10.name());
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withManualSubUnit_thenPopulatedForm() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setSubUnit(null);
    reservoirProperty.setSubUnitManual("manual sub unit");
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(
        reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail).getSubUnit()
    ).isEqualTo(
        SearchSelectorService.getValueWithManualEntryPrefix(
            reservoirProperty.getSubUnitManual()
        )
    );
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withNullSubUnitAndNullManual_thenNullSubUnit() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setSubUnit(null);
    reservoirProperty.setSubUnitManual(null);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail).getSubUnit())
        .isNull();
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withAgeStageEnum_thenPopulatedForm() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    reservoirProperty.setAgeStageManual(null);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail).getAgeStageOrSeries())
        .isEqualTo(AgeSeries.LOWER_CARBONIFEROUS);
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withManualAgeStage_thenNotPopulatedForm() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setAgeStage(null);
    reservoirProperty.setAgeStageManual("manual age stage");
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail).getAgeStageOrSeries())
        .isNull();
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withNullAgeStageAndNullManual_thenNullAgeStage() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setAgeStage(null);
    reservoirProperty.setAgeStageManual(null);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail).getAgeStageOrSeries())
        .isNull();
  }

  private ReservoirProperty getReservoirProperty(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirProperty.setLithology(ReservoirLithology.OTHER);
    reservoirProperty.setLithologyOther("other lithology");
    reservoirProperty.setReservoirConnectivity("connectivity");
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setSubUnit(ReservoirSubUnit.T10);
    reservoirProperty.setAgeStage(AgeSeries.LOWER_DEVONIAN);
    reservoirProperty.setFracturePressure(BigDecimal.ONE);
    reservoirProperty.setDepositionalSetting(DepositionalSetting.TERRESTRIAL.name());
    reservoirProperty.setDepositionalEnvironment(DepositionalEnvironment.AEOLIAN.name());
    reservoirProperty.setPlannedProducers(BigDecimal.ONE);
    reservoirProperty.setPlannedInjectors(BigDecimal.ONE);
    reservoirProperty.setLateralBarriersBaffles(false);
    reservoirProperty.setVerticalBarriersBaffles(false);

    return reservoirProperty;
  }
}