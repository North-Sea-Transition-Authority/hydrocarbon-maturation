package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service

public class FieldGasCaseVolumetricTotalsSubmissionService implements SectionSubmissionService {

  private final FieldGasCaseVolumetricTotalsService fieldGasCaseVolumetricTotalsService;
  private final FieldGasCaseVolumetricTotalsFormValidator fieldGasCaseVolumetricTotalsFormValidator;

  @Autowired
  public FieldGasCaseVolumetricTotalsSubmissionService(
      FieldGasCaseVolumetricTotalsService fieldGasCaseVolumetricTotalsService,
      FieldGasCaseVolumetricTotalsFormValidator fieldGasCaseVolumetricTotalsFormValidator) {

    this.fieldGasCaseVolumetricTotalsService = fieldGasCaseVolumetricTotalsService;
    this.fieldGasCaseVolumetricTotalsFormValidator = fieldGasCaseVolumetricTotalsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.isFieldSubmission()) {
      var form = fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail);
      BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
      return fieldGasCaseVolumetricTotalsFormValidator
          .isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
    }

    return true;
  }
}
