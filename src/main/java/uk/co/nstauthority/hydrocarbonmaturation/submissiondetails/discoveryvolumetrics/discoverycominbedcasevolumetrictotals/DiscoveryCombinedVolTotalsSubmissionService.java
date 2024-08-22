package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DiscoveryCombinedVolTotalsSubmissionService implements SectionSubmissionService {

  private final DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService;
  private final DiscoveryCombinedCaseVolTotalsFormValidator discoveryCombinedCaseVolTotalsFormValidator;

  @Autowired
  public DiscoveryCombinedVolTotalsSubmissionService(
      DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService,
      DiscoveryCombinedCaseVolTotalsFormValidator discoveryCombinedCaseVolTotalsFormValidator) {
    this.discoveryCombinedCaseVolTotalsService = discoveryCombinedCaseVolTotalsService;
    this.discoveryCombinedCaseVolTotalsFormValidator = discoveryCombinedCaseVolTotalsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    var form = discoveryCombinedCaseVolTotalsService.submissionDetailToForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return discoveryCombinedCaseVolTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
