package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.numberinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class HydrocarbonCaseFormService implements SubmissionFormService {

  private final HydrocarbonCaseFormValidator hydrocarbonCaseFormValidator;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  public HydrocarbonCaseFormService(HydrocarbonCaseFormValidator hydrocarbonCaseFormValidator,
                                    HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.hydrocarbonCaseFormValidator = hydrocarbonCaseFormValidator;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    hydrocarbonCaseFormValidator.validate(form, bindingResult, validationType);
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
      form.setGasOnlyProbability(DecimalInput.createDecimalInput(hydrocarbonCaseDetail.getGasOnlyProbability()));
    }

    if (hydrocarbonCaseDetail.getOilOnly() != null && hydrocarbonCaseDetail.getOilOnly()) {
      form.setOilOnlyProbability(DecimalInput.createDecimalInput(hydrocarbonCaseDetail.getOilOnlyProbability()));
    }

    if (hydrocarbonCaseDetail.getOilAndGas() != null && hydrocarbonCaseDetail.getOilAndGas()) {
      form.setOilAndGasProbability(DecimalInput.createDecimalInput(hydrocarbonCaseDetail.getOilAndGasProbability()));
    }

    return form;
  }


}
