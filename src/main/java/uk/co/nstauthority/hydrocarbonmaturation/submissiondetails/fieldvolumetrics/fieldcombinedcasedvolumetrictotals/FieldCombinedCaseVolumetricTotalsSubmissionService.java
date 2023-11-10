package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals;

import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FieldCombinedCaseVolumetricTotalsSubmissionService implements SectionSubmissionService {
  private final FieldCombinedCaseVolumetricTotalsService fieldCombinedCaseVolumetricTotalsService;
  private final FieldCombinedCaseVolumetricTotalsFormValidator fieldCombinedCaseVolumetricTotalsFormValidator;

  public FieldCombinedCaseVolumetricTotalsSubmissionService(
      FieldCombinedCaseVolumetricTotalsService fieldCombinedCaseVolumetricTotalsService,
      FieldCombinedCaseVolumetricTotalsFormValidator fieldCombinedCaseVolumetricTotalsFormValidator) {

    this.fieldCombinedCaseVolumetricTotalsService = fieldCombinedCaseVolumetricTotalsService;
    this.fieldCombinedCaseVolumetricTotalsFormValidator = fieldCombinedCaseVolumetricTotalsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.isFieldSubmission()) {
      var form = fieldCombinedCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);
      BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
      return fieldCombinedCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
    }

    return true;
  }
}
