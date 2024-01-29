package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
public class TrapPropertiesPersistenceService {

  private final TrapPropertiesRepository trapPropertiesRepository;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public TrapPropertiesPersistenceService(TrapPropertiesRepository trapPropertiesRepository,
                                          ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                          SubmissionSupportingDocumentService supportingDocumentService) {
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
    trapProperty.setDatum(form.getDatum());
    trapProperty.setCentreOfInterestQuadrantBlock(form.getCentreOfInterestQuadrantBlock());

    if (form.getTrapType() == TrapType.OTHER) {
      trapProperty.setOtherTrapType(form.getOtherTrapType());
    } else {
      trapProperty.setOtherTrapType(null);
    }

    trapPropertiesRepository.save(trapProperty);

    var resourceType = submissionDetail.getSubmission().getResource().getType();

    // save depth to crest details
    var depthToCrestInstance = ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      depthToCrestInstance = ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        depthToCrestInstance,
        form.getDepthToCrest(),
        submissionDetail);

    // save depth to spill point details
    var depthToSpillPointInstance = ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      depthToSpillPointInstance = ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        depthToSpillPointInstance,
        form.getDepthToSpillPoint(),
        submissionDetail
    );

    // save productive area details
    var productiveAreaInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      productiveAreaInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_DISCOVERY;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        productiveAreaInstance,
        form.getProductiveArea(),
        submissionDetail
    );

    supportingDocumentService
        .saveDocuments(submissionDetail, form.getProspectDepthMap(), FileDocumentType.PROSPECT_DEPTH_MAP);
  }

  public Optional<TrapProperty> getTrapProperty(SubmissionDetail submissionDetail) {
    return trapPropertiesRepository.findBySubmissionDetail(submissionDetail);
  }

  public Optional<String> getCentreOfInterestBlockFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return trapPropertiesRepository.findBySubmissionDetail(submissionDetail)
        .map(TrapProperty::getCentreOfInterestQuadrantBlock)
        .or(Optional::empty);
  }
}
