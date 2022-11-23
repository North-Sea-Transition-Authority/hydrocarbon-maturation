package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class OilFluidPropertySummaryService {

  private final OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  @Autowired
  OilFluidPropertySummaryService(OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService) {
    this.oilFluidPropertyPersistenceService = oilFluidPropertyPersistenceService;
  }

  public OilFluidPropertySummaryView getOilFluidPropertySummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var oilFluidProperty = oilFluidPropertyPersistenceService.getOilFluidProperty(submissionDetail);
    return oilFluidProperty.map(this::createSummaryViewFromOilFluidProperty)
        .orElse(OilFluidPropertySummaryView.empty());
  }

  private OilFluidPropertySummaryView createSummaryViewFromOilFluidProperty(OilFluidProperty oilFluidProperty) {
    return new OilFluidPropertySummaryView(
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilGravity(), "°API"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilViscosity(),  "cP"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilSulphur(),  "ppm"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilWax(), "%"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(oilFluidProperty.getOilOtherWaxAppearanceTemperature(), "°F"));
  }
}
