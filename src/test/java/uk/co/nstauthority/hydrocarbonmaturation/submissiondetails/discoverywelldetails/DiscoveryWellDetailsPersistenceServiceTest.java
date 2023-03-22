package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@ExtendWith(MockitoExtension.class)
class DiscoveryWellDetailsPersistenceServiceTest {

  @Mock
  private DiscoveryWellDetailsRepository discoveryWellDetailsRepository;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private WellQueryService wellQueryService;

  @InjectMocks
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getDiscoveryWellDetail_verifyMethodCall() {
    discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail);
    verify(discoveryWellDetailsRepository, times(1))
        .findBySubmissionDetail(submissionDetail);
  }

  @Test
  void saveDiscoveryWellDetails_whenGivenEmptyForm_verifyCreatedEmptyEntity() {
    var form = new DiscoveryWellDetailsForm();

    var discoveryWellDetail = new DiscoveryWellDetail();

    when(discoveryWellDetailsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetail));

    discoveryWellDetailsPersistenceService.saveDiscoveryWellDetails(form, submissionDetail);

    verify(discoveryWellDetailsRepository, times(1))
        .save(discoveryWellDetail);
    var detailForm = form.getWellDetailsForm();
    assertThat(discoveryWellDetail)
        .extracting(
            DiscoveryWellDetail::getWellReference,
            DiscoveryWellDetail::getTopReservoirLatitudeDegrees,
            DiscoveryWellDetail::getTopReservoirLatitudeMinutes,
            DiscoveryWellDetail::getTopReservoirLatitudeMinutes,
            DiscoveryWellDetail::getTopReservoirLongitudeDegrees,
            DiscoveryWellDetail::getTopReservoirLongitudeMinutes,
            DiscoveryWellDetail::getTopReservoirLongitudeSeconds,
            DiscoveryWellDetail::getTopReservoirLongitudeHemisphere,
            DiscoveryWellDetail::getTopReservoirDatum,
            DiscoveryWellDetail::getTotalDepthFormation,
            DiscoveryWellDetail::getTotalDepthFormationComments,
            DiscoveryWellDetail::getDataAcquisitionProgramme,
            DiscoveryWellDetail::getGeophysicalUpdateCommentary,
            DiscoveryWellDetail::getDiscoveryStatus,
            DiscoveryWellDetail::getChanceOfCommercialityComments,
            DiscoveryWellDetail::getTotalWellCost,
            DiscoveryWellDetail::getHydrocarbonsDiscovered,
            DiscoveryWellDetail::getSuccessRelatedCosts)
        .containsOnlyNulls();
  }

  @Test
  void saveDiscoveryWellDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = getFilledForm();

    var discoveryWellDetailArgumentCaptor = ArgumentCaptor.forClass(DiscoveryWellDetail.class);
    discoveryWellDetailsPersistenceService.saveDiscoveryWellDetails(form, submissionDetail);

    verify(discoveryWellDetailsRepository, times(1))
        .save(discoveryWellDetailArgumentCaptor.capture());
    var savedDiscoveryWellDetail = discoveryWellDetailArgumentCaptor.getValue();
    var detailForm = form.getWellDetailsForm();
    assertThat(savedDiscoveryWellDetail)
        .extracting(
            DiscoveryWellDetail::getWellId,
            DiscoveryWellDetail::getTopReservoirLatitudeDegrees,
            DiscoveryWellDetail::getTopReservoirLatitudeMinutes,
            DiscoveryWellDetail::getTopReservoirLatitudeMinutes,
            DiscoveryWellDetail::getTopReservoirLongitudeDegrees,
            DiscoveryWellDetail::getTopReservoirLongitudeMinutes,
            DiscoveryWellDetail::getTopReservoirLongitudeSeconds,
            DiscoveryWellDetail::getTopReservoirLongitudeHemisphere,
            DiscoveryWellDetail::getTopReservoirDatum,
            DiscoveryWellDetail::getTotalDepthFormation,
            DiscoveryWellDetail::getTotalDepthFormationComments,
            DiscoveryWellDetail::getDataAcquisitionProgramme,
            DiscoveryWellDetail::getGeophysicalUpdateCommentary,
            DiscoveryWellDetail::getDiscoveryStatus,
            DiscoveryWellDetail::getChanceOfCommercialityComments,
            DiscoveryWellDetail::getTotalWellCost,
            DiscoveryWellDetail::getHydrocarbonsDiscovered,
            DiscoveryWellDetail::getSuccessRelatedCosts)
        .containsExactly(
            Integer.valueOf(detailForm.getWell()),
            detailForm.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLongitudeHemisphere(),
            detailForm.getReservoirDatum(),
            detailForm.getTotalDepthFormation(),
            detailForm.getTotalDepthFormationComments(),
            detailForm.getDataAcquisitionProgramme(),
            detailForm.getGeophysicalUpdate(),
            form.getDiscoveryStatus(),
            form.getChanceOfCommercialityComments(),
            form.getDryHoleCost().createBigDecimalOrNull(),
            form.getHydrocarbonsDiscovered(),
            form.getSuccessRelatedCosts().createBigDecimalOrNull()
        );
  }

  @ParameterizedTest
  @EnumSource(HydrocarbonsDiscovered.class)
  void isDiscovery_gasOilOrMixed_assertTrueFalse(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    var discoveryWellDetail = new DiscoveryWellDetail();
    discoveryWellDetail.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);

    switch(hydrocarbonsDiscovered) {
      case GAS_ONLY -> {
        assertTrue(discoveryWellDetailsPersistenceService.isDiscoveryGasOnly(discoveryWellDetail));
        assertFalse(discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWellDetail));
        assertFalse(discoveryWellDetailsPersistenceService.isDiscoveryOilOnly(discoveryWellDetail));
      }
      case MIXED_OIL_AND_GAS -> {
        assertFalse(discoveryWellDetailsPersistenceService.isDiscoveryGasOnly(discoveryWellDetail));
        assertTrue(discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWellDetail));
        assertFalse(discoveryWellDetailsPersistenceService.isDiscoveryOilOnly(discoveryWellDetail));
      }
      case OIL_ONLY -> {
        assertFalse(discoveryWellDetailsPersistenceService.isDiscoveryGasOnly(discoveryWellDetail));
        assertFalse(discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWellDetail));
        assertTrue(discoveryWellDetailsPersistenceService.isDiscoveryOilOnly(discoveryWellDetail));
      }
    }
  }

  @ParameterizedTest
  @EnumSource(HydrocarbonsDiscovered.class)
  void saveDiscoveryWellDetails_whenGivenAForm_verifyDeletedProbabilityDistribution(
      HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    var form = getFilledForm();
    form.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);
    var discoveryWellDetailArgumentCaptor = ArgumentCaptor.forClass(DiscoveryWellDetail.class);
    discoveryWellDetailsPersistenceService.saveDiscoveryWellDetails(form, submissionDetail);

    verify(discoveryWellDetailsRepository, times(1))
        .save(discoveryWellDetailArgumentCaptor.capture());
    var savedDiscoveryWellDetail = discoveryWellDetailArgumentCaptor.getValue();
    var detailForm = form.getWellDetailsForm();
    assertThat(savedDiscoveryWellDetail)
        .extracting(
            DiscoveryWellDetail::getWellId,
            DiscoveryWellDetail::getTopReservoirLatitudeDegrees,
            DiscoveryWellDetail::getTopReservoirLatitudeMinutes,
            DiscoveryWellDetail::getTopReservoirLatitudeMinutes,
            DiscoveryWellDetail::getTopReservoirLongitudeDegrees,
            DiscoveryWellDetail::getTopReservoirLongitudeMinutes,
            DiscoveryWellDetail::getTopReservoirLongitudeSeconds,
            DiscoveryWellDetail::getTopReservoirLongitudeHemisphere,
            DiscoveryWellDetail::getTopReservoirDatum,
            DiscoveryWellDetail::getTotalDepthFormation,
            DiscoveryWellDetail::getTotalDepthFormationComments,
            DiscoveryWellDetail::getDataAcquisitionProgramme,
            DiscoveryWellDetail::getGeophysicalUpdateCommentary,
            DiscoveryWellDetail::getDiscoveryStatus,
            DiscoveryWellDetail::getChanceOfCommercialityComments,
            DiscoveryWellDetail::getTotalWellCost,
            DiscoveryWellDetail::getHydrocarbonsDiscovered,
            DiscoveryWellDetail::getSuccessRelatedCosts)
        .containsExactly(
            Integer.valueOf(detailForm.getWell()),
            detailForm.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            detailForm.getCoordinateInputForm().getLongitudeHemisphere(),
            detailForm.getReservoirDatum(),
            detailForm.getTotalDepthFormation(),
            detailForm.getTotalDepthFormationComments(),
            detailForm.getDataAcquisitionProgramme(),
            detailForm.getGeophysicalUpdate(),
            form.getDiscoveryStatus(),
            form.getChanceOfCommercialityComments(),
            form.getDryHoleCost().createBigDecimalOrNull(),
            form.getHydrocarbonsDiscovered(),
            form.getSuccessRelatedCosts().createBigDecimalOrNull()
        );
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
      verify(probabilityDistributionPersistenceService).deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT, submissionDetail);
      verify(probabilityDistributionPersistenceService).deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT, submissionDetail);
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
      verify(probabilityDistributionPersistenceService).deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT, submissionDetail);
      verify(probabilityDistributionPersistenceService).deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT, submissionDetail);
    }
  }

  @Test
  void saveDiscoveryWellDetails_whenGivenAFormNoHydrocarbons_verifyDeletedProbabilityDistribution() {
    var form = getFilledForm();
    form.setHydrocarbonsDiscovered(null);
    var wellDetails = new DiscoveryWellDetail();
    wellDetails.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(wellDetails));

    var discoveryWellDetailArgumentCaptor = ArgumentCaptor.forClass(DiscoveryWellDetail.class);
    discoveryWellDetailsPersistenceService.saveDiscoveryWellDetails(form, submissionDetail);

    verify(discoveryWellDetailsRepository, times(1))
        .save(discoveryWellDetailArgumentCaptor.capture());
    verify(probabilityDistributionPersistenceService).deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT, submissionDetail);
    verify(probabilityDistributionPersistenceService).deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT, submissionDetail);
    verify(probabilityDistributionPersistenceService).deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT, submissionDetail);
  }

  private DiscoveryWellDetailsForm getFilledForm() {
    var form = new DiscoveryWellDetailsForm();
    form.getWellDetailsForm().setWell("1");
    form.setChanceOfCommercialityComments("dryHoleComments");
    form.setDiscoveryStatus(DiscoveryStatus.TECHNICAL_DISCOVERY);
    form.getWellDetailsForm().setReservoirDatum(ReservoirDatum.ED50);
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    form.getWellDetailsForm().getCoordinateInputForm().setLatitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(46), BigDecimal.valueOf(35), BigDecimal.valueOf(35)));
    form.getWellDetailsForm().getCoordinateInputForm().setLongitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(25), BigDecimal.valueOf(25), BigDecimal.valueOf(25)));
    form.getWellDetailsForm().getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.getWellDetailsForm().setDataAcquisitionProgramme("data");
    form.getWellDetailsForm().setGeophysicalUpdate("update");
    form.getWellDetailsForm().setTotalDepthFormation("formation");
    form.setDryHoleCost(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setSuccessRelatedCosts(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.getWellDetailsForm().setTotalDepthFormationComments("Depth comments");
    return form;
  }
}