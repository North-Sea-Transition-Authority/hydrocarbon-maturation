package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class OilFluidPropertySummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String OIL_FLUID_PROPERTIES = "Oil properties";
  public static final String OIL_GRAVITY = "Oil gravity";
  public static final String OIL_VISCOSITY = "Oil viscosity";
  public static final String OIL_SULPHUR = "Oil sulphur";
  public static final String OIL_WAX = "Oil wax";
  public static final String OIL_OTHER = "Oil other incl wax appearance temperature";

  public static final int OIL_FLUID_PROPERTIES_DISPLAY_ORDER = 80;

  private final OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  OilFluidPropertySummaryService(OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService,
                                 GeneralReservoirInformationService generalReservoirInformationService) {
    this.oilFluidPropertyPersistenceService = oilFluidPropertyPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  public OilFluidPropertySummaryView getOilFluidPropertySummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var oilFluidProperty = oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail);
    return oilFluidProperty.map(this::createSummaryViewFromOilFluidProperty)
        .orElse(OilFluidPropertySummaryView.empty());
  }

  private OilFluidPropertySummaryView createSummaryViewFromOilFluidProperty(OilFluidProperty oilFluidProperty) {
    return new OilFluidPropertySummaryView(
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilGravity(), "°API"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilViscosity(), "cP"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilSulphur(), "ppm"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilWax(), "%"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilOtherWaxAppearanceTemperature(), "°C"));
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail, User user) {
    if (generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)) {
      return Optional.empty();
    }
    
    var summaryItems = List.of(getOilFluidPropertySummary(reservoirSubmissionDetail));
    return Optional.of(new SummarySection(OIL_FLUID_PROPERTIES_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getOilFluidPropertySummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail).ifPresentOrElse(
        oilFluidProperty -> {
          summaryDataView.addKeyValue(OIL_GRAVITY,
              NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilGravity(), "°API"));
          summaryDataView.addKeyValue(OIL_VISCOSITY,
              NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilViscosity(), "cP"));
          summaryDataView.addKeyValue(OIL_SULPHUR,
              NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilSulphur(), "ppm"));
          summaryDataView.addKeyValue(OIL_WAX,
              NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilWax(), "%"));
          summaryDataView.addKeyValue(OIL_OTHER,
              NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilOtherWaxAppearanceTemperature(), "°C"));
        }, () -> {
          summaryDataView.addKeyValue(OIL_GRAVITY, "");
          summaryDataView.addKeyValue(OIL_VISCOSITY, "");
          summaryDataView.addKeyValue(OIL_SULPHUR, "");
          summaryDataView.addKeyValue(OIL_WAX, "");
          summaryDataView.addKeyValue(OIL_OTHER, "");
        });

    var summaryCard = SummaryCard.simpleSummaryCard(
        summaryDataView
    );

    return SummaryItem.withCard(OIL_FLUID_PROPERTIES, summaryCard);
  }
}
