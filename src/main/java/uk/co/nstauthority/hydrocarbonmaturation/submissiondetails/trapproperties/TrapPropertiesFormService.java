package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
public class TrapPropertiesFormService {

  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public TrapPropertiesFormService(TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                                   ProbabilityDistributionFormService probabilityDistributionFormService,
                                   SubmissionSupportingDocumentService supportingDocumentService) {
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.supportingDocumentService = supportingDocumentService;
  }

  TrapPropertiesForm getTrapPropertiesForm(SubmissionDetail submissionDetail) {
    var prospectDepthMapUploads = supportingDocumentService.getUploadedFileForms(
        submissionDetail, FileDocumentType.PROSPECT_DEPTH_MAP);

    return trapPropertiesPersistenceService.getTrapProperty(submissionDetail)
        .map(trapProperty -> trapPropertyEntityToForm(trapProperty, prospectDepthMapUploads))
        .orElseGet(() -> {
          var form = new TrapPropertiesForm();
          form.setProspectDepthMap(prospectDepthMapUploads);
          return form;
        });
  }

  private TrapPropertiesForm trapPropertyEntityToForm(TrapProperty trapProperty,
                                                      List<UploadedFileForm> prospectDepthMapUploads) {
    var form = new TrapPropertiesForm();
    form.setTrapType(trapProperty.getTrapType());
    form.setTrapDescription(trapProperty.getTrapDescription());
    form.setOtherTrapType(trapProperty.getOtherTrapType());

    var latitudeCoordinates = CoordinateInput.createCoordinateInputForForm(trapProperty.getLatitudeDegrees(),
        trapProperty.getLatitudeMinutes(), trapProperty.getLatitudeSeconds());

    var longitudeCoordinates = CoordinateInput.createCoordinateInputForForm(trapProperty.getLongitudeDegrees(),
        trapProperty.getLongitudeMinutes(), trapProperty.getLongitudeSeconds());

    var coordinateInputForm = new CoordinateInputForm();
    coordinateInputForm.setLatitudeCoordinates(latitudeCoordinates);
    coordinateInputForm.setLongitudeCoordinates(longitudeCoordinates);
    coordinateInputForm.setLongitudeHemisphere(trapProperty.getLongitudeHemisphere());
    form.setCoordinateInputForm(coordinateInputForm);
    form.setDatum(trapProperty.getDatum());
    if (trapProperty.getCentreOfInterestQuadrantBlock() != null) {
      form.setCentreOfInterestQuadrantBlock(trapProperty.getCentreOfInterestQuadrantBlock());
    }
    var submissionDetail = trapProperty.getSubmissionDetail();

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      form.setDepthToCrest(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT
      ));

      form.setDepthToSpillPoint(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT
      ));

      form.setProductiveArea(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT
      ));
    } else if (resourceType == ResourceType.DISCOVERY) {
      form.setDepthToCrest(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_DISCOVERY
      ));

      form.setDepthToSpillPoint(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_DISCOVERY
      ));

      form.setProductiveArea(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_DISCOVERY
      ));
    }

    form.setProspectDepthMap(prospectDepthMapUploads);

    return form;
  }
}
