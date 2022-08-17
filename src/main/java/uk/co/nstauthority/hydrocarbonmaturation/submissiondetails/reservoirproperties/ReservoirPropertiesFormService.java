package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class ReservoirPropertiesFormService implements SubmissionFormService {

  private final ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Autowired
  public ReservoirPropertiesFormService(ReservoirPropertiesFormValidator reservoirPropertiesFormValidator,
                                        ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService) {
    this.reservoirPropertiesFormValidator = reservoirPropertiesFormValidator;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
  }

  @Override
  public BindingResult validate(Object form, BindingResult bindingResult, ValidationType validationType) {
    reservoirPropertiesFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  ReservoirPropertiesForm getReservoirPropertiesForm(SubmissionDetail submissionDetail) {
    return reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail)
        .map(this::reservoirPropertyToEntity)
        .orElse(new ReservoirPropertiesForm());
  }

  private ReservoirPropertiesForm reservoirPropertyToEntity(ReservoirProperty reservoirProperty) {
    var form = new ReservoirPropertiesForm();
    form.setFormationName(reservoirProperty.getFormationName());
    form.setAgeStageOrSeries(reservoirProperty.getAgeStage());
    form.setLithology(reservoirProperty.getLithology());

    return form;
  }
}
