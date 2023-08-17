package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class GeneralReservoirInformationService {
  private final ReservoirHydrocarbonsRepository reservoirHydrocarbonsRepository;
  private final ReservoirPersistenceService reservoirPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  public GeneralReservoirInformationService(ReservoirHydrocarbonsRepository reservoirHydrocarbonsRepository,
                                            ReservoirPersistenceService reservoirPersistenceService,
                                            ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.reservoirHydrocarbonsRepository = reservoirHydrocarbonsRepository;
    this.reservoirPersistenceService = reservoirPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  @Transactional
  public void saveGeneralReservoirInformation(GeneralReservoirInformationForm form,
                                              ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoir = reservoirSubmissionDetail.getReservoir();
    reservoirPersistenceService.saveReservoir(reservoir, form.getReservoirName());

    var reservoirHydrocarbons = getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());
    reservoirHydrocarbons.setReservoirSubmissionDetail(reservoirSubmissionDetail);

    if (reservoirHydrocarbons.getHydrocarbonsDiscovered() != form.getHydrocarbonsDiscovered()) {
      cleanHydrocarbonsDiscovered(reservoirSubmissionDetail, form.getHydrocarbonsDiscovered());
    }

    reservoirHydrocarbons.setHydrocarbonsDiscovered(form.getHydrocarbonsDiscovered());
    reservoirHydrocarbonsRepository.save(reservoirHydrocarbons);
  }

  public GeneralReservoirInformationForm getGeneralReservoirInformationForm(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName(reservoirSubmissionDetail.getReservoir().getName());

    // Set the forms' hydrocarbons discovered to be: the saved reservoir hydrocarbons' hydrocarbons discovered or null
    HydrocarbonsDiscovered hydrocarbonsDiscovered = reservoirHydrocarbonsRepository
        .findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .map(ReservoirHydrocarbons::getHydrocarbonsDiscovered)
        .orElse(null);
    form.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);

    return form;
  }

  public Optional<ReservoirHydrocarbons> getReservoirHydrocarbons(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirHydrocarbonsRepository
        .findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  private void cleanHydrocarbonsDiscovered(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                           HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(
          List.of(
              ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY,
              ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
              ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY,
              ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD
          ),
          reservoirSubmissionDetail
      );
    } else if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(
          List.of(
              ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY,
              ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
              ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
              ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
              ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR
          ),
          reservoirSubmissionDetail
      );
    }
  }
}
