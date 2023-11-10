package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FieldOilCaseVolumetricTotalsSubmissionService implements SectionSubmissionService {

  private final FieldOilCaseVolumetricTotalsService fieldOilCaseVolumetricTotalsService;
  private final FieldOilCaseVolumetricTotalsFormValidator fieldOilCaseVolumetricTotalsFormValidator;

  public FieldOilCaseVolumetricTotalsSubmissionService(
      FieldOilCaseVolumetricTotalsService fieldOilCaseVolumetricTotalsService,
      FieldOilCaseVolumetricTotalsFormValidator fieldOilCaseVolumetricTotalsFormValidator) {
    this.fieldOilCaseVolumetricTotalsService = fieldOilCaseVolumetricTotalsService;
    this.fieldOilCaseVolumetricTotalsFormValidator = fieldOilCaseVolumetricTotalsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.isFieldSubmission()) {
      var form = fieldOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);
      BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
      return fieldOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
    }

    return true;
  }
}
