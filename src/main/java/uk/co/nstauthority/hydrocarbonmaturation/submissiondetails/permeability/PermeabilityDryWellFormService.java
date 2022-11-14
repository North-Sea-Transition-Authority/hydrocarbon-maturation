package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class PermeabilityDryWellFormService implements SubmissionFormService {

  private final PermeabilityDryWellFormValidator permeabilityDryWellFormValidator;
  private final PermeabilityPersistenceService permeabilityPersistenceService;

  @Autowired
  public PermeabilityDryWellFormService(PermeabilityDryWellFormValidator permeabilityDryWellFormValidator,
                                        PermeabilityPersistenceService permeabilityPersistenceService) {
    this.permeabilityDryWellFormValidator = permeabilityDryWellFormValidator;
    this.permeabilityPersistenceService = permeabilityPersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    permeabilityDryWellFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  PermeabilityDryWellForm getPermeabilityForm(SubmissionDetail submissionDetail) {
    return permeabilityPersistenceService.getPermeabilityDetail(submissionDetail)
        .map(this::permeabilityDetailToForm)
        .orElse(new PermeabilityDryWellForm());
  }

  private PermeabilityDryWellForm permeabilityDetailToForm(PermeabilityDetail permeabilityDetail) {
    var form = new PermeabilityDryWellForm();
    var permeabilityForm = form.getPermeabilityForm();
    form.setHasPermeability(permeabilityDetail.getHasPermeability());
    if (BooleanUtils.isTrue(permeabilityDetail.getHasPermeability())) {
      permeabilityForm.setAverageMethodUsed(permeabilityDetail.getAverageMethodUsed());
      permeabilityForm.setPermeabilityAverage(
          DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityAverage()));
      permeabilityForm.setPermeabilityComments(permeabilityDetail.getPermeabilityComments());
      permeabilityForm.setPermeabilityMax(
          DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityMax()));
      permeabilityForm.setPermeabilityMin(
          DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityMin()));
      permeabilityForm.setPermeabilityAverage(
          DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityAverage()));
      permeabilityForm.setPermeabilitySource(permeabilityDetail.getPermeabilitySource());
      permeabilityForm.setKvKhAverageMethodUsed(permeabilityDetail.getKvKhAverageMethodUsed());
      permeabilityForm.setKvKhComments(permeabilityDetail.getKvKhComments());
      permeabilityForm.setKvKhMax(DecimalInput.createDecimalInput(permeabilityDetail.getKvKhMax()));
      permeabilityForm.setKvKhMin(DecimalInput.createDecimalInput(permeabilityDetail.getKvKhMin()));
      permeabilityForm.setKvKhSource(permeabilityDetail.getKvKhSource());
      permeabilityForm.setKvKhAverage(DecimalInput.createDecimalInput(permeabilityDetail.getKvKhAverage()));

      if (permeabilityDetail.getPermeabilitySource() == PermeabilitySource.OTHER) {
        permeabilityForm.setOtherPermeabilitySource(permeabilityDetail.getSourceOther());
      }

      if (permeabilityDetail.getKvKhSource() == PermeabilitySource.OTHER) {
        permeabilityForm.setOtherKvKhSource(permeabilityDetail.getKvKhSourceOther());
      }

      if (permeabilityDetail.getKvKhAverageMethodUsed() == PermeabilitySource.OTHER) {
        permeabilityForm.setKvKhAverageMethodUsedOther(permeabilityDetail.getKvKhAverageMethodUsedOther());
      }
    }

    return form;
  }
}
