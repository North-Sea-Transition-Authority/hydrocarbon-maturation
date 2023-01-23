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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
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

  @BeforeEach
  void setUp() {
    reservoirPropertiesFormService = new ReservoirPropertiesFormService(reservoirPropertiesFormValidator,
        reservoirPropertiesPersistenceService, probabilityDistributionFormService,
        discoveryWellDetailsPersistenceService, hydrocarbonCasePersistenceService);

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
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
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail)).thenReturn(Optional.empty());

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail))
        .extracting(
            ReservoirPropertiesForm::getFormationName,
            ReservoirPropertiesForm::getAgeStageOrSeries,
            ReservoirPropertiesForm::getLithology,
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
  void reservoirPropertiesEntityToForm_existingEntity_thenPopulatedForm(SubmissionType submissionType) {
    var reservoirProperty = getReservoirProperty(submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail)).thenReturn(Optional.of(reservoirProperty));
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm();
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        eq(submissionDetail), any())).thenReturn(probabilityDistributionForm);

    var expectedConnectivity = submissionType == SubmissionType.POST_DRILL_DRY_HOLE ? "connectivity" : null;
    var expectedFacies = submissionType == SubmissionType.POST_DRILL_DRY_HOLE ? "facies" : null;

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail))
        .extracting(
            ReservoirPropertiesForm::getFormationName,
            ReservoirPropertiesForm::getAgeStageOrSeries,
            ReservoirPropertiesForm::getLithology,
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
            form -> form.getAverageNetPayThickness().getNormal().getConstant()
        )
        .containsExactly(
            reservoirProperty.getFormationName(),
            reservoirProperty.getAgeStage(),
            reservoirProperty.getLithology(),
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
            probabilityDistributionForm.getNormal().getConstant()
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_DISCOVERY", "UPDATE_DISCOVERY"})
  void reservoirPropertiesEntityToForm_whenCreateDiscovery_thenVerifyDIscoveryDistributionEntityToForm(
      SubmissionType submissionType) {
    var reservoirProperty = getReservoirProperty(submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail)).thenReturn(Optional.of(reservoirProperty));

    var discoveryWellDetail = new DiscoveryWellDetail();

    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetail));
    when(discoveryWellDetailsPersistenceService.isDiscoveryMixed(any()))
        .thenReturn(true);

    reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail);

    verify(probabilityDistributionFormService, times(1))
        .probabilityDistributionEntityToForm(submissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT);
    verify(probabilityDistributionFormService, times(1))
        .probabilityDistributionEntityToForm(submissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT);
    verify(probabilityDistributionFormService, times(1))
        .probabilityDistributionEntityToForm(submissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT);
    verify(probabilityDistributionFormService, times(1))
        .probabilityDistributionEntityToForm(submissionDetail,
            ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY);
  }

  private ReservoirProperty getReservoirProperty(SubmissionDetail submissionDetail) {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setSubmissionDetail(submissionDetail);
    reservoirProperty.setFormationName("formation name");
    reservoirProperty.setAgeStage("age stage");
    reservoirProperty.setLithology("lithology");
    reservoirProperty.setReservoirConnectivity("connectivity");
    reservoirProperty.setReservoirFacies("facies");
    return reservoirProperty;
  }
}