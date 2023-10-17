package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
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
class OilFluidPropertySummaryServiceTest {

  @Mock
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;
  
  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private OilFluidPropertySummaryService oilFluidPropertySummaryService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @Test
  void getOilFluidPropertySummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var view = oilFluidPropertySummaryService.getOilFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilFluid));

    var view = oilFluidPropertySummaryService.getOilFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilFluidProperty));

    var view = oilFluidPropertySummaryService.getOilFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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
            "3 °C"
        );
  }

  @Test
  void getSummarySection_emptyOilProperty() {
    when(oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    var summarySection = oilFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), OilFluidPropertySummaryService.OIL_FLUID_PROPERTIES_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        OilFluidPropertySummaryService.OIL_GRAVITY, ""
    );
    summaryDataView.addKeyValue(OilFluidPropertySummaryService.OIL_VISCOSITY, "");
    summaryDataView.addKeyValue(OilFluidPropertySummaryService.OIL_SULPHUR, "");
    summaryDataView.addKeyValue(OilFluidPropertySummaryService.OIL_WAX, "");
    summaryDataView.addKeyValue(OilFluidPropertySummaryService.OIL_OTHER, "");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(OilFluidPropertySummaryService.OIL_FLUID_PROPERTIES, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_nonEmptyOilProperty() {
    var oilProperty = new OilFluidProperty();
    oilProperty.setOilGravity(1);
    oilProperty.setOilViscosity(BigDecimal.valueOf(1));
    oilProperty.setOilSulphur(1);
    oilProperty.setOilWax(BigDecimal.valueOf(1));
    oilProperty.setOilOtherWaxAppearanceTemperature(1);

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilProperty));
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    var summarySection = oilFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), OilFluidPropertySummaryService.OIL_FLUID_PROPERTIES_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        OilFluidPropertySummaryService.OIL_GRAVITY, "1 °API"
    );
    summaryDataView.addKeyValue(OilFluidPropertySummaryService.OIL_VISCOSITY, "1 cP");
    summaryDataView.addKeyValue(OilFluidPropertySummaryService.OIL_SULPHUR, "1 ppm");
    summaryDataView.addKeyValue(OilFluidPropertySummaryService.OIL_WAX, "1 %");
    summaryDataView.addKeyValue(OilFluidPropertySummaryService.OIL_OTHER, "1 °C");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(OilFluidPropertySummaryService.OIL_FLUID_PROPERTIES, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_notOilField() {
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    assertThat(oilFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEqualTo(Optional.empty());
  }
}