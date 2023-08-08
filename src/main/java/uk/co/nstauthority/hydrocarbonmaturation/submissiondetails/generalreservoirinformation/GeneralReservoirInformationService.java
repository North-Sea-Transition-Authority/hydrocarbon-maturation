package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class GeneralReservoirInformationService {
  private final ReservoirHydrocarbonsRepository reservoirHydrocarbonsRepository;
  private final ReservoirPersistenceService reservoirPersistenceService;

  public GeneralReservoirInformationService(ReservoirHydrocarbonsRepository reservoirHydrocarbonsRepository,
                                            ReservoirPersistenceService reservoirPersistenceService) {
    this.reservoirHydrocarbonsRepository = reservoirHydrocarbonsRepository;
    this.reservoirPersistenceService = reservoirPersistenceService;
  }

  @Transactional
  public void saveGeneralReservoirInformation(GeneralReservoirInformationForm form,
                                              ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoir = reservoirSubmissionDetail.getReservoir();
    reservoirPersistenceService.saveReservoir(reservoir, form.getReservoirName());

    var reservoirHydrocarbons = getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());
    reservoirHydrocarbons.setReservoirSubmissionDetail(reservoirSubmissionDetail);
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
}
