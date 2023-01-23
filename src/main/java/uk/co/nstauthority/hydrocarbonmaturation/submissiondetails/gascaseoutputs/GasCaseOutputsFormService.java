package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class GasCaseOutputsFormService implements SubmissionFormService {

  private final GasCaseOutputsFormValidator gasCaseOutputsFormValidator;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  public GasCaseOutputsFormService(GasCaseOutputsFormValidator gasCaseOutputsFormValidator,
                                   ProbabilityDistributionFormService probabilityDistributionFormService,
                                   GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService) {
    this.gasCaseOutputsFormValidator = gasCaseOutputsFormValidator;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    gasCaseOutputsFormValidator.validate(form, bindingResult,
        new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  GasCaseOutputsForm getGasCaseOutputsForm(SubmissionDetail submissionDetail) {
    return submissionDetailToForm(submissionDetail);
  }

  private GasCaseOutputsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new GasCaseOutputsForm();

    form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE
    ));

    form.setGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES
    ));

    var hasAssociatedCondensateRecoverableResources = gasCaseOutputsPersistenceService.getGasCaseOutput(submissionDetail)
        .map(GasCaseOutput::getHasAssociatedCondensateRecoverableResources)
        .orElse(null);

    form.setHasAssociatedCondensateRecoverableResources(hasAssociatedCondensateRecoverableResources);
    form.setAssociatedCondensateRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES
    ));

    form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES
    ));

    return form;
  }
}
