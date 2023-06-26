package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@ExtendWith(MockitoExtension.class)
class WellDetailsFormServiceTest {

  @Mock
  private WellQueryService wellQueryService;

  @Mock
  private SearchSelectorService searchSelectorService;

  private WellDetailsFormService wellDetailsFormService;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
    wellDetailsFormService = new WellDetailsFormService(searchSelectorService, wellQueryService);
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
            WellDetailsForm::getWell,
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
            drillDetail.getWellId().toString(),
            drillDetail.getTopReservoirDatum(),
            drillDetail.getTopReservoirLatitudeDegrees(),
            drillDetail.getTopReservoirLongitudeDegrees(),
            drillDetail.getTopReservoirLatitudeMinutes(),
            drillDetail.getTopReservoirLongitudeMinutes(),
            drillDetail.getTopReservoirLatitudeSeconds(),
            drillDetail.getTopReservoirLongitudeSeconds(),
            drillDetail.getTopReservoirLongitudeHemisphere(),
            drillDetail.getGeophysicalUpdateCommentary(),
            drillDetail.getTotalDepthFormation(),
            drillDetail.getTotalDepthFormationComments()
        );
  }

  @Test
  void getPreSelectedWell_whenNull_assertEmptyMap() {
    var result = wellDetailsFormService.getPreSelectedWell(null);
    assertThat(result).isEqualTo(Map.of());
    verifyNoInteractions(wellQueryService);
  }

  @Test
  void getPreSelectedWell_whenNotNull_assertSelectedWell() {
    var expectedMap = Map.of("1", "v2");

    when(searchSelectorService.buildPrePopulatedSelection("1", "K1"))
        .thenReturn(expectedMap);
    when(wellQueryService.findWellById(1))
        .thenReturn(Optional.of(new WellView("1", "K1")));
    var result = wellDetailsFormService.getPreSelectedWell("1");
    assertThat(result).isEqualTo(expectedMap);
  }

  public static DrillDetail getDrillDetail(SubmissionDetail submissionDetail) {
    var drillDetail = new DrillDetail();
    drillDetail.setSubmissionDetail(submissionDetail);
    drillDetail.setWellId(1);
    drillDetail.setWellReference("dryWellName");
    drillDetail.setTopReservoirDatum(ReservoirDatum.ED50);
    drillDetail.setTopReservoirLatitudeDegrees(BigDecimal.TEN);
    drillDetail.setTopReservoirLatitudeMinutes(BigDecimal.TEN);
    drillDetail.setTopReservoirLatitudeSeconds(BigDecimal.TEN);
    drillDetail.setTopReservoirLongitudeDegrees(BigDecimal.ONE);
    drillDetail.setTopReservoirLongitudeMinutes(BigDecimal.ONE);
    drillDetail.setTopReservoirLongitudeSeconds(BigDecimal.ONE);
    drillDetail.setTopReservoirLongitudeHemisphere(Hemisphere.EAST);
    drillDetail.setDataAcquisitionProgramme("data");
    drillDetail.setGeophysicalUpdateCommentary("update");
    drillDetail.setTotalDepthFormation("formation");
    drillDetail.setTotalDepthFormationComments("Depth comments");
    return drillDetail;
  }
}