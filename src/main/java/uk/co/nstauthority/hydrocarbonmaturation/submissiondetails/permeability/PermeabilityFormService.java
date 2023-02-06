package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class PermeabilityFormService implements SubmissionFormService {

  private final PermeabilityFormValidator permeabilityFormValidator;
  private final PermeabilityPersistenceService permeabilityPersistenceService;

  @Autowired
  public PermeabilityFormService(PermeabilityFormValidator permeabilityFormValidator,
                                 PermeabilityPersistenceService permeabilityPersistenceService) {
    this.permeabilityFormValidator = permeabilityFormValidator;
    this.permeabilityPersistenceService = permeabilityPersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form,
                                BindingResult bindingResult,
                                ValidationType validationType,
                                SubmissionDetail submissionDetail) {
    permeabilityFormValidator.validate(
        form, bindingResult,
        new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  PermeabilityForm getPermeabilityForm(SubmissionDetail submissionDetail) {
    return permeabilityPersistenceService.getPermeabilityDetail(submissionDetail)
        .map(detail -> permeabilityDetailToForm(submissionDetail, detail))
        .orElse(new PermeabilityForm());
  }

  private PermeabilityForm permeabilityDetailToForm(SubmissionDetail submissionDetail, PermeabilityDetail permeabilityDetail) {
    var form = new PermeabilityForm();
    if (submissionDetail.getSubmissionType() == SubmissionType.POST_DRILL_DRY_HOLE) {
      var permeabilityForm = form.getPermeabilityValuesForm();
      form.setHasPermeability(permeabilityDetail.getHasPermeability());
      if (BooleanUtils.isTrue(permeabilityDetail.getHasPermeability())) {
        setPermeabilityForm(permeabilityForm, permeabilityDetail);
      }
    } else {
      setPermeabilityForm(form.getPermeabilityValuesForm(), permeabilityDetail);
    }

    return form;
  }

  private void setPermeabilityForm(PermeabilityValuesForm permeabilityValuesForm, PermeabilityDetail permeabilityDetail) {
    permeabilityValuesForm.setAverageMethodUsed(permeabilityDetail.getAverageMethodUsed());
    permeabilityValuesForm.setPermeabilityAverage(
        DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityAverage()));
    permeabilityValuesForm.setPermeabilityComments(permeabilityDetail.getPermeabilityComments());
    permeabilityValuesForm.setPermeabilityMax(
        DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityMax()));
    permeabilityValuesForm.setPermeabilityMin(
        DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityMin()));
    permeabilityValuesForm.setPermeabilityAverage(
        DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityAverage()));
    permeabilityValuesForm.setPermeabilitySource(permeabilityDetail.getPermeabilitySource());
    permeabilityValuesForm.setKvKhAverage(DecimalInput.createDecimalInput(permeabilityDetail.getKvKhAverage()));

    if (permeabilityDetail.getPermeabilitySource() == PermeabilitySource.OTHER) {
      permeabilityValuesForm.setOtherPermeabilitySource(permeabilityDetail.getSourceOther());
    }
  }
}
