package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class OilFluidPropertySummaryServiceTest {

  @Mock
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  @InjectMocks
  private OilFluidPropertySummaryService oilFluidPropertySummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getOilFluidPropertySummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(oilFluidPropertyPersistenceService.getOilFluidProperty(submissionDetail))
        .thenReturn(Optional.empty());

    var view = oilFluidPropertySummaryService.getOilFluidPropertySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            OilFluidPropertySummaryView::oilGravity,
            OilFluidPropertySummaryView::oilViscosity,
            OilFluidPropertySummaryView::oilSulphur,
            OilFluidPropertySummaryView::oilWax,
            OilFluidPropertySummaryView::oilOtherWaxAppearanceTemperature)
        .containsOnly("");
  }

  @Test
  void getOilFluidPropertySummaryFromSubmissionDetail_whenEmpty_emptySummary() {
    var oilFluid = new OilFluidProperty();

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(submissionDetail))
        .thenReturn(Optional.of(oilFluid));

    var view = oilFluidPropertySummaryService.getOilFluidPropertySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            OilFluidPropertySummaryView::oilGravity,
            OilFluidPropertySummaryView::oilViscosity,
            OilFluidPropertySummaryView::oilSulphur,
            OilFluidPropertySummaryView::oilWax,
            OilFluidPropertySummaryView::oilOtherWaxAppearanceTemperature)
        .containsOnly("");
  }

  @Test
  void getOilFluidPropertySummaryFromSubmissionDetail_whenPresent_fullSummary() {
    var oilFluidProperty = new OilFluidProperty();
    oilFluidProperty.setOilGravity(5);
    oilFluidProperty.setOilViscosity(BigDecimal.TEN);
    oilFluidProperty.setOilSulphur(4);
    oilFluidProperty.setOilWax(BigDecimal.ONE);
    oilFluidProperty.setOilOtherWaxAppearanceTemperature(3);

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(submissionDetail))
        .thenReturn(Optional.of(oilFluidProperty));

    var view = oilFluidPropertySummaryService.getOilFluidPropertySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            OilFluidPropertySummaryView::oilGravity,
            OilFluidPropertySummaryView::oilViscosity,
            OilFluidPropertySummaryView::oilSulphur,
            OilFluidPropertySummaryView::oilWax,
            OilFluidPropertySummaryView::oilOtherWaxAppearanceTemperature)
        .containsExactly(
            "5 °API",
            "10 cP",
            "4 ppm",
            "1 %",
            "3 °F"
        );
  }
}