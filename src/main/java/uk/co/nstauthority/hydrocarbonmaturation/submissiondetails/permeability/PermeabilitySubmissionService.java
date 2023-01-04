package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class PermeabilitySubmissionService implements SectionSubmissionService {

  private final PermeabilityFormService permeabilityFormService;

  @Autowired
  public PermeabilitySubmissionService(PermeabilityFormService permeabilityFormService) {
    this.permeabilityFormService = permeabilityFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = permeabilityFormService.getPermeabilityForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = permeabilityFormService.validate(form, bindingResult,
        ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
