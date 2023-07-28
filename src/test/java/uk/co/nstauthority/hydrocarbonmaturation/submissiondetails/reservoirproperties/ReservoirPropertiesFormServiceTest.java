package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesFormServiceTest {

  private static ReservoirPropertiesFormService reservoirPropertiesFormService;

  @Mock
  private static ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;

  @Mock
  private static ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private static DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @Mock
  private static ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private static HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  private static SubmissionDetail submissionDetail;
  private static ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirPropertiesFormService = new ReservoirPropertiesFormService(reservoirPropertiesFormValidator,
        reservoirPropertiesPersistenceService, probabilityDistributionFormService,
        discoveryWellDetailsPersistenceService, hydrocarbonCasePersistenceService);

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new ReservoirPropertiesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var captor = ArgumentCaptor.forClass(ReservoirPropertiesValidatorHints.class);
    reservoirPropertiesFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(reservoirPropertiesFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .hasOnlyFields("hydrocarbonsDiscovered")
        .extracting(
            ReservoirPropertiesValidatorHints::getValidationType,
            ReservoirPropertiesValidatorHints::getSubmissionDetail,
            ReservoirPropertiesValidatorHints::getHydrocarbonsDiscovered)
        .containsExactly(
            ValidationType.FULL,
            submissionDetail,
            null
        );
  }

  @Test
  void reservoirPropertiesEntityToForm_newEntity_thenEmptyForm() {
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.empty());

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail))
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
            form -> form.getGrossRockVolume().getProbabilityDistributionType()
        )
        .containsOnlyNulls();
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
            reservoirProperty.getAgeStage().name(),
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

    var discoveryWellDetail = new DiscoveryWellDetail();

    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(discoverySubmissionDetail))
        .thenReturn(Optional.of(discoveryWellDetail));
    when(discoveryWellDetailsPersistenceService.isDiscoveryMixed(any()))
        .thenReturn(true);

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(discoveryReservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT))
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
  void reservoirPropertiesEntityToForm_existingEntity_withManualFormation_thenPopulatedForm() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setFormationName(null);
    reservoirProperty.setFormationNameManual("manual formation name");
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(
        reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)
            .getFormationName()
    ).isEqualTo(
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

    assertThat(
        reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)
            .getAgeStageOrSeries()
    ).isEqualTo(AgeSeries.LOWER_CARBONIFEROUS.name());
  }

  @Test
  void reservoirPropertiesEntityToForm_existingEntity_withManualAgeStage_thenPopulatedForm() {
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail);
    reservoirProperty.setAgeStage(null);
    reservoirProperty.setAgeStageManual("manual age stage");
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(
        reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)
            .getAgeStageOrSeries()
    ).isEqualTo(
        SearchSelectorService.getValueWithManualEntryPrefix(
            reservoirProperty.getAgeStageManual()
        )
    );
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

    return reservoirProperty;
  }
}