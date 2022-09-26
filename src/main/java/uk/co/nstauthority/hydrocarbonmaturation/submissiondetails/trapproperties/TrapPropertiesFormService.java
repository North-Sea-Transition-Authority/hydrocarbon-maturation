package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class TrapPropertiesFormService implements SubmissionFormService {

  private final TrapPropertiesFormValidator trapPropertiesFormValidator;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  @Autowired
  TrapPropertiesFormService(TrapPropertiesFormValidator trapPropertiesFormValidator,
                            TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                            ProbabilityDistributionFormService probabilityDistributionFormService) {
    this.trapPropertiesFormValidator = trapPropertiesFormValidator;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    trapPropertiesFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  TrapPropertiesForm getTrapPropertiesForm(SubmissionDetail submissionDetail) {
    return trapPropertiesPersistenceService.getTrapProperty(submissionDetail)
        .map(this::trapPropertyEntityToForm)
        .orElse(new TrapPropertiesForm());
  }

  private TrapPropertiesForm trapPropertyEntityToForm(TrapProperty trapProperty) {
    var form = new TrapPropertiesForm();
    form.setTrapType(trapProperty.getTrapType());
    form.setTrapDescription(trapProperty.getTrapDescription());
    form.setOtherTrapType(trapProperty.getOtherTrapType());

    var latitudeCoordinates = createCoordinateInputForEntity(trapProperty.getLatitudeDegrees(),
        trapProperty.getLatitudeMinutes(), trapProperty.getLatitudeSeconds());

    var longitudeCoordinates = createCoordinateInputForEntity(trapProperty.getLongitudeDegrees(),
        trapProperty.getLongitudeMinutes(), trapProperty.getLongitudeSeconds());

    form.setLatitudeCoordinates(latitudeCoordinates);
    form.setLongitudeCoordinates(longitudeCoordinates);
    form.setLongitudeHemisphere(trapProperty.getLongitudeHemisphere());

    var submissionDetail = trapProperty.getSubmissionDetail();
    form.setDepthToCrest(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT_DISCOVERY
    ));

    form.setDepthToSpillPoint(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY
    ));

    form.setProductiveArea(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA
    ));

    return form;
  }

  private CoordinateInput createCoordinateInputForEntity(BigDecimal degrees, BigDecimal minutes, BigDecimal seconds) {
    var coordinateInput = new CoordinateInput();

    var degreesCoordinate = degrees == null ? new DecimalInput() : new DecimalInput(degrees.toString());
    var minutesCoordinate = minutes == null ? new DecimalInput() : new DecimalInput(minutes.toString());
    var secondsCoordinate = seconds == null ? new DecimalInput() : new DecimalInput(seconds.toString());

    coordinateInput.setDegrees(degreesCoordinate);
    coordinateInput.setMinutes(minutesCoordinate);
    coordinateInput.setSeconds(secondsCoordinate);

    return coordinateInput;
  }
}
