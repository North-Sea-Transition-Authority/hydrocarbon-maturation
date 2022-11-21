package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.DryWellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@Service
public class WellDetailsFormService {

  private final SearchSelectorService searchSelectorService;
  private final DryWellQueryService dryWellQueryService;

  @Autowired
  WellDetailsFormService(SearchSelectorService searchSelectorService, DryWellQueryService dryWellQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.dryWellQueryService = dryWellQueryService;
  }

  public WellDetailsForm drillDetailToForm(DrillDetail drillDetail) {
    var form = new WellDetailsForm();
    form.setDryWell(drillDetail.getDryWellId());
    form.setReservoirDatum(drillDetail.getDatum());
    form.setDataAcquisitionProgramme(drillDetail.getDataAcquisitionProgramme());
    form.setGeophysicalUpdate(drillDetail.getGeophysicalUpdateCommentary());
    var latitudeCoordinates =
        CoordinateInput.createCoordinateInputForForm(
            drillDetail.getLatitudeDegrees(),
            drillDetail.getLatitudeMinutes(),
            drillDetail.getLatitudeSeconds());
    var longitudeCoordinates =
        CoordinateInput.createCoordinateInputForForm(
            drillDetail.getLongitudeDegrees(),
            drillDetail.getLongitudeMinutes(),
            drillDetail.getLongitudeSeconds());
    var hemisphere = drillDetail.getLongitudeHemisphere();
    var coordinateInputForm = new CoordinateInputForm();
    coordinateInputForm.setLongitudeCoordinates(longitudeCoordinates);
    coordinateInputForm.setLatitudeCoordinates(latitudeCoordinates);
    coordinateInputForm.setLongitudeHemisphere(hemisphere);
    form.setCoordinateInputForm(coordinateInputForm);
    form.setTotalDepthFormation(drillDetail.getTotalDepthFormation());
    form.setTotalDepthFormationComments(drillDetail.getTotalDepthFormationComments());

    return form;
  }

  public Map<String, String> getPreSelectedDryWell(String dryWell) {
    Map<String, String> preSelectedMap = Map.of();
    if (dryWell != null) {
      preSelectedMap = searchSelectorService.buildPrePopulatedSelections(
          Collections.singletonList(dryWell),
          Map.of(
              dryWell,
              dryWellQueryService.getDryWellByName(dryWell).get(0).getSelectionText()
          )
      );
    }
    return preSelectedMap;
  }
}
