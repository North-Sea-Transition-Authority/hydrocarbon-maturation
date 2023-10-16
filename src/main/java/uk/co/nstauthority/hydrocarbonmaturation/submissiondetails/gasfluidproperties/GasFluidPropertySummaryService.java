package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class GasFluidPropertySummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String GAS_FLUID_PROPERTIES = "Gas properties";

  public static final String GAS_SPECIFIC_GRAVITY = "Gas specific gravity (air=1)";
  public static final String GAS_H2S = "Gas H₂S";
  public static final String GAS_CO2 = "Gas CO₂";
  public static final String GAS_N2 = "Gas N₂";
  public static final String IS_CONDENSATE_PRESENT = "Is condensate present";
  public static final String CONDENSATE_GRAVITY = "Condensate gravity";
  public static final String CONDENSATE_VISCOSITY = "Condensate viscosity";
  public static final String CONDENSATE_SULPHUR = "Condensate sulphur";
  public static final String CONDENSATE_WAX = "Condensate wax";
  public static final String CONDENSATE_OTHER_INCL_WAX = "Condensate other incl wax appearance temperature";
  public static final String ADVISE_OTHER_GASES_CONCENTRATION = "Advise of any other gases and their concentrations";

  public static final int GAS_PROPERTIES_DISPLAY_ORDER = 30;

  private final GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public GasFluidPropertySummaryService(GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService,
                                        GeneralReservoirInformationService generalReservoirInformationService) {
    this.gasFluidPropertyPersistenceService = gasFluidPropertyPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  public GasFluidPropertySummaryView getGasFluidPropertySummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var gasFluidProperty = gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail);
    return gasFluidProperty.map(this::createSummaryViewFromGasFluidProperty)
        .orElse(GasFluidPropertySummaryView.empty());
  }

  private GasFluidPropertySummaryView createSummaryViewFromGasFluidProperty(GasFluidProperty gasFluidProperty) {
    return new GasFluidPropertySummaryView(
        ObjectUtils.defaultIfNull(gasFluidProperty.getGasSpecificGravity(), "").toString(),
        NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getGasH2s(), "ppm"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getGasCo2(),  "%"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getGasN2(),  "%"),
        BooleanUtil.yesNoFromBoolean(gasFluidProperty.getCondensatePresent(), ""),
        ObjectUtils.defaultIfNull(gasFluidProperty.getCondensatePresent(), true),
        NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateGravity(), "°API"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateViscosity(), "cP"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateSulphur(), "ppm"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateWax(), "%"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateOtherInclWax(), "°C"),
        ObjectUtils.defaultIfNull(gasFluidProperty.getAdviseOtherGasesConcentration(), ""));
  }


  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)) {
      return Optional.empty();
    }

    var summaryItems = List.of(getOilFluidPropertySummary(reservoirSubmissionDetail));
    return Optional.of(new SummarySection(30, summaryItems, ""));
  }

  private SummaryItem getOilFluidPropertySummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());

    gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail).ifPresentOrElse(
        gasFluidProperty -> {
          summaryDataView.addKeyValue(GAS_SPECIFIC_GRAVITY,
              ObjectUtils.defaultIfNull(gasFluidProperty.getGasSpecificGravity(), "").toString());
          summaryDataView.addKeyValue(GAS_H2S,
              NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getGasH2s(), "ppm"));
          summaryDataView.addKeyValue(GAS_CO2,
              NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getGasCo2(), "%"));
          summaryDataView.addKeyValue(GAS_N2,
              NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getGasN2(), "%"));

          summaryDataView.addKeyValue(IS_CONDENSATE_PRESENT,
              BooleanUtil.yesNoFromBoolean(gasFluidProperty.getCondensatePresent(), ""));

          if (gasFluidProperty.getCondensatePresent() == true) {
              summaryDataView.addKeyValue(CONDENSATE_GRAVITY,
                      NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateGravity(), "°API"));
              summaryDataView.addKeyValue(CONDENSATE_VISCOSITY,
                      NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateViscosity(), "cP"));
              summaryDataView.addKeyValue(CONDENSATE_SULPHUR,
                      NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateSulphur(), "ppm"));
              summaryDataView.addKeyValue(CONDENSATE_WAX,
                      NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateWax(), "%"));
              summaryDataView.addKeyValue(CONDENSATE_OTHER_INCL_WAX,
                      NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateOtherInclWax(), "°C"));
          }

          summaryDataView.addKeyValue(ADVISE_OTHER_GASES_CONCENTRATION,
              ObjectUtils.defaultIfNull(gasFluidProperty.getAdviseOtherGasesConcentration(), ""));

        }, () -> {
          summaryDataView.addKeyValue(GAS_SPECIFIC_GRAVITY, "");
          summaryDataView.addKeyValue(GAS_H2S, "");
          summaryDataView.addKeyValue(GAS_CO2, "");
          summaryDataView.addKeyValue(GAS_N2, "");
          summaryDataView.addKeyValue(IS_CONDENSATE_PRESENT, "");
          summaryDataView.addKeyValue(CONDENSATE_GRAVITY, "");
          summaryDataView.addKeyValue(CONDENSATE_VISCOSITY, "");
          summaryDataView.addKeyValue(CONDENSATE_SULPHUR, "");
          summaryDataView.addKeyValue(CONDENSATE_WAX, "");
          summaryDataView.addKeyValue(CONDENSATE_OTHER_INCL_WAX, "");
          summaryDataView.addKeyValue(ADVISE_OTHER_GASES_CONCENTRATION, "");
        }
    );

    var summaryCard = SummaryCard.simpleSummaryCard(
        summaryDataView
    );

    return SummaryItem.withCard(GAS_FLUID_PROPERTIES, summaryCard);
  }
}
