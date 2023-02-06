package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class DiscoveryWellDetailsPersistenceService {

  private final DiscoveryWellDetailsRepository discoveryWellDetailsRepository;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public DiscoveryWellDetailsPersistenceService(
      DiscoveryWellDetailsRepository discoveryWellDetailsRepository,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.discoveryWellDetailsRepository = discoveryWellDetailsRepository;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  @Transactional
  public void saveDiscoveryWellDetails(DiscoveryWellDetailsForm form, SubmissionDetail submissionDetail) {
    var discoveryWellDetails = getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
        .orElse(new DiscoveryWellDetail());
    discoveryWellDetails.setWellId(form.getWellDetailsForm().getWell());
    discoveryWellDetails.setWellName(form.getWellDetailsForm().getWell());
    discoveryWellDetails.setSubmissionDetail(submissionDetail);
    discoveryWellDetails.setDatum(form.getWellDetailsForm().getReservoirDatum());
    discoveryWellDetails.setChanceOfCommercialityComments(form.getChanceOfCommercialityComments());
    discoveryWellDetails.setDiscoveryStatus(form.getDiscoveryStatus());
    discoveryWellDetails.setLatitudeDegrees(
        form.getWellDetailsForm().getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull());
    discoveryWellDetails.setLatitudeMinutes(
        form.getWellDetailsForm().getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull());
    discoveryWellDetails.setLatitudeSeconds(
        form.getWellDetailsForm().getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull());
    discoveryWellDetails.setLongitudeDegrees(
        form.getWellDetailsForm().getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull());
    discoveryWellDetails.setLongitudeMinutes(
        form.getWellDetailsForm().getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull());
    discoveryWellDetails.setLongitudeSeconds(
        form.getWellDetailsForm().getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull());
    discoveryWellDetails.setSuccessRelatedCosts(form.getSuccessRelatedCosts().createBigDecimalOrNull());
    discoveryWellDetails.setLongitudeHemisphere(form.getWellDetailsForm().getCoordinateInputForm().getLongitudeHemisphere());
    discoveryWellDetails.setDataAcquisitionProgramme(form.getWellDetailsForm().getDataAcquisitionProgramme());
    discoveryWellDetails.setGeophysicalUpdateCommentary(form.getWellDetailsForm().getGeophysicalUpdate());
    discoveryWellDetails.setTotalDepthFormation(form.getWellDetailsForm().getTotalDepthFormation());
    discoveryWellDetails.setTotalWellCost(form.getDryHoleCost().createBigDecimalOrNull());
    discoveryWellDetails.setTotalDepthFormationComments(form.getWellDetailsForm().getTotalDepthFormationComments());

    if (discoveryWellDetails.getHydrocarbonsDiscovered() != form.getHydrocarbonsDiscovered()) {
      cleanHydrocarbonsDiscovered(submissionDetail, form.getHydrocarbonsDiscovered());
    }
    discoveryWellDetails.setHydrocarbonsDiscovered(form.getHydrocarbonsDiscovered());

    discoveryWellDetailsRepository.save(discoveryWellDetails);
  }

  public Optional<DiscoveryWellDetail> getDiscoveryWellDetailBySubmissionDetail(SubmissionDetail submissionDetail) {
    return discoveryWellDetailsRepository.findBySubmissionDetail(submissionDetail);
  }

  public boolean isDiscoveryGasOnly(DiscoveryWellDetail discoveryWellDetail) {
    return discoveryWellDetail.getHydrocarbonsDiscovered() == HydrocarbonsDiscovered.GAS_ONLY;
  }

  public boolean isDiscoveryOilOnly(DiscoveryWellDetail discoveryWellDetail) {
    return discoveryWellDetail.getHydrocarbonsDiscovered() == HydrocarbonsDiscovered.OIL_ONLY;
  }

  public boolean isDiscoveryMixed(DiscoveryWellDetail discoveryWellDetail) {
    return discoveryWellDetail.getHydrocarbonsDiscovered() == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS;
  }

  private void cleanHydrocarbonsDiscovered(SubmissionDetail submissionDetail,
                                           HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
          submissionDetail
      );
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
          submissionDetail
      );
    } else if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
          submissionDetail
      );
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
          submissionDetail
      );
    } else if (hydrocarbonsDiscovered == null) {
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
          submissionDetail
      );
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
          submissionDetail
      );
      probabilityDistributionPersistenceService.deleteProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
          submissionDetail
      );
    }
  }
}
