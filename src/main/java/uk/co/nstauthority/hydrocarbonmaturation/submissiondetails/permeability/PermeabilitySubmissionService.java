package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.submission.DryWellSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class PermeabilitySubmissionService implements DryWellSectionSubmissionService {

  private final PermeabilityDryWellFormService permeabilityDryWellFormService;

  @Autowired
  public PermeabilitySubmissionService(PermeabilityDryWellFormService permeabilityDryWellFormService) {
    this.permeabilityDryWellFormService = permeabilityDryWellFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = permeabilityDryWellFormService.getPermeabilityForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = permeabilityDryWellFormService.validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }
}
