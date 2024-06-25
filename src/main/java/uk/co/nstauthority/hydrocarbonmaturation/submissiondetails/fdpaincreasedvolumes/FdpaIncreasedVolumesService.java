package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes;

import jakarta.transaction.Transactional;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;

@Service
public class FdpaIncreasedVolumesService {

  private final FdpaIncreasedVolumesRepository fdpaIncreasedVolumesRepository;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Autowired
  public FdpaIncreasedVolumesService(
      FdpaIncreasedVolumesRepository fdpaIncreasedVolumesRepository,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService) {
    this.fdpaIncreasedVolumesRepository = fdpaIncreasedVolumesRepository;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
  }

  public FdpaIncreasedVolumesForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var fdpaIncreasedVolumesForm = new FdpaIncreasedVolumesForm();

    var fdpaIncreasedVolume = fdpaIncreasedVolumesRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new FdpaIncreasedVolume());

    var changeInVolumetrics = fdpaIncreasedVolume.getChangeInVolumetrics();
    fdpaIncreasedVolumesForm.setChangeInVolumetrics(changeInVolumetrics);

    if (BooleanUtils.isTrue(changeInVolumetrics)) {
      setProbabilityDistributions(fdpaIncreasedVolumesForm, submissionDetail);
      fdpaIncreasedVolumesForm.setComment(fdpaIncreasedVolume.getComment());
    }

    return fdpaIncreasedVolumesForm;
  }

  @Transactional
  public void saveFdpaIncreasedVolumes(FdpaIncreasedVolumesForm form,
                                       SubmissionDetail submissionDetail) {
    var fdpaIncreasedVolume = fdpaIncreasedVolumesRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new FdpaIncreasedVolume());

    var changeInVolumetrics = form.getChangeInVolumetrics();

    fdpaIncreasedVolume.setSubmissionDetail(submissionDetail);
    fdpaIncreasedVolume.setChangeInVolumetrics(changeInVolumetrics);

    if (BooleanUtils.isTrue(changeInVolumetrics)) {
      fdpaIncreasedVolume.setComment(form.getComment());
      saveProbabilityDistributions(form, submissionDetail);
    } else {
      fdpaIncreasedVolume.setComment(null);
      deleteProbabilityDistributions(submissionDetail);
    }

    fdpaIncreasedVolumesRepository.save(fdpaIncreasedVolume);
  }

  private void setProbabilityDistributions(FdpaIncreasedVolumesForm form,
                                           SubmissionDetail submissionDetail) {
    var hydrocarbonsDiscovered = reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail);

    if (BooleanUtils.isTrue(isOilOrCombinedCase(hydrocarbonsDiscovered))) {
      form.setOilInitiallyInPlace(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              submissionDetail,
              ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
          )
      );
      form.setOilRecoverableResources(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              submissionDetail,
              ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
          )
      );
      form.setOilReserves(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              submissionDetail,
              ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA
          )
      );
    }

    if (BooleanUtils.isTrue(isGasOrCombinedCase(hydrocarbonsDiscovered))) {
      form.setGasInitiallyInPlace(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              submissionDetail,
              ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
          )
      );
    }

    form.setAssociatedCondensateRecoverableResources(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
        )
    );
    form.setAssociatedCondensateReserves(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA
        )
    );
    form.setNaturalGasLiquidsRecoverableResources(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
        )
    );
    form.setNaturalGasLiquidsReserves(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA
        )
    );
    form.setSalesGasRecoverableResources(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
        )
    );
    form.setSalesGasReserves(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA
        )
    );
    form.setOilEquivalentRecoverableResources(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA
        )
    );
    form.setOilEquivalentReserves(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA
        )
    );
  }

  private void saveProbabilityDistributions(FdpaIncreasedVolumesForm form,
                                            SubmissionDetail submissionDetail) {
    var hydrocarbonsDiscovered = reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail);

    if (BooleanUtils.isTrue(isOilOrCombinedCase(hydrocarbonsDiscovered))) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,
          form.getOilInitiallyInPlace(),
          submissionDetail
      );
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
          form.getOilRecoverableResources(),
          submissionDetail
      );
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA,
          form.getOilReserves(),
          submissionDetail
      );
    } else {
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(
          List.of(
              ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,
              ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
              ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA
          ),
          submissionDetail
      );
    }

    if (BooleanUtils.isTrue(isGasOrCombinedCase(hydrocarbonsDiscovered))) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,
          form.getGasInitiallyInPlace(),
          submissionDetail
      );
    } else {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,
          submissionDetail
      );
    }

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        form.getAssociatedCondensateRecoverableResources(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA,
        form.getAssociatedCondensateReserves(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        form.getNaturalGasLiquidsRecoverableResources(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA,
        form.getNaturalGasLiquidsReserves(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        form.getSalesGasRecoverableResources(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA,
        form.getSalesGasReserves(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        form.getOilEquivalentRecoverableResources(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA,
        form.getOilEquivalentReserves(),
        submissionDetail
    );
  }

  private void deleteProbabilityDistributions(SubmissionDetail submissionDetail) {
    probabilityDistributionPersistenceService.deleteProbabilityDistributions(
        List.of(
            // oil only
            ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA,

            // gas only
            ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,

            //combined case
            ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA
        ),
        submissionDetail
    );
  }

  private boolean isOilOrCombinedCase(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    return hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.OIL_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
  }

  private boolean isGasOrCombinedCase(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    return hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.GAS_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
  }
}
