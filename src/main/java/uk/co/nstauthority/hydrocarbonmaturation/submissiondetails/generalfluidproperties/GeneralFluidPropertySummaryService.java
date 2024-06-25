package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class GeneralFluidPropertySummaryService implements SummarySectionService<ReservoirSubmissionDetail> {
  public static final String GENERAL_FLUID_PROPERTIES = "General properties";
  public static final String DATUM_DEPTH = "Datum depth";
  public static final String TEMPERATURE_AT_DATUM_DEPTH = "Temperature at datum depth";
  public static final String INITIAL_TEMPERATURE = "Initial temperature";
  public static final String PRESSURE_AT_DATUM_DEPTH = "Pressure at datum depth";
  public static final String INITIAL_PRESSURE = "Initial pressure";
  public static final String SATURATION_PRESSURE_AT_DATUM_DEPTH = "Saturation pressure at datum depth";
  public static final String WATER_SALINITY = "Water salinity";

  public static final int GENERAL_PROPERTIES_DISPLAY_ORDER = 100;

  private final GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @Autowired
  public GeneralFluidPropertySummaryService(
      GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService) {
    this.generalFluidPropertyPersistenceService = generalFluidPropertyPersistenceService;
  }

  public GeneralFluidPropertySummaryView getGeneralFluidPropertySummaryFromSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var generalFluidProperty = generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail);
    return generalFluidProperty.map(this::createSummaryViewFromGeneralFluidProperty)
        .orElse(GeneralFluidPropertySummaryView.empty());
  }

  private GeneralFluidPropertySummaryView createSummaryViewFromGeneralFluidProperty(GeneralFluidProperty generalFluidProperty) {
    return new GeneralFluidPropertySummaryView(
        NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getDatumDepth(), "m tvdss"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getTemperatureAtDatumDepth(),  "°C"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getPressureAtDatumDepth(), "psia"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getSaturationPressureAtDatumDepth(), "psia"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getWaterSalinity(), "ppm"));
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryItems = List.of(getGeneralFluidPropertySummary(reservoirSubmissionDetail));
    return Optional.of(new SummarySection(GENERAL_PROPERTIES_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getGeneralFluidPropertySummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    var isCop = reservoirSubmissionDetail.getSubmissionDetail().isCessationOfProductionSubmission();
    generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail).ifPresentOrElse(
        generalFluidProperty -> {
          summaryDataView.addKeyValue(DATUM_DEPTH,
              NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getDatumDepth(), "m tvdss"));
          summaryDataView.addKeyValue(TEMPERATURE_AT_DATUM_DEPTH,
              NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getTemperatureAtDatumDepth(), "°C"));
          if (isCop) {
            summaryDataView.addKeyValue(INITIAL_TEMPERATURE,
                NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getInitialTemperature(), "°C"));
          }
          summaryDataView.addKeyValue(PRESSURE_AT_DATUM_DEPTH,
              NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getPressureAtDatumDepth(), "psia"));
          if (isCop) {
            summaryDataView.addKeyValue(INITIAL_PRESSURE,
                NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getInitialPressure(), "psia"));
          }
          summaryDataView.addKeyValue(SATURATION_PRESSURE_AT_DATUM_DEPTH,
              NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getSaturationPressureAtDatumDepth(), "psia"));
          summaryDataView.addKeyValue(WATER_SALINITY,
              NumberUtil.getNumberStringWithUnitsOrEmpty(generalFluidProperty.getWaterSalinity(), "ppm"));
        }, () -> {
          summaryDataView.addKeyValue(DATUM_DEPTH, "");
          summaryDataView.addKeyValue(TEMPERATURE_AT_DATUM_DEPTH, "");
          summaryDataView.addKeyValue(INITIAL_TEMPERATURE, "");
          summaryDataView.addKeyValue(PRESSURE_AT_DATUM_DEPTH, "");
          summaryDataView.addKeyValue(INITIAL_PRESSURE, "");
          summaryDataView.addKeyValue(SATURATION_PRESSURE_AT_DATUM_DEPTH, "");
          summaryDataView.addKeyValue(WATER_SALINITY, "");
        });

    var summaryCard = SummaryCard.simpleSummaryCard(
        summaryDataView
    );

    return SummaryItem.withCard(GENERAL_FLUID_PROPERTIES, summaryCard);
  }
}
