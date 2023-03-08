package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@Service
public class OpportunityDetailsSummaryService {

  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final FileUploadService fileUploadService;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public OpportunityDetailsSummaryService(OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                          FileUploadService fileUploadService,
                                          SupportingDocumentService supportingDocumentService) {
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.fileUploadService = fileUploadService;
    this.supportingDocumentService = supportingDocumentService;
  }

  public OpportunityDetailsSummaryView getOpportunityDetailsSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var opportunityDetails = opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail);

    return opportunityDetails
        .map(opportunityDetail -> createSummaryViewFromOpportunityDetail(submissionDetail, opportunityDetail))
        .orElse(OpportunityDetailsSummaryView.empty());
  }

  private OpportunityDetailsSummaryView createSummaryViewFromOpportunityDetail(SubmissionDetail submissionDetail,
                                                                               OpportunityDetail opportunityDetail) {
    var opportunityType = opportunityDetail.getOpportunityType() != null
        ? opportunityDetail.getOpportunityType().getDisplayName()
        : "";
    var otherOpportunity = OpportunityType.OTHER.equals(opportunityDetail.getOpportunityType());
    var otherOpportunityType = otherOpportunity && opportunityDetail.getOtherTypeOfOpportunity() != null
        ? opportunityDetail.getOtherTypeOfOpportunity()
        : "";
    var opportunityComments = Objects.requireNonNullElse(opportunityDetail.getOpportunityComments(), "");
    var keyEvaluationWork = Objects.requireNonNullElse(opportunityDetail.getKeyEvaluationWorkCarriedOut(), "");
    var keyWorkRequired = Objects.requireNonNullElse(opportunityDetail.getKeyWorkRequiredForFurtherMaturation(), "");

    var mapOfOpportunityUploadFormList = supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail,
        SupportingDocumentType.MAP_OF_OPPORTUNITY);

    var geologicalCrossSectionUploadFormList = supportingDocumentService
        .getFileUploadFormListForSubmissionAndType(
            submissionDetail,
            SupportingDocumentType.GEOLOGICAL_CROSS_SECTION);


    return new OpportunityDetailsSummaryView(opportunityType,
        otherOpportunity,
        otherOpportunityType,
        opportunityComments,
        keyEvaluationWork,
        keyWorkRequired,
        getFileUploadList(mapOfOpportunityUploadFormList, submissionDetail),
        getFileUploadList(geologicalCrossSectionUploadFormList, submissionDetail));
  }

  private Map<UploadedFile, String> getFileUploadList(List<FileUploadForm> fileUploadForms,
                                                      SubmissionDetail submissionDetail) {
    return fileUploadForms
        .stream()
        .collect(
            Collectors.toMap(
                key -> fileUploadService.findUploadedFileOrThrow(key.getUploadedFileId()),
                value -> ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
                    .download(submissionDetail.getId(), value.getUploadedFileId()))));
  }
}
