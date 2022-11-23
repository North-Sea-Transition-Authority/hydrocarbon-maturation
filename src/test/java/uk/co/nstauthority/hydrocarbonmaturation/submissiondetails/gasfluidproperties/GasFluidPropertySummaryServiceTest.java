package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

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
class GasFluidPropertySummaryServiceTest {

  @Mock
  private GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  @InjectMocks
  private GasFluidPropertySummaryService gasFluidPropertySummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getGasFluidPropertySummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(gasFluidPropertyPersistenceService.getGasFluidProperty(submissionDetail))
        .thenReturn(Optional.empty());

    var view = gasFluidPropertySummaryService.getGasFluidPropertySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GasFluidPropertySummaryView::gasGravity,
            GasFluidPropertySummaryView::gasH2s,
            GasFluidPropertySummaryView::gasCo2,
            GasFluidPropertySummaryView::gasN2,
            GasFluidPropertySummaryView::condensatePresent,
            GasFluidPropertySummaryView::showCondensate,
            GasFluidPropertySummaryView::condensateGravity,
            GasFluidPropertySummaryView::condensateViscosity,
            GasFluidPropertySummaryView::condensateSulphur,
            GasFluidPropertySummaryView::condensateWax,
            GasFluidPropertySummaryView::condensateOtherInclWax,
            GasFluidPropertySummaryView::adviseOtherGasesConcentration)
        .containsOnly("", true);
  }

  @Test
  void getGasFluidPropertySummaryFromSubmissionDetail_whenEmpty_emptySummary() {
    var gasFluid = new GasFluidProperty();

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(submissionDetail))
        .thenReturn(Optional.of(gasFluid));

    var view = gasFluidPropertySummaryService.getGasFluidPropertySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GasFluidPropertySummaryView::gasGravity,
            GasFluidPropertySummaryView::gasH2s,
            GasFluidPropertySummaryView::gasCo2,
            GasFluidPropertySummaryView::gasN2,
            GasFluidPropertySummaryView::condensatePresent,
            GasFluidPropertySummaryView::showCondensate,
            GasFluidPropertySummaryView::condensateGravity,
            GasFluidPropertySummaryView::condensateViscosity,
            GasFluidPropertySummaryView::condensateSulphur,
            GasFluidPropertySummaryView::condensateWax,
            GasFluidPropertySummaryView::condensateOtherInclWax,
            GasFluidPropertySummaryView::adviseOtherGasesConcentration)
        .containsOnly("", true);
  }

  @Test
  void getGasFluidPropertySummaryFromSubmissionDetail_whenPresent_fullSummary() {
    var gasFluidProperty = new GasFluidProperty();
    gasFluidProperty.setGasSpecificGravity(BigDecimal.ONE);
    gasFluidProperty.setGasH2s(BigDecimal.TEN);
    gasFluidProperty.setGasCo2(BigDecimal.ONE);
    gasFluidProperty.setGasN2(BigDecimal.TEN);
    gasFluidProperty.setCondensatePresent(true);
    gasFluidProperty.setCondensateGravity(BigDecimal.ONE);
    gasFluidProperty.setCondensateViscosity(BigDecimal.TEN);
    gasFluidProperty.setCondensateSulphur(BigDecimal.ONE);
    gasFluidProperty.setCondensateWax(BigDecimal.TEN);
    gasFluidProperty.setCondensateOtherInclWax(BigDecimal.ONE);
    gasFluidProperty.setAdviseOtherGasesConcentration("advise of gases");

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(submissionDetail))
        .thenReturn(Optional.of(gasFluidProperty));

    var view = gasFluidPropertySummaryService.getGasFluidPropertySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GasFluidPropertySummaryView::gasGravity,
            GasFluidPropertySummaryView::gasH2s,
            GasFluidPropertySummaryView::gasCo2,
            GasFluidPropertySummaryView::gasN2,
            GasFluidPropertySummaryView::condensatePresent,
            GasFluidPropertySummaryView::showCondensate,
            GasFluidPropertySummaryView::condensateGravity,
            GasFluidPropertySummaryView::condensateViscosity,
            GasFluidPropertySummaryView::condensateSulphur,
            GasFluidPropertySummaryView::condensateWax,
            GasFluidPropertySummaryView::condensateOtherInclWax,
            GasFluidPropertySummaryView::adviseOtherGasesConcentration)
        .containsExactly(
            "1",
            "10 ppm",
            "1 %",
            "10 %",
            "Yes",
            true,
            "1 °API",
            "10 cP",
            "1 ppm",
            "10 %",
            "1 °F",
            "advise of gases"
        );
  }
}