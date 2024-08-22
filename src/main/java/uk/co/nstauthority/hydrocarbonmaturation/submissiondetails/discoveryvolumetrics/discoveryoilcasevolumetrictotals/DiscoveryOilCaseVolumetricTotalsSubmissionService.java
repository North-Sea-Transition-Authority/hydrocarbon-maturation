package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DiscoveryOilCaseVolumetricTotalsSubmissionService implements SectionSubmissionService {

  private final DiscoveryOilCaseVolumetricTotalsService discoveryOilCaseVolumetricTotalsService;
  private final DiscoveryOilCaseVolumetricTotalsFormValidator discoveryOilCaseVolumetricTotalsFormValidator;

  @Autowired
  public DiscoveryOilCaseVolumetricTotalsSubmissionService(
      DiscoveryOilCaseVolumetricTotalsService discoveryOilCaseVolumetricTotalsService,
      DiscoveryOilCaseVolumetricTotalsFormValidator discoveryOilCaseVolumetricTotalsFormValidator) {
    this.discoveryOilCaseVolumetricTotalsService = discoveryOilCaseVolumetricTotalsService;
    this.discoveryOilCaseVolumetricTotalsFormValidator = discoveryOilCaseVolumetricTotalsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    var form = discoveryOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return discoveryOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
