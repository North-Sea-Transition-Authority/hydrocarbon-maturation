package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class ReservoirPropertiesSubmissionService implements ReservoirSectionSubmissionService {

  private final ReservoirPropertiesFormService reservoirPropertiesFormService;

  @Autowired
  public ReservoirPropertiesSubmissionService(ReservoirPropertiesFormService reservoirPropertiesFormService) {
    this.reservoirPropertiesFormService = reservoirPropertiesFormService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    bindingResult = reservoirPropertiesFormService.validate(
        form, bindingResult,
        ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
