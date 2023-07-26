package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
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
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    oilCaseOutputsFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  OilCaseOutputsForm getOilCaseOutputsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirSubmissionDetailToForm(reservoirSubmissionDetail);
  }

  private OilCaseOutputsForm reservoirSubmissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new OilCaseOutputsForm();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT
      ));

      form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT
      ));

      form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT
      ));

      form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      ));
    } else if (resourceType == ResourceType.DISCOVERY) {
      form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
      ));

      form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
      ));

      form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
      ));

      form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      ));
    }

    var oilCaseOutput = oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail);

    var riskedOilEquivalentRecoverableMean = oilCaseOutput
        .map(OilCaseOutput::getRiskedOilEquivalentRecoverableMean)
        .orElse(null);
    var hasAssociatedGasRecoverableResources = oilCaseOutput
        .map(OilCaseOutput::getHasAssociatedGasRecoverableResources)
        .orElse(null);

    form.setRiskedOilEquivalentRecoverableMean(DecimalInput.createDecimalInput(riskedOilEquivalentRecoverableMean));
    form.setHasAssociatedGasRecoverableResources(hasAssociatedGasRecoverableResources);
    return form;
  }
}
