package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

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
class GeneralFluidPropertySummaryServiceTest {

  @Mock
  private GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @InjectMocks
  private GeneralFluidPropertySummaryService generalFluidPropertySummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getGeneralFluidPropertySummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(submissionDetail))
        .thenReturn(Optional.empty());

    var view = generalFluidPropertySummaryService.getGeneralFluidPropertySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GeneralFluidPropertySummaryView::datumDepth,
            GeneralFluidPropertySummaryView::temperatureAtDatumDepth,
            GeneralFluidPropertySummaryView::pressureAtDatumDepth,
            GeneralFluidPropertySummaryView::saturationPressureAtDatumDepth,
            GeneralFluidPropertySummaryView::waterSalinity)
        .containsOnly("");
  }

  @Test
  void getGeneralFluidPropertySummaryFromSubmissionDetail_whenEmpty_emptySummary() {
    var generalFluid = new GeneralFluidProperty();

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(submissionDetail))
        .thenReturn(Optional.of(generalFluid));

    var view = generalFluidPropertySummaryService.getGeneralFluidPropertySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GeneralFluidPropertySummaryView::datumDepth,
            GeneralFluidPropertySummaryView::temperatureAtDatumDepth,
            GeneralFluidPropertySummaryView::pressureAtDatumDepth,
            GeneralFluidPropertySummaryView::saturationPressureAtDatumDepth,
            GeneralFluidPropertySummaryView::waterSalinity)
        .containsOnly("");
  }

  @Test
  void getGeneralFluidPropertySummaryFromSubmissionDetail_whenPresent_fullSummary() {
    var generalFluidProperty = new GeneralFluidProperty();
    generalFluidProperty.setDatumDepth(BigDecimal.ONE);
    generalFluidProperty.setTemperatureAtDatumDepth(2);
    generalFluidProperty.setPressureAtDatumDepth(3);
    generalFluidProperty.setSaturationPressureAtDatumDepth(4);
    generalFluidProperty.setWaterSalinity(5);

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(submissionDetail))
        .thenReturn(Optional.of(generalFluidProperty));

    var view = generalFluidPropertySummaryService.getGeneralFluidPropertySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GeneralFluidPropertySummaryView::datumDepth,
            GeneralFluidPropertySummaryView::temperatureAtDatumDepth,
            GeneralFluidPropertySummaryView::pressureAtDatumDepth,
            GeneralFluidPropertySummaryView::saturationPressureAtDatumDepth,
            GeneralFluidPropertySummaryView::waterSalinity)
        .containsExactly(
            "1 ft tvdss",
            "2 °F",
            "3 psia",
            "4 psia",
            "5 ppm"
        );
  }
}