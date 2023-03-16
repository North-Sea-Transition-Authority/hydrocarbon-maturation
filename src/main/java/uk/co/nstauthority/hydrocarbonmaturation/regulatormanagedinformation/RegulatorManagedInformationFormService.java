package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Service
class RegulatorManagedInformationFormService {

  private final RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Autowired
  RegulatorManagedInformationFormService(
      RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator,
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService) {
    this.regulatorManagedInformationFormValidator = regulatorManagedInformationFormValidator;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
  }


  RegulatorManagedInformationForm getRegulatorManagedInformationForm(Resource resource) {
    return regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)
        .map(this::regulatorManagedInformationEntityToForm)
        .orElseGet(RegulatorManagedInformationForm::new);
  }

  BindingResult validate(RegulatorManagedInformationForm form, BindingResult bindingResult) {
    regulatorManagedInformationFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  private RegulatorManagedInformationForm regulatorManagedInformationEntityToForm(
      RegulatorManagedInformation regulatorManagedInformation) {
    var form = new RegulatorManagedInformationForm();

    form.setGeographicalArea(regulatorManagedInformation.getGeographicalArea());

    return form;
  }
}
