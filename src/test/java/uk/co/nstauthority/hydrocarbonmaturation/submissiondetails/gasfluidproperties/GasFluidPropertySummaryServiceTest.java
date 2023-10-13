package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.GAS_PROPERTIES_DISPLAY_ORDER;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

@ExtendWith(MockitoExtension.class)
class GasFluidPropertySummaryServiceTest {

  @Mock
  private GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private GasFluidPropertySummaryService gasFluidPropertySummaryService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @Test
  void getGasFluidPropertySummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var view = gasFluidPropertySummaryService.getGasFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(gasFluid));

    var view = gasFluidPropertySummaryService.getGasFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(gasFluidProperty));

    var view = gasFluidPropertySummaryService.getGasFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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
            "1 °C",
            "advise of gases"
        );
  }

  @Test
  void getSummarySection_emptyGasProperty() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
            .thenReturn(false);
    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var summarySection = gasFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GAS_PROPERTIES_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GasFluidPropertySummaryService.GAS_SPECIFIC_GRAVITY, ""
    );
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.GAS_H2S, "");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.GAS_CO2, "");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.GAS_N2, "");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.IS_CONDENSATE_PRESENT, "");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_GRAVITY, "");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_VISCOSITY, "");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_SULPHUR, "");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_WAX, "");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_OTHER_INCL_WAX, "");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.ADVISE_OTHER_GASES_CONCENTRATION, "");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(GasFluidPropertySummaryService.GAS_FLUID_PROPERTIES, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_nonEmptyGasProperty() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);
    var gasProperty = new GasFluidProperty();
    gasProperty.setGasSpecificGravity(BigDecimal.valueOf(1));
    gasProperty.setGasH2s(BigDecimal.valueOf(2));
    gasProperty.setGasCo2(BigDecimal.valueOf(3));
    gasProperty.setGasN2(BigDecimal.valueOf(4));
    gasProperty.setCondensatePresent(true);
    gasProperty.setCondensateGravity(BigDecimal.valueOf(5));
    gasProperty.setCondensateViscosity(BigDecimal.valueOf(6));
    gasProperty.setCondensateSulphur(BigDecimal.valueOf(7));
    gasProperty.setCondensateWax(BigDecimal.valueOf(8));
    gasProperty.setCondensateOtherInclWax(BigDecimal.valueOf(9));
    gasProperty.setAdviseOtherGasesConcentration("Other gases concentration");

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(gasProperty));

    var summarySection = gasFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GAS_PROPERTIES_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GasFluidPropertySummaryService.GAS_SPECIFIC_GRAVITY, "1"
    );
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.GAS_H2S, "2 ppm");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.GAS_CO2, "3 %");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.GAS_N2, "4 %");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.IS_CONDENSATE_PRESENT, "Yes");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_GRAVITY, "5 °API");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_VISCOSITY, "6 cP");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_SULPHUR, "7 ppm");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_WAX, "8 %");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.CONDENSATE_OTHER_INCL_WAX, "9 °C");
    summaryDataView.addKeyValue(GasFluidPropertySummaryService.ADVISE_OTHER_GASES_CONCENTRATION, "Other gases concentration");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(GasFluidPropertySummaryService.GAS_FLUID_PROPERTIES, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_oilOnly() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    assertThat(gasFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

}