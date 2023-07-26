package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class MixedCaseVolumetricsSubmissionService implements ReservoirSectionSubmissionService {

  private final MixedCaseVolumetricsFormService mixedCaseVolumetricsFormService;

  @Autowired
  public MixedCaseVolumetricsSubmissionService(MixedCaseVolumetricsFormService mixedCaseVolumetricsFormService) {
    this.mixedCaseVolumetricsFormService = mixedCaseVolumetricsFormService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = mixedCaseVolumetricsFormService.getMixedCaseVolumetricsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    bindingResult = mixedCaseVolumetricsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
