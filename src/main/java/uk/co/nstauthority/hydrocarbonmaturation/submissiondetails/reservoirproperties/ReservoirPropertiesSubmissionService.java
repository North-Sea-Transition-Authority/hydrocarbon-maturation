package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.submission.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class ReservoirPropertiesSubmissionService implements SectionSubmissionService {

  private final ReservoirPropertiesFormService reservoirPropertiesFormService;

  @Autowired
  public ReservoirPropertiesSubmissionService(ReservoirPropertiesFormService reservoirPropertiesFormService) {
    this.reservoirPropertiesFormService = reservoirPropertiesFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = reservoirPropertiesFormService.validate(
        form, bindingResult,
        ValidationType.FULL, submissionDetail.getSubmissionType());
    return !bindingResult.hasErrors();
  }
}
