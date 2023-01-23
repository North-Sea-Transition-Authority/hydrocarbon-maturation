package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class MixedCaseOutputsFormService implements SubmissionFormService {

  private final MixedCaseOutputsFormValidator mixedCaseOutputsFormValidator;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  @Autowired
  public MixedCaseOutputsFormService(
      MixedCaseOutputsFormValidator mixedCaseOutputsFormValidator,
      ProbabilityDistributionFormService probabilityDistributionFormService,
      MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService) {
    this.mixedCaseOutputsFormValidator = mixedCaseOutputsFormValidator;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.mixedCaseOutputsPersistenceService = mixedCaseOutputsPersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    mixedCaseOutputsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  MixedCaseOutputsForm getMixedCaseOutputsForm(SubmissionDetail submissionDetail) {
    return submissionDetailToForm(submissionDetail);
  }

  private MixedCaseOutputsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new MixedCaseOutputsForm();

    var mixedCaseOutputs = mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail);
    var hasAssociatedGasRecoverableResources = mixedCaseOutputs
        .map(MixedCaseOutput::getHasAssociatedGasRecoverableResources)
        .orElse(null);
    var riskedMixedEquivalentRecoverableMean = mixedCaseOutputs
        .map(MixedCaseOutput::getRiskedOilEquivalentRecoverableMean)
        .orElse(null);

    form.setRiskedOilEquivalentRecoverableMean(DecimalInput.createDecimalInput(riskedMixedEquivalentRecoverableMean));
    form.setHasAssociatedGasRecoverableResources(hasAssociatedGasRecoverableResources);

    form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE
    ));

    form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES
    ));

    form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES
    ));

    form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE
    ));

    form.setGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES
    ));

    form.setAssociatedCondensateRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES
    ));

    form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES
    ));

    return form;
  }
}
