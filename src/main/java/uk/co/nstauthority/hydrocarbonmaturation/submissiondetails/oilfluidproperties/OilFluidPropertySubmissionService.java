package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilFluidPropertySubmissionService implements ReservoirSectionSubmissionService {

  private final OilFluidPropertyFormService oilFluidPropertyFormService;
  private final OilFluidPropertyFormValidator oilFluidPropertyFormValidator;

  @Autowired
  public OilFluidPropertySubmissionService(OilFluidPropertyFormService oilFluidPropertyFormService,
                                           OilFluidPropertyFormValidator oilFluidPropertyFormValidator) {
    this.oilFluidPropertyFormService = oilFluidPropertyFormService;
    this.oilFluidPropertyFormValidator = oilFluidPropertyFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return oilFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
  }
}
