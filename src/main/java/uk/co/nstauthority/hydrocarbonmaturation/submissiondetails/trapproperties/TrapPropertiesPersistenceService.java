package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;

@Service
public class TrapPropertiesPersistenceService {

  private final TrapPropertiesRepository trapPropertiesRepository;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public TrapPropertiesPersistenceService(TrapPropertiesRepository trapPropertiesRepository,
                                          ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                          SupportingDocumentService supportingDocumentService) {
    this.trapPropertiesRepository = trapPropertiesRepository;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  @Transactional
  public void saveTrapProperties(TrapPropertiesForm form, SubmissionDetail submissionDetail) {
    var trapProperty = getTrapProperty(submissionDetail).orElse(new TrapProperty());
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setTrapDescription(form.getTrapDescription());
    trapProperty.setTrapType(form.getTrapType());
    trapProperty.setLatitudeDegrees(
        form.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull());
    trapProperty.setLatitudeMinutes(
        form.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull());
    trapProperty.setLatitudeSeconds(
        form.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull());
    trapProperty.setLongitudeDegrees(
        form.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull());
    trapProperty.setLongitudeMinutes(
        form.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull());
    trapProperty.setLongitudeSeconds(
        form.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull());
    trapProperty.setLongitudeHemisphere(form.getCoordinateInputForm().getLongitudeHemisphere());

    if (form.getTrapType() == TrapType.OTHER) {
      trapProperty.setOtherTrapType(form.getOtherTrapType());
    } else {
      trapProperty.setOtherTrapType(null);
    }

    trapPropertiesRepository.save(trapProperty);

    // save depth to crest details
    if (form.getDepthToCrest().getProbabilityDistributionType() != null) {
      var depthToCrestInstance = ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT_DISCOVERY;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          depthToCrestInstance,
          form.getDepthToCrest(),
          submissionDetail);
    }

    // save depth to spill point details
    if (form.getDepthToSpillPoint().getProbabilityDistributionType() != null) {
      var depthToSpillPointInstance = ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          depthToSpillPointInstance,
          form.getDepthToSpillPoint(),
          submissionDetail
      );
    }

    // save productive area details
    if (form.getProductiveArea().getProbabilityDistributionType() != null) {
      var productiveAreaInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          productiveAreaInstance,
          form.getProductiveArea(),
          submissionDetail
      );
    }

    supportingDocumentService.updateSupportingDocumentFileDescriptions(form.getProspectDepthMap());
  }

  Optional<TrapProperty> getTrapProperty(SubmissionDetail submissionDetail) {
    return trapPropertiesRepository.findBySubmissionDetail(submissionDetail);
  }
}
