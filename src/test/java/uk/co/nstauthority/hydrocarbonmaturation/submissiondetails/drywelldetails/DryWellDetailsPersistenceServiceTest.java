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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@ExtendWith(MockitoExtension.class)
class DryWellDetailsPersistenceServiceTest {

  @Mock
  private DryWellDetailsRepository dryWellDetailsRepository;

  @Mock
  private WellQueryService wellQueryService;

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
    var detailForm = form.getDrillDetailsForm();
    assertThat(savedDryWellDetail)
        .extracting(
            DryWellDetail::getWellId,
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
            DryWellDetail::getDryHoleCost,
            DryWellDetail::getMigrationFailureCategory,
            DryWellDetail::getReservoirFailureCategory,
            DryWellDetail::getSourceFailureCategory,
            DryWellDetail::getSealFailureCategory,
            DryWellDetail::getTrapFailureCategory)
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
            form.getDryHoleType(),
            form.getDryHoleDescription(),
            form.getDryHoleScope(),
            form.getDryHoleComments(),
            form.getDryHoleCost().createBigDecimalOrNull(),
            form.getMigrationFailureCategory(),
            form.getReservoirFailureCategory(),
            form.getSourceFailureCategory(),
            form.getSealFailureCategory(),
            form.getTrapFailureCategory()
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
    var detailForm = form.getDrillDetailsForm();
    assertThat(savedDryWellDetail)
        .extracting(
            DryWellDetail::getWellId,
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
            DryWellDetail::getDryHoleCost,
            DryWellDetail::getMigrationFailureCategory,
            DryWellDetail::getReservoirFailureCategory,
            DryWellDetail::getSourceFailureCategory,
            DryWellDetail::getSealFailureCategory,
            DryWellDetail::getTrapFailureCategory)
        .containsExactly(
            detailForm.getWell(),
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
            form.getDryHoleType(),
            form.getDryHoleDescription(),
            form.getDryHoleScope(),
            form.getDryHoleComments(),
            form.getDryHoleCost().createBigDecimalOrNull(),
            form.getMigrationFailureCategory(),
            form.getReservoirFailureCategory(),
            form.getSourceFailureCategory(),
            form.getSealFailureCategory(),
            form.getTrapFailureCategory()
        );
  }

  @Test
  void getDryWellDetail_verifyMethodCall() {
    dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail);
    verify(dryWellDetailsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  private DryWellDetailsForm getFilledForm() {
    var form = new DryWellDetailsForm();
    form.getDrillDetailsForm().setWell("1");
    form.setDryHoleComments("dryHoleComments");
    form.setDryHoleScope("dryHoleScope");
    form.getDrillDetailsForm().setReservoirDatum(ReservoirDatum.ED50);
    form.setDryHoleDescription("description");
    form.getDrillDetailsForm().getCoordinateInputForm().setLatitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(46), BigDecimal.valueOf(35), BigDecimal.valueOf(35)));
    form.getDrillDetailsForm().getCoordinateInputForm().setLongitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(25), BigDecimal.valueOf(25), BigDecimal.valueOf(25)));
    form.getDrillDetailsForm().getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.getDrillDetailsForm().setDataAcquisitionProgramme("data");
    form.getDrillDetailsForm().setGeophysicalUpdate("update");
    form.getDrillDetailsForm().setTotalDepthFormation("formation");
    form.setDryHoleType(DryHoleType.GAS);
    form.setDryHoleCost(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.getDrillDetailsForm().setTotalDepthFormationComments("Depth comments");
    form.setMigrationFailureCategory(MigrationFailureCategory.PROSPECT_NOT_ON_MIGRATION);
    form.setSourceFailureCategory(SourceFailureCategory.SOURCE_NOT_MATURE);
    form.setSealFailureCategory(SealFailureCategory.SEAL_LITHOLOGY_NOT_PRESENT);
    form.setReservoirFailureCategory(ReservoirFailureCategory.RESERVOIR_INTERVAL_POOR_QUALITY);
    form.setTrapFailureCategory(TrapFailureCategory.STRATIGRAPHIC_TRAP_COMPONENTS_NOT_PRESENT);
    return form;
  }


}