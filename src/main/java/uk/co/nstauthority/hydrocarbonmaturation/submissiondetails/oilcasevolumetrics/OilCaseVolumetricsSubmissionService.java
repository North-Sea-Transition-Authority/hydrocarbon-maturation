package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilCaseVolumetricsSubmissionService implements SectionSubmissionService {

  private final OilCaseVolumetricsFormService oilCaseVolumetricsFormService;

  @Autowired
  public OilCaseVolumetricsSubmissionService(OilCaseVolumetricsFormService oilCaseVolumetricsFormService) {
    this.oilCaseVolumetricsFormService = oilCaseVolumetricsFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = oilCaseVolumetricsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
