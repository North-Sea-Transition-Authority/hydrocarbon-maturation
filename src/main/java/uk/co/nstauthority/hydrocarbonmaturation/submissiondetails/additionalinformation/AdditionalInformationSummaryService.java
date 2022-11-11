package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class AdditionalInformationSummaryService {

  private final AdditionalInformationPersistenceService additionalInformationPersistenceService;
  private final FileUploadService fileUploadService;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public AdditionalInformationSummaryService(
      AdditionalInformationPersistenceService additionalInformationPersistenceService,
      FileUploadService fileUploadService,
      SupportingDocumentService supportingDocumentService) {
    this.additionalInformationPersistenceService = additionalInformationPersistenceService;
    this.fileUploadService = fileUploadService;
    this.supportingDocumentService = supportingDocumentService;
  }

  public AdditionalInformationSummaryView getAdditionalInformationSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var additionalInformation = additionalInformationPersistenceService.getAdditionalInformation(submissionDetail);

    return additionalInformation
        .map(additionalInformationObj -> createSummaryViewFromAdditionalInformation(submissionDetail, additionalInformationObj))
        .orElse(AdditionalInformationSummaryView.empty());
  }

  private AdditionalInformationSummaryView createSummaryViewFromAdditionalInformation(SubmissionDetail submissionDetail,
                                                                               AdditionalInformation additionalInformation) {
    var providingAdditionalComments = additionalInformation.getHasAdditionalComments() != null
        ? BooleanUtil.yesNoFromBoolean(additionalInformation.getHasAdditionalComments())
        : "";

    var additionalComments = Objects.requireNonNullElse(additionalInformation.getAdditionalComments(), "");

    var providingAdditionalDocuments = additionalInformation.getHasAdditionalDocuments() != null
        ? BooleanUtil.yesNoFromBoolean(additionalInformation.getHasAdditionalDocuments())
        : "";

    var fileUploadList = supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail,
        SupportingDocumentType.ADDITIONAL_DOCUMENT);

    Map<UploadedFile, String> additionalDocumentsUploadsAndLinks = Map.of();

    if (BooleanUtils.isTrue(additionalInformation.getHasAdditionalDocuments())) {
      additionalDocumentsUploadsAndLinks = fileUploadList
          .stream()
          .collect(
              Collectors.toMap(
                  key -> fileUploadService.findUploadedFileOrThrow(key.getUploadedFileId()),
                  value -> ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
                      .download(submissionDetail.getId(), value.getUploadedFileId()))));
    }

    return new AdditionalInformationSummaryView(
        providingAdditionalComments,
        additionalComments,
        providingAdditionalDocuments,
        additionalDocumentsUploadsAndLinks);
  }
}
