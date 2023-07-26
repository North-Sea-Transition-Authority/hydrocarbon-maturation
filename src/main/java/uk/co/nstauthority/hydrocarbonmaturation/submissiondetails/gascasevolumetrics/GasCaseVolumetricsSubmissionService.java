package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GasCaseVolumetricsSubmissionService implements ReservoirSectionSubmissionService {

  private final GasCaseVolumetricsFormService gasCaseVolumetricsFormService;

  @Autowired
  public GasCaseVolumetricsSubmissionService(GasCaseVolumetricsFormService gasCaseVolumetricsFormService) {
    this.gasCaseVolumetricsFormService = gasCaseVolumetricsFormService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    bindingResult = gasCaseVolumetricsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
