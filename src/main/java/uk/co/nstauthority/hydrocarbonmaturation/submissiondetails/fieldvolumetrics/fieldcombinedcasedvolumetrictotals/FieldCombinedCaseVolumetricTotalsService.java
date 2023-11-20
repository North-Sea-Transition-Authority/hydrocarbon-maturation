package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals;

import javax.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotalsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;

@Service
public class FieldCombinedCaseVolumetricTotalsService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final CombinedCaseVolumetricTotalsRepository combinedCaseVolumetricTotalsRepository;

  @Autowired
  public FieldCombinedCaseVolumetricTotalsService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      CombinedCaseVolumetricTotalsRepository combinedCaseVolumetricTotalsRepository) {

    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.combinedCaseVolumetricTotalsRepository = combinedCaseVolumetricTotalsRepository;
  }

  public MixedCaseOutputsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new MixedCaseOutputsForm();

    form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    ));

    form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    ));

    form.setOilRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    ));

    var hasAssociatedCondensateRecoverableResources =
        combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)
          .map(CombinedCaseVolumetricTotal::getHasAssociatedCondensateRecoverableResources)
          .orElse(null);
    form.setHasAssociatedCondensateRecoverableResources(hasAssociatedCondensateRecoverableResources);

    form.setAssociatedCondensateRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
    ));

    form.setAssociatedGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD
    ));

    form.setOilEquivalentRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD
    ));

    form.setNaturalGasLiquidsResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD
    ));

    form.setSalesGasResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    ));

    form.setGasRecoverableResources(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD
    ));

    if (!submissionDetail.isConceptSelectReportSubmission()) {
      form.setOilRecoverableReserves(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD
      ));

      form.setAssociatedCondensateRecoverableReserves(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
      ));

      form.setAssociatedGasRecoverableReserves(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD
      ));

      form.setOilEquivalentRecoverableReserves(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
      ));

      form.setNaturalGasLiquidsReserves(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD
      ));

      form.setSalesGasReserves(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
      ));

      form.setGasRecoverableReserves(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD
      ));
    }

    return form;
  }

  @Transactional
  public void saveFieldCombinedCaseVolumetricTotals(MixedCaseOutputsForm form, SubmissionDetail submissionDetail) {
    saveProbabilityDistributionFields(form, submissionDetail);
    saveCombinedCaseVolumetricTotal(form, submissionDetail);
  }

  private void saveCombinedCaseVolumetricTotal(MixedCaseOutputsForm mixedCaseOutputsForm, SubmissionDetail submissionDetail) {
    var combinedCaseVolumetricTotal = combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new CombinedCaseVolumetricTotal());
    combinedCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    combinedCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(
        mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources()
    );
    combinedCaseVolumetricTotalsRepository.save(combinedCaseVolumetricTotal);
  }

  private void saveProbabilityDistributionFields(
      MixedCaseOutputsForm mixedCaseOutputsForm,
      SubmissionDetail submissionDetail) {

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
        mixedCaseOutputsForm.getOilInPlace(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
        mixedCaseOutputsForm.getOilRecoverableResources(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD,
        mixedCaseOutputsForm.getAssociatedGasRecoverableResources(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD,
        mixedCaseOutputsForm.getGasInPlace(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
        mixedCaseOutputsForm.getGasRecoverableResources(),
        submissionDetail
    );

    var hasAssociatedCondensateRecoverableResources = mixedCaseOutputsForm.getHasAssociatedCondensateRecoverableResources();
    if (BooleanUtils.isTrue(hasAssociatedCondensateRecoverableResources)) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
          mixedCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
          submissionDetail
      );
    } else if (BooleanUtils.isFalse(hasAssociatedCondensateRecoverableResources)) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
          submissionDetail
      );
    }

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        mixedCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        mixedCaseOutputsForm.getNaturalGasLiquidsResources(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
        mixedCaseOutputsForm.getSalesGasResources(),
        submissionDetail
    );

    if (!submissionDetail.isConceptSelectReportSubmission()) {
      // Save the fields related to reserves if it's not a concept select report submission
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD,
          mixedCaseOutputsForm.getOilRecoverableReserves(),
          submissionDetail
      );
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD,
          mixedCaseOutputsForm.getAssociatedGasRecoverableReserves(),
          submissionDetail
      );

      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
          mixedCaseOutputsForm.getGasRecoverableReserves(),
          submissionDetail
      );

      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
          mixedCaseOutputsForm.getAssociatedCondensateRecoverableReserves(),
          submissionDetail
      );

      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
          mixedCaseOutputsForm.getOilEquivalentRecoverableReserves(),
          submissionDetail
      );

      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
          mixedCaseOutputsForm.getNaturalGasLiquidsReserves(),
          submissionDetail
      );

      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD,
          mixedCaseOutputsForm.getSalesGasReserves(),
          submissionDetail
      );
    }
  }

  @Transactional
  public void deleteCombinedCaseVolumetricTotalsForSubmissionDetail(SubmissionDetail submissionDetail) {
    combinedCaseVolumetricTotalsRepository.deleteBySubmissionDetail(submissionDetail);
  }
}
