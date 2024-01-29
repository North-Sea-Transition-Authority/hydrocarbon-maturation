package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;

@Service
public class OpportunityDetailsSummaryService {

  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public OpportunityDetailsSummaryService(OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                          SubmissionSupportingDocumentService supportingDocumentService) {
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
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

    var mapOfOpportunityUploadFormList = supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.MAP_OF_OPPORTUNITY
    );

    var geologicalCrossSectionUploadFormList = supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.GEOLOGICAL_CROSS_SECTION
    );

    var representativeSeismicSectionUploadFormList = supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION
    );

    return new OpportunityDetailsSummaryView(opportunityType,
        otherOpportunity,
        otherOpportunityType,
        opportunityComments,
        keyEvaluationWork,
        keyWorkRequired,
        getFileUploadList(mapOfOpportunityUploadFormList, submissionDetail),
        getFileUploadList(representativeSeismicSectionUploadFormList, submissionDetail),
        getFileUploadList(geologicalCrossSectionUploadFormList, submissionDetail));
  }

  private Map<UploadedFile, String> getFileUploadList(List<UploadedFile> fileUploadForms,
                                                      SubmissionDetail submissionDetail) {
    return fileUploadForms
        .stream()
        .collect(
            Collectors.toMap(
                Function.identity(),
                value -> ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
                    .download(submissionDetail.getId(), value.getId()))
            )
        );
  }
}
