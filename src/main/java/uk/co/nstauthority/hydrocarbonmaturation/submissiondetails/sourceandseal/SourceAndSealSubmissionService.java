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
  private final SourceAndSealFormValidator sourceAndSealFormValidator;

  @Autowired
  public SourceAndSealSubmissionService(SourceAndSealFormService sourceAndSealFormService,
                                        SourceAndSealFormValidator sourceAndSealFormValidator) {
    this.sourceAndSealFormService = sourceAndSealFormService;
    this.sourceAndSealFormValidator = sourceAndSealFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = sourceAndSealFormService.getSourceAndSealForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return sourceAndSealFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
