package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class DiscoveryWellDetailsPersistenceService {

  private final DiscoveryWellDetailsRepository discoveryWellDetailsRepository;

  @Autowired
  DiscoveryWellDetailsPersistenceService(DiscoveryWellDetailsRepository discoveryWellDetailsRepository) {
    this.discoveryWellDetailsRepository = discoveryWellDetailsRepository;
  }

  @Transactional
  public void saveDiscoveryWellDetails(DiscoveryWellDetailsForm form, SubmissionDetail submissionDetail) {
    var discoveryWellDetails = getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
        .orElse(new DiscoveryWellDetail());
    discoveryWellDetails.setDryWellId(form.getWellDetailsForm().getDryWell());
    discoveryWellDetails.setDryWellName(form.getWellDetailsForm().getDryWell());
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
    discoveryWellDetails.setHydrocarbonsDiscovered(form.getHydrocarbonsDiscovered());
    discoveryWellDetailsRepository.save(discoveryWellDetails);
  }

  public Optional<DiscoveryWellDetail> getDiscoveryWellDetailBySubmissionDetail(SubmissionDetail submissionDetail) {
    return discoveryWellDetailsRepository.findBySubmissionDetail(submissionDetail);
  }
}
