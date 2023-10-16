package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralFluidPropertySubmissionService implements ReservoirSectionSubmissionService {

  private final GeneralFluidPropertyFormService generalFluidPropertyFormService;
  private final GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator;

  @Autowired
  public GeneralFluidPropertySubmissionService(GeneralFluidPropertyFormService generalFluidPropertyFormService,
                                               GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator) {
    this.generalFluidPropertyFormService = generalFluidPropertyFormService;
    this.generalFluidPropertyFormValidator = generalFluidPropertyFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = generalFluidPropertyFormService.getGeneralFluidPropertyForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return generalFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
  }
}
