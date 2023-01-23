package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilFluidPropertySubmissionService implements SectionSubmissionService {

  private final OilFluidPropertyFormService oilFluidPropertyFormService;

  @Autowired
  public OilFluidPropertySubmissionService(OilFluidPropertyFormService oilFluidPropertyFormService) {
    this.oilFluidPropertyFormService = oilFluidPropertyFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = oilFluidPropertyFormService.getOilFluidPropertyForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = oilFluidPropertyFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
