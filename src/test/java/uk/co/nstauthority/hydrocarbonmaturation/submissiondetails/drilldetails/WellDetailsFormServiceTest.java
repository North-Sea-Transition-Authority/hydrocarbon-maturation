package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.DryWellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@ExtendWith(MockitoExtension.class)
class WellDetailsFormServiceTest {

  @Mock
  private DryWellQueryService dryWellQueryService;

  @Mock
  private SearchSelectorService searchSelectorService;

  private WellDetailsFormService wellDetailsFormService;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    wellDetailsFormService = new WellDetailsFormService(searchSelectorService, dryWellQueryService);
  }

  @Test
  void wellDetailToForm_newDetail_thenEmptyForm() {
    var form = wellDetailsFormService.drillDetailToForm(new DrillDetail());
    assertThat(form)
        .hasAllNullFieldsOrPropertiesExcept("coordinateInputForm");
  }

  @Test
  void wellDetailToForm_filledDetail_thenFilledForm() {
    var drillDetail = getDrillDetail(submissionDetail);
    var form = wellDetailsFormService.drillDetailToForm(drillDetail);
    var coordinateForm = form.getCoordinateInputForm();
    assertThat(form)
        .extracting(
            WellDetailsForm::getDataAcquisitionProgramme,
            WellDetailsForm::getDryWell,
            WellDetailsForm::getReservoirDatum,
            theForm -> coordinateForm.getLatitudeCoordinates().getDegrees().asBigDecimal().get(),
            theForm -> coordinateForm.getLongitudeCoordinates().getDegrees().asBigDecimal().get(),
            theForm -> coordinateForm.getLatitudeCoordinates().getMinutes().asBigDecimal().get(),
            theForm -> coordinateForm.getLongitudeCoordinates().getMinutes().asBigDecimal().get(),
            theForm -> coordinateForm.getLatitudeCoordinates().getSeconds().asBigDecimal().get(),
            theForm -> coordinateForm.getLongitudeCoordinates().getSeconds().asBigDecimal().get(),
            theForm -> coordinateForm.getLongitudeHemisphere(),
            WellDetailsForm::getGeophysicalUpdate,
            WellDetailsForm::getTotalDepthFormation,
            WellDetailsForm::getTotalDepthFormationComments)
        .containsExactly(
            drillDetail.getDataAcquisitionProgramme(),
            drillDetail.getDryWellId(),
            drillDetail.getDatum(),
            drillDetail.getLatitudeDegrees(),
            drillDetail.getLongitudeDegrees(),
            drillDetail.getLatitudeMinutes(),
            drillDetail.getLongitudeMinutes(),
            drillDetail.getLatitudeSeconds(),
            drillDetail.getLongitudeSeconds(),
            drillDetail.getLongitudeHemisphere(),
            drillDetail.getGeophysicalUpdateCommentary(),
            drillDetail.getTotalDepthFormation(),
            drillDetail.getTotalDepthFormationComments()
        );
  }

  public static DrillDetail getDrillDetail(SubmissionDetail submissionDetail) {
    var drillDetail = new DrillDetail();
    drillDetail.setSubmissionDetail(submissionDetail);
    drillDetail.setDryWellId("dryWellId");
    drillDetail.setDryWellName("dryWellName");
    drillDetail.setDatum(ReservoirDatum.ED50);
    drillDetail.setLatitudeDegrees(BigDecimal.TEN);
    drillDetail.setLatitudeMinutes(BigDecimal.TEN);
    drillDetail.setLatitudeSeconds(BigDecimal.TEN);
    drillDetail.setLongitudeDegrees(BigDecimal.ONE);
    drillDetail.setLongitudeMinutes(BigDecimal.ONE);
    drillDetail.setLongitudeSeconds(BigDecimal.ONE);
    drillDetail.setLongitudeHemisphere(Hemisphere.EAST);
    drillDetail.setDataAcquisitionProgramme("data");
    drillDetail.setGeophysicalUpdateCommentary("update");
    drillDetail.setTotalDepthFormation("formation");
    drillDetail.setTotalDepthFormationComments("Depth comments");
    return drillDetail;
  }
}