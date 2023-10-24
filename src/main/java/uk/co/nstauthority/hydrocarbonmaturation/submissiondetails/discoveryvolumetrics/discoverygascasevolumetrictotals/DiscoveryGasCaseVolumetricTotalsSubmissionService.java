package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DiscoveryGasCaseVolumetricTotalsSubmissionService implements SectionSubmissionService {

  private final DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService;
  private final DiscoveryGasCaseVolumetricTotalsFormValidator discoveryGasCaseVolumetricTotalsFormValidator;

  @Autowired
  public DiscoveryGasCaseVolumetricTotalsSubmissionService(
      DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService,
      DiscoveryGasCaseVolumetricTotalsFormValidator discoveryGasCaseVolumetricTotalsFormValidator) {
    this.discoveryGasCaseVolumetricTotalsService = discoveryGasCaseVolumetricTotalsService;
    this.discoveryGasCaseVolumetricTotalsFormValidator = discoveryGasCaseVolumetricTotalsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = discoveryGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return discoveryGasCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
