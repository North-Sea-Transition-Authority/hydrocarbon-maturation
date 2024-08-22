package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OpportunityDetailsFormService implements SubmissionFormService {

  private final OpportunityDetailsFormValidator opportunityDetailsFormValidator;
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public OpportunityDetailsFormService(OpportunityDetailsFormValidator opportunityDetailsFormValidator,
                                       OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                       SubmissionSupportingDocumentService supportingDocumentService) {
    this.opportunityDetailsFormValidator = opportunityDetailsFormValidator;
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  OpportunityDetailsForm getOpportunityDetailsForm(SubmissionDetail submissionDetail) {
    var mapOfOpportunityFileUploads = supportingDocumentService
        .getUploadedFileForms(submissionDetail, FileDocumentType.MAP_OF_OPPORTUNITY);
    var geologicalCrossSectionFileUploads = supportingDocumentService
        .getUploadedFileForms(submissionDetail, FileDocumentType.GEOLOGICAL_CROSS_SECTION);
    var representativeSeismicSectionUploads = supportingDocumentService
        .getUploadedFileForms(submissionDetail, FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION);

    return opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail)
        .map(opportunityDetail -> opportunityDetailEntityToForm(
            opportunityDetail,
            mapOfOpportunityFileUploads,
            geologicalCrossSectionFileUploads,
            representativeSeismicSectionUploads))
        .orElseGet(() -> {
          var form = new OpportunityDetailsForm();
          form.setMapOfOpportunity(mapOfOpportunityFileUploads);
          form.setGeologicalCrossSection(geologicalCrossSectionFileUploads);
          form.setRepresentativeSeismicSection(representativeSeismicSectionUploads);
          return form;
        });
  }

  private OpportunityDetailsForm opportunityDetailEntityToForm(OpportunityDetail opportunityDetail,
                                                               List<UploadedFileForm> mapOfOpportunityUploads,
                                                               List<UploadedFileForm> geologicalCrossSectionUploads,
                                                               List<UploadedFileForm> representativeSeismicSectionUploads) {
    var form = new OpportunityDetailsForm();
    form.setOpportunityComments(opportunityDetail.getOpportunityComments());
    if (opportunityDetail.getSubmissionDetail().isProspectSubmission()) {
      form.setOpportunityType(opportunityDetail.getOpportunityType());
    }
    form.setOtherTypeOfOpportunity(opportunityDetail.getOtherTypeOfOpportunity());
    form.setKeyEvaluationWorkCarriedOut(opportunityDetail.getKeyEvaluationWorkCarriedOut());
    form.setKeyWorkRequiredForFurtherMaturation(opportunityDetail.getKeyWorkRequiredForFurtherMaturation());
    form.setMapOfOpportunity(mapOfOpportunityUploads);
    form.setGeologicalCrossSection(geologicalCrossSectionUploads);
    form.setRepresentativeSeismicSection(representativeSeismicSectionUploads);
    return form;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail,
                                User user) {
    opportunityDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }
}
