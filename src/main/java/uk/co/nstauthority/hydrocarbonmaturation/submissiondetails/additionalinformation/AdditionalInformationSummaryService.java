package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class AdditionalInformationSummaryService {

  private final AdditionalInformationPersistenceService additionalInformationPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public AdditionalInformationSummaryService(
      AdditionalInformationPersistenceService additionalInformationPersistenceService,
      SubmissionSupportingDocumentService supportingDocumentService) {
    this.additionalInformationPersistenceService = additionalInformationPersistenceService;
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

    var fileUploadList = supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.ADDITIONAL_DOCUMENT);

    Map<UploadedFile, String> additionalDocumentsUploadsAndLinks = Map.of();

    if (BooleanUtils.isTrue(additionalInformation.getHasAdditionalDocuments())) {
      additionalDocumentsUploadsAndLinks = fileUploadList
          .stream()
          .collect(
              Collectors.toMap(
                  file -> file,
                  value -> ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
                      .download(submissionDetail.getId(), value.getId()))));
    }

    return new AdditionalInformationSummaryView(
        providingAdditionalComments,
        additionalComments,
        providingAdditionalDocuments,
        additionalDocumentsUploadsAndLinks);
  }
}
