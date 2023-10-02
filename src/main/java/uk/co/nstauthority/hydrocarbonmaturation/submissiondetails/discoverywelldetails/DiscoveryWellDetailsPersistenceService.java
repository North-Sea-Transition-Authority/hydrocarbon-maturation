package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;


import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class DiscoveryWellDetailsPersistenceService {

  private final DiscoveryWellDetailsRepository discoveryWellDetailsRepository;
  private final WellQueryService wellQueryService;

  @Autowired
  public DiscoveryWellDetailsPersistenceService(
      DiscoveryWellDetailsRepository discoveryWellDetailsRepository,
      WellQueryService wellQueryService) {
    this.discoveryWellDetailsRepository = discoveryWellDetailsRepository;
    this.wellQueryService = wellQueryService;
  }

  @Transactional
  public void saveDiscoveryWellDetails(DiscoveryWellDetailsForm form, SubmissionDetail submissionDetail) {
    var discoveryWellDetails = getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
        .orElse(new DiscoveryWellDetail());

    if (form.getWellDetailsForm().getWell() != null) {
      discoveryWellDetails.setWellId(Integer.valueOf(form.getWellDetailsForm().getWell()));
    }
    if (!StringUtils.isBlank(form.getWellDetailsForm().getWell())) {
      discoveryWellDetails.setWellReference(wellQueryService
          .findWellById(Integer.valueOf(form.getWellDetailsForm().getWell()))
          .map(WellView::registeredNumber)
          .orElse(null)
      );
    }
    discoveryWellDetails.setSubmissionDetail(submissionDetail);
    discoveryWellDetails.setTopReservoirDatum(form.getWellDetailsForm().getReservoirDatum());
    discoveryWellDetails.setChanceOfCommercialityComments(form.getChanceOfCommercialityComments());
    discoveryWellDetails.setDiscoveryStatus(form.getDiscoveryStatus());
    discoveryWellDetails.setTopReservoirLatitudeDegrees(
        form.getWellDetailsForm().getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull());
    discoveryWellDetails.setTopReservoirLatitudeMinutes(
        form.getWellDetailsForm().getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull());
    discoveryWellDetails.setTopReservoirLatitudeSeconds(
        form.getWellDetailsForm().getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull());
    discoveryWellDetails.setTopReservoirLongitudeDegrees(
        form.getWellDetailsForm().getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull());
    discoveryWellDetails.setTopReservoirLongitudeMinutes(
        form.getWellDetailsForm().getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull());
    discoveryWellDetails.setTopReservoirLongitudeSeconds(
        form.getWellDetailsForm().getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull());
    discoveryWellDetails.setSuccessRelatedCosts(form.getSuccessRelatedCosts().createBigDecimalOrNull());
    discoveryWellDetails.setTopReservoirLongitudeHemisphere(
        form.getWellDetailsForm().getCoordinateInputForm().getLongitudeHemisphere()
    );
    discoveryWellDetails.setDataAcquisitionProgramme(form.getWellDetailsForm().getDataAcquisitionProgramme());
    discoveryWellDetails.setGeophysicalUpdateCommentary(form.getWellDetailsForm().getGeophysicalUpdate());
    discoveryWellDetails.setTotalDepthFormation(form.getWellDetailsForm().getTotalDepthFormation());
    discoveryWellDetails.setTotalWellCost(form.getDryHoleCost().createBigDecimalOrNull());
    discoveryWellDetails.setTotalDepthFormationComments(form.getWellDetailsForm().getTotalDepthFormationComments());

    discoveryWellDetailsRepository.save(discoveryWellDetails);
  }

  public Optional<DiscoveryWellDetail> getDiscoveryWellDetailBySubmissionDetail(SubmissionDetail submissionDetail) {
    return discoveryWellDetailsRepository.findBySubmissionDetail(submissionDetail);
  }
}
