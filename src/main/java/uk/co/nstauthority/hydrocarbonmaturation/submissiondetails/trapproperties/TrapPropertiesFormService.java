package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadFdsView;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
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
    List<FileUploadDetail> prospectDepthMapUploads = supportingDocumentService.getFileUploadDetailListForSubmissionAndType(
        submissionDetail, SupportingDocumentType.PROSPECT_DEPTH_MAP);

    return trapPropertiesPersistenceService.getTrapProperty(submissionDetail)
        .map(trapProperty -> trapPropertyEntityToForm(trapProperty, prospectDepthMapUploads))
        .orElse(new TrapPropertiesForm());
  }

  private TrapPropertiesForm trapPropertyEntityToForm(TrapProperty trapProperty,
                                                      List<FileUploadDetail> prospectDepthMapUploads) {
    var form = new TrapPropertiesForm();
    form.setTrapType(trapProperty.getTrapType());
    form.setTrapDescription(trapProperty.getTrapDescription());
    form.setOtherTrapType(trapProperty.getOtherTrapType());

    var latitudeCoordinates = createCoordinateInputForEntity(trapProperty.getLatitudeDegrees(),
        trapProperty.getLatitudeMinutes(), trapProperty.getLatitudeSeconds());

    var longitudeCoordinates = createCoordinateInputForEntity(trapProperty.getLongitudeDegrees(),
        trapProperty.getLongitudeMinutes(), trapProperty.getLongitudeSeconds());

    form.setLatitudeCoordinates(latitudeCoordinates);
    form.setLongitudeCoordinates(longitudeCoordinates);
    form.setLongitudeHemisphere(trapProperty.getLongitudeHemisphere());

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

  private CoordinateInput createCoordinateInputForEntity(BigDecimal degrees, BigDecimal minutes, BigDecimal seconds) {
    var coordinateInput = new CoordinateInput();

    var degreesCoordinate = degrees == null ? new DecimalInput() : new DecimalInput(degrees.toString());
    var minutesCoordinate = minutes == null ? new DecimalInput() : new DecimalInput(minutes.toString());
    var secondsCoordinate = seconds == null ? new DecimalInput() : new DecimalInput(seconds.toString());

    coordinateInput.setDegrees(degreesCoordinate);
    coordinateInput.setMinutes(minutesCoordinate);
    coordinateInput.setSeconds(secondsCoordinate);

    return coordinateInput;
  }

  public List<FileUploadFdsView> getProspectDepthMapUploads(TrapPropertiesForm trapPropertiesForm) {
    var prospectDepthMapUploadIdList = FileUploadUtils.getUploadIdList(trapPropertiesForm.getProspectDepthMap());
    return supportingDocumentService.getFileUploadFdsViewList(prospectDepthMapUploadIdList);
  }
}
