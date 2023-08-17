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
  private final OilCaseVolumetricsFormValidator oilCaseVolumetricsFormValidator;

  @Autowired
  public OilCaseVolumetricsSubmissionService(OilCaseVolumetricsFormService oilCaseVolumetricsFormService,
                                             OilCaseVolumetricsFormValidator oilCaseVolumetricsFormValidator) {
    this.oilCaseVolumetricsFormService = oilCaseVolumetricsFormService;
    this.oilCaseVolumetricsFormValidator = oilCaseVolumetricsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    return oilCaseVolumetricsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
