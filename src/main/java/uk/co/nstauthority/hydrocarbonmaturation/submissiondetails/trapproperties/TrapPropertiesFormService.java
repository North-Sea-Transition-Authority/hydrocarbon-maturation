package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class TrapPropertiesFormService implements SubmissionFormService {

  private final TrapPropertiesFormValidator trapPropertiesFormValidator;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  TrapPropertiesFormService(TrapPropertiesFormValidator trapPropertiesFormValidator,
                            TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                            ProbabilityDistributionFormService probabilityDistributionFormService,
                            SupportingDocumentService supportingDocumentService) {
    this.trapPropertiesFormValidator = trapPropertiesFormValidator;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.supportingDocumentService = supportingDocumentService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    trapPropertiesFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  TrapPropertiesForm getTrapPropertiesForm(SubmissionDetail submissionDetail) {
    List<FileUploadForm> prospectDepthMapUploads = supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail, SupportingDocumentType.PROSPECT_DEPTH_MAP);

    return trapPropertiesPersistenceService.getTrapProperty(submissionDetail)
        .map(trapProperty -> trapPropertyEntityToForm(trapProperty, prospectDepthMapUploads))
        .orElse(new TrapPropertiesForm());
  }

  private TrapPropertiesForm trapPropertyEntityToForm(TrapProperty trapProperty,
                                                      List<FileUploadForm> prospectDepthMapUploads) {
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

    var submissionDetail = trapProperty.getSubmissionDetail();
    form.setDepthToCrest(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT_DISCOVERY
    ));

    form.setDepthToSpillPoint(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY
    ));

    form.setProductiveArea(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA
    ));

    form.setProspectDepthMap(prospectDepthMapUploads);

    return form;
  }

  public List<UploadedFileView> getProspectDepthMapUploads(TrapPropertiesForm trapPropertiesForm) {
    var prospectDepthMapUploadIdList = FileUploadUtils.getUploadIdList(trapPropertiesForm.getProspectDepthMap());
    return supportingDocumentService.getUploadedFileViewList(prospectDepthMapUploadIdList);
  }
}
