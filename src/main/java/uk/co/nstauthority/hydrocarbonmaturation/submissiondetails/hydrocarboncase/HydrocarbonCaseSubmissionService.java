package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class HydrocarbonCaseSubmissionService implements SectionSubmissionService {

  private final HydrocarbonCaseFormService hydrocarbonCaseFormService;

  @Autowired
  public HydrocarbonCaseSubmissionService(HydrocarbonCaseFormService hydrocarbonCaseFormService) {
    this.hydrocarbonCaseFormService = hydrocarbonCaseFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = hydrocarbonCaseFormService.getHydrocarbonCaseForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = hydrocarbonCaseFormService.validate(
        form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
