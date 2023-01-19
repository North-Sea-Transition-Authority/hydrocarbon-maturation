package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class OpportunityDetailsFormService implements SubmissionFormService {

  private final OpportunityDetailsFormValidator opportunityDetailsFormValidator;
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public OpportunityDetailsFormService(OpportunityDetailsFormValidator opportunityDetailsFormValidator,
                                       OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                       SupportingDocumentService supportingDocumentService) {
    this.opportunityDetailsFormValidator = opportunityDetailsFormValidator;
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  OpportunityDetailsForm getOpportunityDetailsForm(SubmissionDetail submissionDetail) {
    var mapOfOpportunityFileUploads = supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY);

    return opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail)
        .map(opportunityDetail -> opportunityDetailEntityToForm(opportunityDetail, mapOfOpportunityFileUploads))
        .orElseGet(() -> {
          var form = new OpportunityDetailsForm();
          form.setMapOfOpportunity(mapOfOpportunityFileUploads);
          return form;
        });
  }

  private OpportunityDetailsForm opportunityDetailEntityToForm(OpportunityDetail opportunityDetail,
                                                               List<FileUploadForm> mapOfOpportunityUploads) {
    var form = new OpportunityDetailsForm();
    form.setOpportunityComments(opportunityDetail.getOpportunityComments());
    form.setOpportunityType(opportunityDetail.getOpportunityType());
    form.setOtherTypeOfOpportunity(opportunityDetail.getOtherTypeOfOpportunity());
    form.setKeyEvaluationWorkCarriedOut(opportunityDetail.getKeyEvaluationWorkCarriedOut());
    form.setKeyWorkRequiredForFurtherMaturation(opportunityDetail.getKeyWorkRequiredForFurtherMaturation());
    form.setMapOfOpportunity(mapOfOpportunityUploads);
    return form;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    opportunityDetailsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  public List<UploadedFileView> getMapOfOpportunityUploads(OpportunityDetailsForm opportunityDetailsForm) {
    var mapOfOpportunityUploadIdList = FileUploadUtils.getUploadIdList(opportunityDetailsForm.getMapOfOpportunity());
    return supportingDocumentService.getUploadedFileViewList(mapOfOpportunityUploadIdList);
  }
}
