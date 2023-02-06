package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class DryWellDetailsFormService implements SubmissionFormService {

  private final DryWellDetailsFormValidator dryWellDetailsFormValidator;
  private final DryWellDetailsPersistenceService dryWellDetailsPersistenceService;
  private final WellDetailsFormService wellDetailsFormService;

  @Autowired
  public DryWellDetailsFormService(DryWellDetailsFormValidator dryWellDetailsFormValidator,
                                   DryWellDetailsPersistenceService dryWellDetailsPersistenceService,
                                   WellDetailsFormService wellDetailsFormService) {
    this.dryWellDetailsFormValidator = dryWellDetailsFormValidator;
    this.dryWellDetailsPersistenceService = dryWellDetailsPersistenceService;
    this.wellDetailsFormService = wellDetailsFormService;
  }

  DryWellDetailsForm getDryWellDetailsForm(SubmissionDetail submissionDetail) {
    return dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail)
        .map(this::dryWellDetailEntityToForm)
        .orElse(new DryWellDetailsForm());
  }

  private DryWellDetailsForm dryWellDetailEntityToForm(DryWellDetail dryWellDetail) {
    var form = new DryWellDetailsForm();
    form.setDrillDetailsForm(wellDetailsFormService.drillDetailToForm(dryWellDetail));
    form.setDryHoleCost(DecimalInput.createDecimalInput(dryWellDetail.getDryHoleCost()));
    form.setDryHoleScope(dryWellDetail.getDryHoleScope());
    form.getDrillDetailsForm().setDataAcquisitionProgramme(dryWellDetail.getDataAcquisitionProgramme());
    form.setDryHoleDescription(dryWellDetail.getDryHoleDescription());
    form.setDryHoleComments(dryWellDetail.getDryHoleComments());
    form.setDryHoleType(dryWellDetail.getDryHoleType());
    form.setTrapFailureCategory(dryWellDetail.getTrapFailureCategory());
    form.setSourceFailureCategory(dryWellDetail.getSourceFailureCategory());
    form.setSealFailureCategory(dryWellDetail.getSealFailureCategory());
    form.setReservoirFailureCategory(dryWellDetail.getReservoirFailureCategory());
    form.setMigrationFailureCategory(dryWellDetail.getMigrationFailureCategory());
    return form;
  }

  public Map<String, String> getPreSelectedWell(String well) {
    return wellDetailsFormService.getPreSelectedWell(well);
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    dryWellDetailsFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }
}
