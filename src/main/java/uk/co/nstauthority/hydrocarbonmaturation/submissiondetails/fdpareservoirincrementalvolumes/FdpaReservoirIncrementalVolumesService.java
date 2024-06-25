package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import jakarta.transaction.Transactional;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class FdpaReservoirIncrementalVolumesService {

  private final FdpaReservoirIncrementalVolumesRepository fdpaReservoirIncrementalRepository;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Autowired
  public FdpaReservoirIncrementalVolumesService(
      FdpaReservoirIncrementalVolumesRepository fdpaReservoirIncrementalRepository,
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService) {
    this.fdpaReservoirIncrementalRepository = fdpaReservoirIncrementalRepository;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
  }

  public FdpaReservoirIncrementalVolumesForm submissionDetailToForm(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var fdpaReservoirIncrementalVolumesForm = new FdpaReservoirIncrementalVolumesForm();
    var incrementalVolume = fdpaReservoirIncrementalRepository.findByReservoirSubmissionDetail(
            reservoirSubmissionDetail)
        .orElse(new FdpaReservoirIncrementalVolume());

    var changeToInPlaceVolumes = incrementalVolume.getChangeToInPlaceVolumes();
    fdpaReservoirIncrementalVolumesForm.setChangeToInPlaceVolumes(changeToInPlaceVolumes);

    setFormProbabilityDistributions(fdpaReservoirIncrementalVolumesForm, reservoirSubmissionDetail);
    fdpaReservoirIncrementalVolumesForm.setComment(incrementalVolume.getComment());

    return fdpaReservoirIncrementalVolumesForm;
  }

  @Transactional
  public void saveFdpaReservoirIncrementalVolume(FdpaReservoirIncrementalVolumesForm form,
                                                 ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirIncrementalVolume = fdpaReservoirIncrementalRepository
        .findByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .orElse(new FdpaReservoirIncrementalVolume());

    var changeToInPlaceVolumes = form.getChangeToInPlaceVolumes();

    reservoirIncrementalVolume.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirIncrementalVolume.setChangeToInPlaceVolumes(changeToInPlaceVolumes);

    if (BooleanUtils.isTrue(changeToInPlaceVolumes)) {
      reservoirIncrementalVolume.setComment(form.getComment());
      saveProbabilityDistributions(reservoirSubmissionDetail, form);
    } else {
      reservoirIncrementalVolume.setComment(null);
      deleteProbabilityDistributions(reservoirSubmissionDetail);
    }

    fdpaReservoirIncrementalRepository.save(reservoirIncrementalVolume);
  }

  private HydrocarbonsDiscovered getHydrocarbonsDiscovered(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail));
  }

  private void setFormProbabilityDistributions(FdpaReservoirIncrementalVolumesForm form,
                                               ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var hydrocarbonsDiscovered = getHydrocarbonsDiscovered(reservoirSubmissionDetail);

    if (hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.OIL_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)) {
      form.setIncrementalIncreaseOil(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA
          )
      );
    }

    if (hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.GAS_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)) {
      form.setIncrementalIncreaseGas(
          probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA
          )
      );
    }
  }

  private void saveProbabilityDistributions(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                            FdpaReservoirIncrementalVolumesForm form) {
    var hydrocarbonsDiscovered = getHydrocarbonsDiscovered(reservoirSubmissionDetail);

    if (hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.OIL_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA,
          form.getIncrementalIncreaseOil(),
          reservoirSubmissionDetail
      );
    } else {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA,
          reservoirSubmissionDetail
      );
    }

    if (hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.GAS_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA,
          form.getIncrementalIncreaseGas(),
          reservoirSubmissionDetail
      );
    } else {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA,
          reservoirSubmissionDetail
      );
    }
  }

  private void deleteProbabilityDistributions(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    probabilityDistributionPersistenceService.deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA,
            ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA
        ),
        reservoirSubmissionDetail
    );
  }
}
