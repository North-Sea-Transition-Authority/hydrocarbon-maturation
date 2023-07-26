package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilCaseOutputsSubmissionService implements ReservoirSectionSubmissionService {

  private final OilCaseOutputsFormService oilCaseOutputsFormService;

  @Autowired
  public OilCaseOutputsSubmissionService(OilCaseOutputsFormService oilCaseOutputsFormService) {
    this.oilCaseOutputsFormService = oilCaseOutputsFormService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = oilCaseOutputsFormService.getOilCaseOutputsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    bindingResult = oilCaseOutputsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
