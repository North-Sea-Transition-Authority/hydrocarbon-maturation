package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInputForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.ReferenceBlockComparator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class TrapPropertiesFormService implements SubmissionFormService {

  private final TrapPropertiesFormValidator trapPropertiesFormValidator;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public TrapPropertiesFormService(TrapPropertiesFormValidator trapPropertiesFormValidator,
                                   TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                                   ProbabilityDistributionFormService probabilityDistributionFormService,
                                   QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                   GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   SupportingDocumentService supportingDocumentService) {
    this.trapPropertiesFormValidator = trapPropertiesFormValidator;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    trapPropertiesFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  TrapPropertiesForm getTrapPropertiesForm(SubmissionDetail submissionDetail) {
    var prospectDepthMapUploads = supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail, SupportingDocumentType.PROSPECT_DEPTH_MAP);

    return trapPropertiesPersistenceService.getTrapProperty(submissionDetail)
        .map(trapProperty -> trapPropertyEntityToForm(trapProperty, prospectDepthMapUploads))
        .orElseGet(() -> {
          var form = new TrapPropertiesForm();
          form.setProspectDepthMap(prospectDepthMapUploads);
          return form;
        });
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

  public List<UploadedFileView> getProspectDepthMapUploads(TrapPropertiesForm trapPropertiesForm) {
    var prospectDepthMapUploadIdList = FileUploadUtils.getUploadIdList(trapPropertiesForm.getProspectDepthMap());
    return supportingDocumentService.getUploadedFileViewList(prospectDepthMapUploadIdList);
  }

  public Map<String, String> getQuadrantAndBlocks(SubmissionDetail submissionDetail) {
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(detail -> quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(detail)
            .stream()
            .sorted(new ReferenceBlockComparator())
            .collect(StreamUtil.toLinkedHashMap(
                QuadrantAndBlock::getQuadrantBlockReference,
                QuadrantAndBlock::getQuadrantBlockReference)))
        .orElseGet(LinkedHashMap::new);
  }
}
