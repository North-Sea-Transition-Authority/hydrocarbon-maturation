package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.FileSummaryUtil;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

@Service
public class OpportunityDetailsSummaryService {

  static final String MAP_OF_OPPORTUNITY_FILE_KEY = "Map of opportunity";
  static final String REPRESENTATIVE_SEISMIC_SECTION_FILE_KEY = "Representative seismic section";
  static final String GEOLOGICAL_FILE_KEY = "Geological cross section";

  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public OpportunityDetailsSummaryService(OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                          SubmissionSupportingDocumentService supportingDocumentService) {
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  public OpportunityDetailsSummaryView getOpportunityDetailsSummaryFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
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

    var opportunityFiles = FileSummaryUtil.convertSubmissionSupportingFilesToSummaryRows(
        MAP_OF_OPPORTUNITY_FILE_KEY,
        mapOfOpportunityUploadFormList,
        submissionDetail
    );
    if (opportunityFiles.isEmpty()) {
      opportunityFiles = List.of(SummaryFileViewRow.empty(MAP_OF_OPPORTUNITY_FILE_KEY));
    }

    var seismicFiles = FileSummaryUtil.convertSubmissionSupportingFilesToSummaryRows(
        REPRESENTATIVE_SEISMIC_SECTION_FILE_KEY,
        representativeSeismicSectionUploadFormList,
        submissionDetail
    );
    if (seismicFiles.isEmpty()) {
      seismicFiles = List.of(SummaryFileViewRow.empty(REPRESENTATIVE_SEISMIC_SECTION_FILE_KEY));
    }

    var geologicalFiles = FileSummaryUtil.convertSubmissionSupportingFilesToSummaryRows(
        GEOLOGICAL_FILE_KEY,
        geologicalCrossSectionUploadFormList,
        submissionDetail
    );
    if (geologicalFiles.isEmpty()) {
      geologicalFiles = List.of(SummaryFileViewRow.empty(GEOLOGICAL_FILE_KEY));
    }

    return new OpportunityDetailsSummaryView(opportunityType,
        otherOpportunity,
        otherOpportunityType,
        opportunityComments,
        keyEvaluationWork,
        keyWorkRequired,
        opportunityFiles,
        seismicFiles,
        geologicalFiles
    );
  }
}