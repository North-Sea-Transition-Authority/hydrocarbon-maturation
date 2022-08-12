package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class ReservoirPropertiesFormService {

  private final ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Autowired
  public ReservoirPropertiesFormService(ReservoirPropertiesFormValidator reservoirPropertiesFormValidator,
                                        ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService) {
    this.reservoirPropertiesFormValidator = reservoirPropertiesFormValidator;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
  }

  BindingResult validate(ReservoirPropertiesForm form, BindingResult bindingResult) {
    reservoirPropertiesFormValidator.validate(form, bindingResult);
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
