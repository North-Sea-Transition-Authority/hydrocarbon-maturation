package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@ExtendWith(MockitoExtension.class)
class DryWellDetailsPersistenceServiceTest {

  @Mock
  private DryWellDetailsRepository dryWellDetailsRepository;

  @InjectMocks
  private DryWellDetailsPersistenceService dryWellDetailsPersistenceService;

  private SubmissionDetail submissionDetail;

  private CoordinateInput latitudeCoordinate = new CoordinateInput();
  private CoordinateInput longitudeCoordinate = new CoordinateInput();


  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    latitudeCoordinate.setDegrees(new DecimalInput("45"));
    latitudeCoordinate.setMinutes(new DecimalInput("4"));
    latitudeCoordinate.setSeconds(new DecimalInput("5.3"));
    longitudeCoordinate.setDegrees(new DecimalInput("30"));
    longitudeCoordinate.setMinutes(new DecimalInput("4"));
    longitudeCoordinate.setSeconds(new DecimalInput("5.3"));
  }

  @Test
  void saveDryWellDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = getFilledForm();

    var dryWellDetailsCaptor = ArgumentCaptor.forClass(DryWellDetail.class);
    dryWellDetailsPersistenceService.saveDryWellDetails(form, submissionDetail);

    verify(dryWellDetailsRepository, times(1))
        .save(dryWellDetailsCaptor.capture());
    var savedDryWellDetail = dryWellDetailsCaptor.getValue();

    assertThat(savedDryWellDetail)
        .extracting(
            DryWellDetail::getDryWellName,
            DryWellDetail::getLatitudeDegrees,
            DryWellDetail::getLatitudeMinutes,
            DryWellDetail::getLatitudeMinutes,
            DryWellDetail::getLongitudeDegrees,
            DryWellDetail::getLongitudeMinutes,
            DryWellDetail::getLongitudeSeconds,
            DryWellDetail::getLongitudeHemisphere,
            DryWellDetail::getDatum,
            DryWellDetail::getTotalDepthFormation,
            DryWellDetail::getTotalDepthFormationComments,
            DryWellDetail::getDataAcquisitionProgramme,
            DryWellDetail::getGeophysicalUpdateCommentary,
            DryWellDetail::getDryHoleType,
            DryWellDetail::getDryHoleDescription,
            DryWellDetail::getDryHoleScope,
            DryWellDetail::getDryHoleComments,
            DryWellDetail::getDryHoleCost)
        .containsExactly(
            form.getDryWell(),
            form.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeHemisphere(),
            form.getReservoirDatum(),
            form.getTotalDepthFormation(),
            form.getTotalDepthFormationComments(),
            form.getDataAcquisitionProgramme(),
            form.getGeophysicalUpdate(),
            form.getDryHoleType(),
            form.getDryHoleDescription(),
            form.getDryHoleScope(),
            form.getDryHoleComments(),
            form.getDryHoleCost().createBigDecimalOrNull()
        );
  }

  @Test
  void saveDryWellDetails_whenGivenEmptyForm_verifyCreatedEntity() {
    var form = new DryWellDetailsForm();

    var dryWellDetailsCaptor = ArgumentCaptor.forClass(DryWellDetail.class);
    dryWellDetailsPersistenceService.saveDryWellDetails(form, submissionDetail);

    verify(dryWellDetailsRepository, times(1))
        .save(dryWellDetailsCaptor.capture());
    var savedDryWellDetail = dryWellDetailsCaptor.getValue();

    assertThat(savedDryWellDetail)
        .extracting(
            DryWellDetail::getDryWellName,
            DryWellDetail::getLatitudeDegrees,
            DryWellDetail::getLatitudeMinutes,
            DryWellDetail::getLatitudeMinutes,
            DryWellDetail::getLongitudeDegrees,
            DryWellDetail::getLongitudeMinutes,
            DryWellDetail::getLongitudeSeconds,
            DryWellDetail::getLongitudeHemisphere,
            DryWellDetail::getDatum,
            DryWellDetail::getTotalDepthFormation,
            DryWellDetail::getTotalDepthFormationComments,
            DryWellDetail::getDataAcquisitionProgramme,
            DryWellDetail::getGeophysicalUpdateCommentary,
            DryWellDetail::getDryHoleType,
            DryWellDetail::getDryHoleDescription,
            DryWellDetail::getDryHoleScope,
            DryWellDetail::getDryHoleComments,
            DryWellDetail::getDryHoleCost)
        .containsExactly(
            form.getDryWell(),
            form.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeHemisphere(),
            form.getReservoirDatum(),
            form.getTotalDepthFormation(),
            form.getTotalDepthFormationComments(),
            form.getDataAcquisitionProgramme(),
            form.getGeophysicalUpdate(),
            form.getDryHoleType(),
            form.getDryHoleDescription(),
            form.getDryHoleScope(),
            form.getDryHoleComments(),
            form.getDryHoleCost().createBigDecimalOrNull()
        );
  }

  @Test
  void getDryWellDetail_verifyMethodCall() {
    dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail);
    verify(dryWellDetailsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  private DryWellDetailsForm getFilledForm() {
    var form = new DryWellDetailsForm();
    form.setDryWell("dryWellName");
    form.setDryHoleComments("dryHoleComments");
    form.setDryHoleScope("dryHoleScope");
    form.setReservoirDatum(ReservoirDatum.ED50);
    form.setDryHoleDescription("description");
    form.getCoordinateInputForm().setLatitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(46), BigDecimal.valueOf(35), BigDecimal.valueOf(35)));
    form.getCoordinateInputForm().setLongitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(25), BigDecimal.valueOf(25), BigDecimal.valueOf(25)));
    form.getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.setDataAcquisitionProgramme("data");
    form.setGeophysicalUpdate("update");
    form.setTotalDepthFormation("formation");
    form.setDryHoleType(DryHoleType.GAS);
    form.setDryHoleCost(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setTotalDepthFormationComments("Depth comments");
    return form;
  }


}