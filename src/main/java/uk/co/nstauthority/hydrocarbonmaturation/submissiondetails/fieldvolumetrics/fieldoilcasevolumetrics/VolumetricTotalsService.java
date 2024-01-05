package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
public class VolumetricTotalsService {
  private final GeneralReservoirInformationService generalReservoirInformationService;

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Autowired
  public VolumetricTotalsService(GeneralReservoirInformationService generalReservoirInformationService,
                                 ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  public boolean isOilCaseOnly(List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    return reservoirSubmissionDetails.stream().allMatch(generalReservoirInformationService::isOilOnly);
  }

  public boolean isOilCaseOnly(SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    return isOilCaseOnly(reservoirSubmissionDetails);
  }

  public boolean isGasCaseOnly(List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    return reservoirSubmissionDetails.stream().allMatch(generalReservoirInformationService::isGasOnly);
  }

  public boolean isGasCaseOnly(SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    return isGasCaseOnly(reservoirSubmissionDetails);
  }

  /** Considered a combined case is any of the reservoirs have combined oil and gas selected.
   * Or a both oil and gas cases have been selected separately.
   * @param reservoirSubmissionDetails the list of reservoirs on the submission
   * @return true if it's a combined case, else false
   */
  public boolean isCombinedCase(List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    var anyCombined = reservoirSubmissionDetails.stream().anyMatch(generalReservoirInformationService::isCombinedOilAndGas);
    var anyOil = reservoirSubmissionDetails.stream().anyMatch(generalReservoirInformationService::isOilOnly);
    var anyGas = reservoirSubmissionDetails.stream().anyMatch(generalReservoirInformationService::isGasOnly);
    return anyCombined || (anyOil && anyGas);
  }

  /** Considered a combined case is any of the reservoirs have combined oil and gas selected.
   * Or a both oil and gas cases have been selected separately.
   * @param submissionDetail the subissionDetail related to the reservoirs to be checked
   * @return true if it's a combined case, else false
   */
  public boolean isCombinedCase(SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);

    return isCombinedCase(reservoirSubmissionDetails);
  }
}
