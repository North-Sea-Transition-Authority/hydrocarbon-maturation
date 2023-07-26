package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class MixedCaseOutputsSubmissionService implements ReservoirSectionSubmissionService {

  private final MixedCaseOutputsFormService mixedCaseOutputsFormService;

  @Autowired
  public MixedCaseOutputsSubmissionService(MixedCaseOutputsFormService mixedCaseOutputsFormService) {
    this.mixedCaseOutputsFormService = mixedCaseOutputsFormService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = mixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    bindingResult = mixedCaseOutputsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
