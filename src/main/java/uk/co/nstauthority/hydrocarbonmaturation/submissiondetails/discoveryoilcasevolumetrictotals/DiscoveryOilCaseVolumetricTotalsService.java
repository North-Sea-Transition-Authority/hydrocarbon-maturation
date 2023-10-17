package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryoilcasevolumetrictotals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;

@Service
public class DiscoveryOilCaseVolumetricTotalsService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public DiscoveryOilCaseVolumetricTotalsService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  public OilCaseOutputsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new OilCaseOutputsForm();

    form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    ));

    form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    ));

    return form;
  }

  @Transactional
  public void saveDiscoveryOilCaseVolumetricTotals(OilCaseOutputsForm oilCaseOutputsForm, SubmissionDetail submissionDetail) {
    saveProbabilityDistributionFields(oilCaseOutputsForm, submissionDetail);
  }

  private void saveProbabilityDistributionFields(
      OilCaseOutputsForm oilCaseOutputsForm,
      SubmissionDetail submissionDetail) {

    var oilInPlace = ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilInPlace,
        oilCaseOutputsForm.getOilInPlace(),
        submissionDetail
    );

    var oilRecoverableResources = ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoverableResources,
        oilCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail
    );

    var associatedGasRecoverableResources =
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        associatedGasRecoverableResources,
        oilCaseOutputsForm.getAssociatedGasRecoverableResources(),
        submissionDetail
    );

    var oilEquivalentRecoverableResources =
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilEquivalentRecoverableResources,
        oilCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail
    );
  }
}
