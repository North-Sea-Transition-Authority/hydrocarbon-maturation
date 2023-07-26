package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilCaseVolumetricsSubmissionService implements ReservoirSectionSubmissionService {

  private final OilCaseVolumetricsFormService oilCaseVolumetricsFormService;

  @Autowired
  public OilCaseVolumetricsSubmissionService(OilCaseVolumetricsFormService oilCaseVolumetricsFormService) {
    this.oilCaseVolumetricsFormService = oilCaseVolumetricsFormService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    bindingResult = oilCaseVolumetricsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
