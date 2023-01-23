package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class SourceAndSealSubmissionService implements SectionSubmissionService {

  private final SourceAndSealFormService sourceAndSealFormService;

  @Autowired
  public SourceAndSealSubmissionService(SourceAndSealFormService sourceAndSealFormService) {
    this.sourceAndSealFormService = sourceAndSealFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = sourceAndSealFormService.getSourceAndSealForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = sourceAndSealFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
