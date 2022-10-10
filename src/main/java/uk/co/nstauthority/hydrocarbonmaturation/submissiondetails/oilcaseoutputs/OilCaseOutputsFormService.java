package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.numberinput.IntegerInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilCaseOutputsFormService implements SubmissionFormService {

  private final OilCaseOutputsFormValidator oilCaseOutputsFormValidator;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Autowired
  public OilCaseOutputsFormService(
      OilCaseOutputsFormValidator oilCaseOutputsFormValidator,
      ProbabilityDistributionFormService probabilityDistributionFormService,
      OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService) {
    this.oilCaseOutputsFormValidator = oilCaseOutputsFormValidator;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.oilCaseOutputsPersistenceService = oilCaseOutputsPersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    oilCaseOutputsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  OilCaseOutputsForm getOilCaseOutputsForm(SubmissionDetail submissionDetail) {
    return submissionDetailToForm(submissionDetail);
  }

  private OilCaseOutputsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new OilCaseOutputsForm();

    form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.OIL_IN_PLACE
    ));

    form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES
    ));

    form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_RECOVERABLE_RESOURCES
    ));

    form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES
    ));

    var riskedOilEquivalentRecoverableMean = oilCaseOutputsPersistenceService.getOilCaseOutput(submissionDetail)
        .map(OilCaseOutput::getRiskedOilEquivalentRecoverableMean)
        .orElse(null);
    form.setRiskedOilEquivalentRecoverableMean(IntegerInput.createNumberInput(riskedOilEquivalentRecoverableMean));

    return form;
  }
}
