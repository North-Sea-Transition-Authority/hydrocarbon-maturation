package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralFluidPropertySubmissionService implements SectionSubmissionService {

  private final GeneralFluidPropertyFormService generalFluidPropertyFormService;

  @Autowired
  public GeneralFluidPropertySubmissionService(GeneralFluidPropertyFormService generalFluidPropertyFormService) {
    this.generalFluidPropertyFormService = generalFluidPropertyFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = generalFluidPropertyFormService.getGeneralFluidPropertyForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = generalFluidPropertyFormService.validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }
}
