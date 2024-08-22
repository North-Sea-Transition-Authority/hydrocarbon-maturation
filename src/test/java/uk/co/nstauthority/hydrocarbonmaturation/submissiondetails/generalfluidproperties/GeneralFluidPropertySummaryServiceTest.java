package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;

@ExtendWith(MockitoExtension.class)
class GeneralFluidPropertySummaryServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @InjectMocks
  private GeneralFluidPropertySummaryService generalFluidPropertySummaryService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(SubmissionType.CREATE_COP);
  }

  @Test
  void getGeneralFluidPropertySummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var view = generalFluidPropertySummaryService.getGeneralFluidPropertySummaryFromSubmissionDetail(reservoirSubmissionDetail);

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

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(generalFluid));

    var view = generalFluidPropertySummaryService.getGeneralFluidPropertySummaryFromSubmissionDetail(reservoirSubmissionDetail);

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

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(generalFluidProperty));

    var view = generalFluidPropertySummaryService.getGeneralFluidPropertySummaryFromSubmissionDetail(reservoirSubmissionDetail);

    assertThat(view)
        .extracting(
            GeneralFluidPropertySummaryView::datumDepth,
            GeneralFluidPropertySummaryView::temperatureAtDatumDepth,
            GeneralFluidPropertySummaryView::pressureAtDatumDepth,
            GeneralFluidPropertySummaryView::saturationPressureAtDatumDepth,
            GeneralFluidPropertySummaryView::waterSalinity)
        .containsExactly(
            "1 m tvdss",
            "2 °C",
            "3 psia",
            "4 psia",
            "5 ppm"
        );
  }

  @Test
  void getSummarySection_emptyGeneralProperty() {
    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var summarySection = generalFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GeneralFluidPropertySummaryService.GENERAL_PROPERTIES_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralFluidPropertySummaryService.DATUM_DEPTH, ""
    );
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.TEMPERATURE_AT_DATUM_DEPTH, "");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.INITIAL_TEMPERATURE, "");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.PRESSURE_AT_DATUM_DEPTH, "");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.INITIAL_PRESSURE, "");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.SATURATION_PRESSURE_AT_DATUM_DEPTH, "");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.WATER_SALINITY, "");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(GeneralFluidPropertySummaryService.GENERAL_FLUID_PROPERTIES, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_nonEmptyGeneralProperty() {
    var generalProperty = getValidGeneralProperty();

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(generalProperty));

    var summarySection = generalFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GeneralFluidPropertySummaryService.GENERAL_PROPERTIES_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralFluidPropertySummaryService.DATUM_DEPTH, "1 m tvdss"
    );
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.TEMPERATURE_AT_DATUM_DEPTH, "1 °C");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.INITIAL_TEMPERATURE, "1 °C");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.PRESSURE_AT_DATUM_DEPTH, "1 psia");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.INITIAL_PRESSURE, "1 psia");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.SATURATION_PRESSURE_AT_DATUM_DEPTH, "1 psia");
    summaryDataView.addKeyValue(GeneralFluidPropertySummaryService.WATER_SALINITY, "1 ppm");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(GeneralFluidPropertySummaryService.GENERAL_FLUID_PROPERTIES, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test  void getSummarySection_isNotCop_thenNoInitialFields() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_CSR);

    var generalProperty = getValidGeneralProperty();

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(generalProperty));

    var summarySection = generalFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    var summaryCards = summarySection.get().summaryItems().get(0).summaryCards();
    var summaryData = ((SummaryDataView) summaryCards.get(0).summaryData()).keyValues();

    assertThat(summaryData)
        .extracting(SummaryKeyValue::key)
        .isNotEmpty()
        .doesNotContain(
            GeneralFluidPropertySummaryService.INITIAL_TEMPERATURE,
            GeneralFluidPropertySummaryService.INITIAL_PRESSURE
        );

  }

  @Test
  void getSummarySection_isCop_thenNoInitialFields() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_COP);

    var generalProperty = getValidGeneralProperty();

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(generalProperty));

    var summarySection = generalFluidPropertySummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    var summaryCards = summarySection.get().summaryItems().get(0).summaryCards();
    var summaryData = ((SummaryDataView) summaryCards.get(0).summaryData()).keyValues();

    assertThat(summaryData)
        .extracting(SummaryKeyValue::key)
        .contains(
            GeneralFluidPropertySummaryService.INITIAL_TEMPERATURE,
            GeneralFluidPropertySummaryService.INITIAL_PRESSURE
        );

  }

  private GeneralFluidProperty getValidGeneralProperty() {
    var generalProperty = new GeneralFluidProperty();
    generalProperty.setDatumDepth(BigDecimal.ONE);
    generalProperty.setTemperatureAtDatumDepth(1);
    generalProperty.setInitialTemperature(1);
    generalProperty.setPressureAtDatumDepth(1);
    generalProperty.setInitialPressure(1);
    generalProperty.setSaturationPressureAtDatumDepth(1);
    generalProperty.setWaterSalinity(1);

    return generalProperty;
  }
}