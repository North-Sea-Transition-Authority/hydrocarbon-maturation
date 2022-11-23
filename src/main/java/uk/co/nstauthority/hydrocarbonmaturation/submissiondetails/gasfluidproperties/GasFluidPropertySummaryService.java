package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class GasFluidPropertySummaryService {

  private final GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  @Autowired
  public GasFluidPropertySummaryService(GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService) {
    this.gasFluidPropertyPersistenceService = gasFluidPropertyPersistenceService;
  }

  public GasFluidPropertySummaryView getGasFluidPropertySummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var gasFluidProperty = gasFluidPropertyPersistenceService.getGasFluidProperty(submissionDetail);
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
        NumberUtil.getNumberStringWithUnitsOrEmpty(gasFluidProperty.getCondensateOtherInclWax(), "°F"),
        ObjectUtils.defaultIfNull(gasFluidProperty.getAdviseOtherGasesConcentration(), ""));
  }
}
