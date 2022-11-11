package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.DryWellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class DryWellDetailsFormService implements SubmissionFormService {

  private final DryWellDetailsFormValidator dryWellDetailsFormValidator;
  private final DryWellDetailsPersistenceService dryWellDetailsPersistenceService;
  private final SearchSelectorService searchSelectorService;
  private final DryWellQueryService dryWellQueryService;

  @Autowired
  public DryWellDetailsFormService(DryWellDetailsFormValidator dryWellDetailsFormValidator,
                                   DryWellDetailsPersistenceService dryWellDetailsPersistenceService,
                                   SearchSelectorService searchSelectorService,
                                   DryWellQueryService dryWellQueryService) {
    this.dryWellDetailsFormValidator = dryWellDetailsFormValidator;
    this.dryWellDetailsPersistenceService = dryWellDetailsPersistenceService;
    this.searchSelectorService = searchSelectorService;
    this.dryWellQueryService = dryWellQueryService;
  }

  DryWellDetailsForm getDryWellDetailsForm(SubmissionDetail submissionDetail) {
    return dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail)
        .map(this::dryWellDetailEntityToForm)
        .orElse(new DryWellDetailsForm());
  }

  private DryWellDetailsForm dryWellDetailEntityToForm(DryWellDetail dryWellDetail) {
    var form = new DryWellDetailsForm();
    form.setDryHoleCost(DecimalInput.createDecimalInput(dryWellDetail.getDryHoleCost()));
    form.setDryWell(dryWellDetail.getDryWellName());
    var latitudeCoordinates =
        CoordinateInput.createCoordinateInputForForm(
            dryWellDetail.getLatitudeDegrees(),
            dryWellDetail.getLatitudeMinutes(),
            dryWellDetail.getLatitudeSeconds());
    var longitudeCoordinates =
        CoordinateInput.createCoordinateInputForForm(
            dryWellDetail.getLongitudeDegrees(),
            dryWellDetail.getLongitudeMinutes(),
            dryWellDetail.getLongitudeSeconds());
    var hemisphere = dryWellDetail.getLongitudeHemisphere();
    var coordinateInputForm = new CoordinateInputForm();
    coordinateInputForm.setLongitudeCoordinates(longitudeCoordinates);
    coordinateInputForm.setLatitudeCoordinates(latitudeCoordinates);
    coordinateInputForm.setLongitudeHemisphere(hemisphere);
    form.setCoordinateInputForm(coordinateInputForm);
    form.setDryHoleScope(dryWellDetail.getDryHoleScope());
    form.setDataAcquisitionProgramme(dryWellDetail.getDataAcquisitionProgramme());
    form.setDryHoleDescription(dryWellDetail.getDryHoleDescription());
    form.setDryHoleComments(dryWellDetail.getDryHoleComments());
    form.setDryHoleType(dryWellDetail.getDryHoleType());
    form.setTotalDepthFormation(dryWellDetail.getTotalDepthFormation());
    form.setReservoirDatum(dryWellDetail.getDatum());
    form.setGeophysicalUpdate(dryWellDetail.getGeophysicalUpdateCommentary());
    form.setTotalDepthFormationComments(dryWellDetail.getTotalDepthFormationComments());
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

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    dryWellDetailsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }
}
