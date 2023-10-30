package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class ProspectMixedCaseOutputsSubmissionService implements ReservoirSectionSubmissionService {

  private final ProspectMixedCaseOutputsFormService prospectMixedCaseOutputsFormService;
  private final ProspectMixedCaseOutputsFormValidator prospectMixedCaseOutputsFormValidator;

  @Autowired
  public ProspectMixedCaseOutputsSubmissionService(ProspectMixedCaseOutputsFormService prospectMixedCaseOutputsFormService,
                                                   ProspectMixedCaseOutputsFormValidator prospectMixedCaseOutputsFormValidator) {
    this.prospectMixedCaseOutputsFormService = prospectMixedCaseOutputsFormService;
    this.prospectMixedCaseOutputsFormValidator = prospectMixedCaseOutputsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isProspectSubmission()) {
      return true;
    }
    var form = prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    return prospectMixedCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
