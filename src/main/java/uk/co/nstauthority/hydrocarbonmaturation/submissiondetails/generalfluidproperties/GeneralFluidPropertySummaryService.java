package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class GeneralFluidPropertySummaryService {
  private final GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @Autowired
  public GeneralFluidPropertySummaryService(
      GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService) {
    this.generalFluidPropertyPersistenceService = generalFluidPropertyPersistenceService;
  }

  public GeneralFluidPropertySummaryView getGeneralFluidPropertySummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var generalFluidProperty = generalFluidPropertyPersistenceService.getGeneralFluidProperty(submissionDetail);
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
}
