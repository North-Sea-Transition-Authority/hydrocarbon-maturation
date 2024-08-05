package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.FileSummaryUtil;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class AdditionalInformationSummaryService {

  static final String FILE_ROW_KEY = "Additional documents";

  private final AdditionalInformationPersistenceService additionalInformationPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public AdditionalInformationSummaryService(
      AdditionalInformationPersistenceService additionalInformationPersistenceService,
      SubmissionSupportingDocumentService supportingDocumentService) {
    this.additionalInformationPersistenceService = additionalInformationPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  public AdditionalInformationSummaryView getAdditionalInformationSummaryFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    var additionalInformation = additionalInformationPersistenceService.getAdditionalInformation(submissionDetail);

    return additionalInformation
        .map(additionalInformationObj -> createSummaryViewFromAdditionalInformation(submissionDetail,
            additionalInformationObj))
        .orElse(AdditionalInformationSummaryView.empty());
  }

  private AdditionalInformationSummaryView createSummaryViewFromAdditionalInformation(
      SubmissionDetail submissionDetail, AdditionalInformation additionalInformation) {
    var providingAdditionalComments = additionalInformation.getHasAdditionalComments() != null
        ? BooleanUtil.yesNoFromBoolean(additionalInformation.getHasAdditionalComments())
        : "";

    var additionalComments = Objects.requireNonNullElse(additionalInformation.getAdditionalComments(), "");

    var providingAdditionalDocuments = additionalInformation.getHasAdditionalDocuments() != null
        ? BooleanUtil.yesNoFromBoolean(additionalInformation.getHasAdditionalDocuments())
        : "";

    var fileUploadList = supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.ADDITIONAL_DOCUMENT
    );

    List<SummaryFileViewRow> additionalDocuments = new ArrayList<>();
    if (BooleanUtils.isTrue(additionalInformation.getHasAdditionalDocuments())) {
      additionalDocuments.addAll(
          FileSummaryUtil.convertSubmissionSupportingFilesToSummaryRows(FILE_ROW_KEY, fileUploadList, submissionDetail)
      );
    }

    return new AdditionalInformationSummaryView(
        providingAdditionalComments,
        additionalComments,
        providingAdditionalDocuments,
        additionalDocuments
    );
  }
}
