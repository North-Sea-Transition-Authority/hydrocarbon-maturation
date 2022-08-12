package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class HydrocarbonCaseService {

  private final HydrocarbonCaseFormValidator hydrocarbonCaseFormValidator;
  private final HydrocarbonCaseRepository hydrocarbonCaseRepository;

  @Autowired
  public HydrocarbonCaseService(HydrocarbonCaseFormValidator hydrocarbonCaseFormValidator,
                                HydrocarbonCaseRepository hydrocarbonCaseRepository) {
    this.hydrocarbonCaseFormValidator = hydrocarbonCaseFormValidator;
    this.hydrocarbonCaseRepository = hydrocarbonCaseRepository;
  }

  BindingResult validate(HydrocarbonCaseForm form, BindingResult bindingResult) {
    hydrocarbonCaseFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  @Transactional
  public void saveHydrocarbonCase(HydrocarbonCaseForm form, SubmissionDetail submissionDetail) {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    hydrocarbonCase.setSubmissionDetail(submissionDetail);
    hydrocarbonCase.setOilOnly(form.getOilOnlyCaseType());
    hydrocarbonCase.setGasOnly(form.getGasOnlyCaseType());
    hydrocarbonCase.setOilAndGas(form.getOilAndGasCaseType());
    hydrocarbonCase.setOilOnlyProbability(form.getOilOnlyProbability().createBigDecimalOrNull());
    hydrocarbonCase.setGasOnlyProbability(form.getGasOnlyProbability().createBigDecimalOrNull());
    hydrocarbonCase.setOilAndGasProbability(form.getOilAndGasProbability().createBigDecimalOrNull());
    hydrocarbonCase.setOilOnlyComments(form.getOilOnlyComments());
    hydrocarbonCase.setGasOnlyComments(form.getGasOnlyComments());
    hydrocarbonCase.setOilAndGasComments(form.getOilAndGasComments());

    hydrocarbonCaseRepository.save(hydrocarbonCase);
  }

}
