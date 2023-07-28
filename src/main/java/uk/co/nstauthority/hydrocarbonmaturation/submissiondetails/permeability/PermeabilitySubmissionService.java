package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class PermeabilitySubmissionService implements ReservoirSectionSubmissionService {

  private final PermeabilityFormService permeabilityFormService;

  @Autowired
  public PermeabilitySubmissionService(PermeabilityFormService permeabilityFormService) {
    this.permeabilityFormService = permeabilityFormService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    bindingResult = permeabilityFormService.validate(form, bindingResult,
        ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
