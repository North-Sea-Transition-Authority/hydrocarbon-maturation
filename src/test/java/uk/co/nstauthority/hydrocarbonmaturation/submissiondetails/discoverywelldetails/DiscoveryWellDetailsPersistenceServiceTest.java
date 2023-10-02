package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@ExtendWith(MockitoExtension.class)
class DiscoveryWellDetailsPersistenceServiceTest {

  @Mock
  private DiscoveryWellDetailsRepository discoveryWellDetailsRepository;

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
            form.getSuccessRelatedCosts().createBigDecimalOrNull()
        );
  }

  @Test
  void saveDiscoveryWellDetails_whenGivenAForm_verifyDeletedProbabilityDistribution() {
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
            form.getSuccessRelatedCosts().createBigDecimalOrNull()
        );
  }

  private DiscoveryWellDetailsForm getFilledForm() {
    var form = new DiscoveryWellDetailsForm();
    form.getWellDetailsForm().setWell("1");
    form.setChanceOfCommercialityComments("dryHoleComments");
    form.setDiscoveryStatus(DiscoveryStatus.TECHNICAL_DISCOVERY);
    form.getWellDetailsForm().setReservoirDatum(ReservoirDatum.ED50);
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