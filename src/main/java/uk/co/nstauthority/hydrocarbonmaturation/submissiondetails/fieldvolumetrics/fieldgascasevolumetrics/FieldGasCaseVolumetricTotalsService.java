package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotalsRepository;

@Service
public class FieldGasCaseVolumetricTotalsService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final GasCaseVolumetricTotalsRepository gasCaseVolumetricTotalsRepository;

  @Autowired
  public FieldGasCaseVolumetricTotalsService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      GasCaseVolumetricTotalsRepository gasCaseVolumetricTotalsRepository) {

    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.gasCaseVolumetricTotalsRepository = gasCaseVolumetricTotalsRepository;
  }

  public GasCaseOutputsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new GasCaseOutputsForm();

    form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    ));

    var gasCaseVolumetricTotal = gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new GasCaseVolumetricTotal());

    form.setHasAssociatedCondensateRecoverableResources(
        gasCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources());

    form.setFieldVolumetricInformationCorrect(gasCaseVolumetricTotal.getFieldVolumetricInformationCorrect());

    form.setAssociatedCondensateRecoverableResources(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
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

    if (!submissionDetail.isConceptSelectReportSubmission()) {
      form.setAssociatedCondensateRecoverableReserves(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              submissionDetail,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD
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
    }

    return form;
  }

  @Transactional
  public void saveFieldGasCaseVolumetricTotals(GasCaseOutputsForm gasCaseOutputsForm,
                                               SubmissionDetail submissionDetail) {
    saveProbabilityDistributionFields(gasCaseOutputsForm, submissionDetail);
    saveGasVolumetricTotal(gasCaseOutputsForm, submissionDetail);
  }

  private void saveGasVolumetricTotal(GasCaseOutputsForm gasCaseOutputsForm, SubmissionDetail submissionDetail) {
    var gasVolumetricTotal = gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new GasCaseVolumetricTotal());
    gasVolumetricTotal.setSubmissionDetail(submissionDetail);
    gasVolumetricTotal
        .setHasAssociatedCondensateRecoverableResources(
            gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources());

    gasVolumetricTotal.setFieldVolumetricInformationCorrect(gasCaseOutputsForm.getFieldVolumetricInformationCorrect());

    gasCaseVolumetricTotalsRepository.save(gasVolumetricTotal);
  }

  private void saveProbabilityDistributionFields(
      GasCaseOutputsForm gasCaseOutputsForm,
      SubmissionDetail submissionDetail) {

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD,
        gasCaseOutputsForm.getGasInPlace(),
        submissionDetail
    );

    var hasAssociatedCondensateRecoverableResources = gasCaseOutputsForm.getHasAssociatedCondensateRecoverableResources();
    if (BooleanUtils.isTrue(hasAssociatedCondensateRecoverableResources)) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
          gasCaseOutputsForm.getAssociatedCondensateRecoverableResources(),
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
        gasCaseOutputsForm.getOilEquivalentRecoverableResources(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        gasCaseOutputsForm.getNaturalGasLiquidsResources(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
        gasCaseOutputsForm.getSalesGasResources(),
        submissionDetail
    );

    if (!submissionDetail.isConceptSelectReportSubmission()) {
      // Save the fields related to reserves if it's not a concept select report submission
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
          gasCaseOutputsForm.getAssociatedCondensateRecoverableReserves(),
          submissionDetail
      );

      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
          gasCaseOutputsForm.getOilEquivalentRecoverableReserves(),
          submissionDetail
      );

      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
          gasCaseOutputsForm.getNaturalGasLiquidsReserves(),
          submissionDetail
      );

      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD,
          gasCaseOutputsForm.getSalesGasReserves(),
          submissionDetail
      );
    }
  }

  @Transactional
  public void deleteGasCaseVolumetricTotalsForSubmissionDetail(SubmissionDetail submissionDetail) {
    gasCaseVolumetricTotalsRepository.deleteBySubmissionDetail(submissionDetail);
  }
}
