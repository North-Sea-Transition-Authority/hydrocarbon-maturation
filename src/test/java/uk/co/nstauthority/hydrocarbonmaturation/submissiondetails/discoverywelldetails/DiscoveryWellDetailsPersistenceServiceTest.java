package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@ExtendWith(MockitoExtension.class)
class DiscoveryWellDetailsPersistenceServiceTest {

  @Mock
  private DiscoveryWellDetailsRepository discoveryWellDetailsRepository;

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
            DiscoveryWellDetail::getDryWellName,
            DiscoveryWellDetail::getLatitudeDegrees,
            DiscoveryWellDetail::getLatitudeMinutes,
            DiscoveryWellDetail::getLatitudeMinutes,
            DiscoveryWellDetail::getLongitudeDegrees,
            DiscoveryWellDetail::getLongitudeMinutes,
            DiscoveryWellDetail::getLongitudeSeconds,
            DiscoveryWellDetail::getLongitudeHemisphere,
            DiscoveryWellDetail::getDatum,
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
            DiscoveryWellDetail::getDryWellName,
            DiscoveryWellDetail::getLatitudeDegrees,
            DiscoveryWellDetail::getLatitudeMinutes,
            DiscoveryWellDetail::getLatitudeMinutes,
            DiscoveryWellDetail::getLongitudeDegrees,
            DiscoveryWellDetail::getLongitudeMinutes,
            DiscoveryWellDetail::getLongitudeSeconds,
            DiscoveryWellDetail::getLongitudeHemisphere,
            DiscoveryWellDetail::getDatum,
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
            detailForm.getDryWell(),
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

  private DiscoveryWellDetailsForm getFilledForm() {
    var form = new DiscoveryWellDetailsForm();
    form.getWellDetailsForm().setDryWell("dryWellName");
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