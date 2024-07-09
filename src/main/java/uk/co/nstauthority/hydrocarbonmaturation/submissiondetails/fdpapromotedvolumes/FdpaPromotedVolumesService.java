package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;

@Service
public class FdpaPromotedVolumesService {

  private final FdpaPromotedVolumesRepository fdpaPromotedVolumesRepository;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Autowired
  public FdpaPromotedVolumesService(
      FdpaPromotedVolumesRepository fdpaPromotedVolumesRepository,
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService) {
    this.fdpaPromotedVolumesRepository = fdpaPromotedVolumesRepository;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
  }

  public FdpaPromotedVolumesForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var fdpaPromotedVolumesForm = new FdpaPromotedVolumesForm();

    var fdpaPromotedVolume = fdpaPromotedVolumesRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new FdpaPromotedVolume());

    var hasPromotedResources = fdpaPromotedVolume.getResourcesAreBeingPromoted();
    fdpaPromotedVolumesForm.setHasPromotedVolumes(hasPromotedResources);

    setProbabilityDistributions(fdpaPromotedVolumesForm, submissionDetail);
    fdpaPromotedVolumesForm.setComment(fdpaPromotedVolume.getComment());

    return fdpaPromotedVolumesForm;
  }

  @Transactional
  public void saveFdpaPromotedVolumes(FdpaPromotedVolumesForm form,
                                      SubmissionDetail submissionDetail) {
    var fdpaPromotedVolume = fdpaPromotedVolumesRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new FdpaPromotedVolume());

    var hasPromotedVolumes = form.getHasPromotedVolumes();

    fdpaPromotedVolume.setSubmissionDetail(submissionDetail);
    fdpaPromotedVolume.setResourcesAreBeingPromoted(hasPromotedVolumes);

    if (BooleanUtils.isTrue(hasPromotedVolumes)) {
      fdpaPromotedVolume.setComment(form.getComment());
      saveProbabilityDistributions(form, submissionDetail);
    } else {
      fdpaPromotedVolume.setComment(null);
      deleteProbabilityDistributions(submissionDetail);
    }

    fdpaPromotedVolumesRepository.save(fdpaPromotedVolume);
  }

  private void setProbabilityDistributions(FdpaPromotedVolumesForm fdpaPromotedVolumesForm,
                                           SubmissionDetail submissionDetail) {
    if (BooleanUtils.isTrue(isOilOnly(submissionDetail))) {
      fdpaPromotedVolumesForm.setOilReserves(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              submissionDetail,
              ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA
          )
      );
    }

    fdpaPromotedVolumesForm.setAssociatedCondensateReserves(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA
        )
    );

    fdpaPromotedVolumesForm.setNaturalGasLiquidsReserves(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA
        )
    );

    fdpaPromotedVolumesForm.setSalesGasReserves(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA
        )
    );

    fdpaPromotedVolumesForm.setOilEquivalentReserves(
        probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA
        )
    );
  }

  private void saveProbabilityDistributions(FdpaPromotedVolumesForm form,
                                            SubmissionDetail submissionDetail) {
    if (BooleanUtils.isTrue(isOilOnly(submissionDetail))) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA,
          form.getOilReserves(),
          submissionDetail
      );
    } else {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA,
          submissionDetail
      );
    }

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA,
        form.getAssociatedCondensateReserves(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA,
        form.getNaturalGasLiquidsReserves(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA,
        form.getSalesGasReserves(),
        submissionDetail
    );

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA,
        form.getOilEquivalentReserves(),
        submissionDetail
    );
  }

  private void deleteProbabilityDistributions(SubmissionDetail submissionDetail) {
    probabilityDistributionPersistenceService.deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA,
            ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA
        ),
        submissionDetail
    );
  }

  private Boolean isOilOnly(SubmissionDetail submissionDetail) {
    return reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail);
  }
}
