package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@Service
public class WellDetailsFormService {

  private final SearchSelectorService searchSelectorService;
  private final WellQueryService wellQueryService;

  @Autowired
  WellDetailsFormService(SearchSelectorService searchSelectorService, WellQueryService wellQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.wellQueryService = wellQueryService;
  }

  public WellDetailsForm drillDetailToForm(DrillDetail drillDetail) {
    var form = new WellDetailsForm();
    if (drillDetail.getWellId() != null) {
      form.setWell(drillDetail.getWellId().toString());
    }
    form.setReservoirDatum(drillDetail.getTopReservoirDatum());
    form.setDataAcquisitionProgramme(drillDetail.getDataAcquisitionProgramme());
    form.setGeophysicalUpdate(drillDetail.getGeophysicalUpdateCommentary());
    var latitudeCoordinates =
        CoordinateInput.createCoordinateInputForForm(
            drillDetail.getTopReservoirLatitudeDegrees(),
            drillDetail.getTopReservoirLatitudeMinutes(),
            drillDetail.getTopReservoirLatitudeSeconds());
    var longitudeCoordinates =
        CoordinateInput.createCoordinateInputForForm(
            drillDetail.getTopReservoirLongitudeDegrees(),
            drillDetail.getTopReservoirLongitudeMinutes(),
            drillDetail.getTopReservoirLongitudeSeconds());
    var hemisphere = drillDetail.getTopReservoirLongitudeHemisphere();
    var coordinateInputForm = new CoordinateInputForm();
    coordinateInputForm.setLongitudeCoordinates(longitudeCoordinates);
    coordinateInputForm.setLatitudeCoordinates(latitudeCoordinates);
    coordinateInputForm.setLongitudeHemisphere(hemisphere);
    form.setCoordinateInputForm(coordinateInputForm);
    form.setTotalDepthFormation(drillDetail.getTotalDepthFormation());
    form.setTotalDepthFormationComments(drillDetail.getTotalDepthFormationComments());

    return form;
  }

  public Map<String, String> getPreSelectedWell(String wellId) {
    Map<String, String> preSelectedMap = Map.of();

    if (wellId != null) {
      var key = wellQueryService.findWellById(Integer.valueOf(wellId))
          .map(WellView::getSelectionText)
          .orElse("");
      return searchSelectorService.buildPrePopulatedSelection(
          wellId,
          key
      );
    }

    return preSelectedMap;
  }
}
