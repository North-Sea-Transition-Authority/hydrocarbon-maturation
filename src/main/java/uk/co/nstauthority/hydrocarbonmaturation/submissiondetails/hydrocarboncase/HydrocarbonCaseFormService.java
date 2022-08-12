package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInput;

@Service
class HydrocarbonCaseFormService {

  private final HydrocarbonCaseFormValidator hydrocarbonCaseFormValidator;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  public HydrocarbonCaseFormService(HydrocarbonCaseFormValidator hydrocarbonCaseFormValidator,
                                    HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.hydrocarbonCaseFormValidator = hydrocarbonCaseFormValidator;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
  }

  BindingResult validate(HydrocarbonCaseForm form, BindingResult bindingResult) {
    hydrocarbonCaseFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  HydrocarbonCaseForm getHydrocarbonCaseForm(SubmissionDetail submissionDetail) {
    return hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail)
        .map(this::hydrocarbonCaseDetailEntityToForm)
        .orElse(new HydrocarbonCaseForm());
  }

  private HydrocarbonCaseForm hydrocarbonCaseDetailEntityToForm(HydrocarbonCaseDetail hydrocarbonCaseDetail) {
    var form = new HydrocarbonCaseForm();
    form.setOilOnlyCaseType(hydrocarbonCaseDetail.getOilOnly());
    form.setOilOnlyComments(hydrocarbonCaseDetail.getOilOnlyComments());
    form.setGasOnlyCaseType(hydrocarbonCaseDetail.getGasOnly());
    form.setGasOnlyComments(hydrocarbonCaseDetail.getGasOnlyComments());
    form.setOilAndGasCaseType(hydrocarbonCaseDetail.getOilAndGas());
    form.setOilAndGasComments(hydrocarbonCaseDetail.getOilAndGasComments());

    if (hydrocarbonCaseDetail.getGasOnly() != null && hydrocarbonCaseDetail.getGasOnly()) {
      form.setGasOnlyProbability(new DecimalInput(hydrocarbonCaseDetail.getGasOnlyProbability().toString()));
    }
    if (hydrocarbonCaseDetail.getOilOnly() != null && hydrocarbonCaseDetail.getOilOnly()) {
      form.setOilOnlyProbability(new DecimalInput(hydrocarbonCaseDetail.getOilOnlyProbability().toString()));
    }
    if (hydrocarbonCaseDetail.getOilAndGas() != null && hydrocarbonCaseDetail.getOilAndGas()) {
      form.setOilAndGasProbability(new DecimalInput(hydrocarbonCaseDetail.getOilAndGasProbability().toString()));
    }

    return form;
  }
}
