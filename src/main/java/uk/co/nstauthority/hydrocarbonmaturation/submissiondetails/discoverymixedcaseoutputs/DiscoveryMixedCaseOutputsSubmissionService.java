package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverymixedcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DiscoveryMixedCaseOutputsSubmissionService implements ReservoirSectionSubmissionService {

  private final DiscoveryMixedCaseOutputsFormService discoveryMixedCaseOutputsFormService;
  private final DiscoveryMixedCaseOutputsFormValidator discoveryMixedCaseOutputsFormValidator;

  @Autowired
  public DiscoveryMixedCaseOutputsSubmissionService(
      DiscoveryMixedCaseOutputsFormService discoveryMixedCaseOutputsFormService,
      DiscoveryMixedCaseOutputsFormValidator discoveryMixedCaseOutputsFormValidator) {
    this.discoveryMixedCaseOutputsFormService = discoveryMixedCaseOutputsFormService;
    this.discoveryMixedCaseOutputsFormValidator = discoveryMixedCaseOutputsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = discoveryMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    return discoveryMixedCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
