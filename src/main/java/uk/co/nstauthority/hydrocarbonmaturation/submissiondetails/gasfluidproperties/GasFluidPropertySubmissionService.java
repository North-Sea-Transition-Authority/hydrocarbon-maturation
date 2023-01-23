package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GasFluidPropertySubmissionService implements SectionSubmissionService {

  private final GasFluidPropertyFormService gasFluidPropertyFormService;

  @Autowired
  public GasFluidPropertySubmissionService(GasFluidPropertyFormService gasFluidPropertyFormService) {
    this.gasFluidPropertyFormService = gasFluidPropertyFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = gasFluidPropertyFormService.getGasFluidPropertyForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = gasFluidPropertyFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
