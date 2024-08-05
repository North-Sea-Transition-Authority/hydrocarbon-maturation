package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class GeneralDetailSummaryService {

  public static final String GENERAL_DETAILS = "General details";
  public static final int GENERAL_DETAIL_SECTION_ORDER = 10;
  public static final String LICENCES = "Licences";
  public static final String WHOLLY_WITHIN_UKCS = "Wholly located within UKCS";
  public static final String WHOLLY_WITHIN_ENGLAND = "Wholly located within England";
  public static final String PERCENT_WITHIN_UKCS = "Percentage located in UKCS";
  public static final String PERCENT_WITHIN_ENGLAND = "Percentage located in England";
  public static final String QUADRANT_AND_BLOCKS = "Quadrant and blocks";
  public static final String FIELD_STOPPED_PRODUCING_DATE = "Date field stopped producing";
  public static final String CAPITAL_PROJECT_SCOPE_DELIVERED = "Capital project scope delivered";
  public static final String CURRENT_DRIVE_MECHANISM = "Current drive mechanism";
  public static final String FINAL_CONCEPT_SELECT_REPORT = "Final concept select report";
  public static final String FINAL_FIELD_DEVELOPMENT_PLAN = "Final field development plan";
  public static final String FINAL_FIELD_DEVELOPMENT_PLAN_ADDENDUM = "Final field development plan addendum";
  public static final String CESSATION_OF_PRODUCTION_REPORT = "Cessation of production report";

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final LicencePersistenceService licencePersistenceService;
  private final OnshoreOffshoreService onshoreOffshoreService;
  private final SubmissionSupportingDocumentService supportingDocumentService;


  @Autowired
  public GeneralDetailSummaryService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                     QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                     LicencePersistenceService licencePersistenceService,
                                     OnshoreOffshoreService onshoreOffshoreService,
                                     SubmissionSupportingDocumentService supportingDocumentService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.licencePersistenceService = licencePersistenceService;
    this.onshoreOffshoreService = onshoreOffshoreService;
    this.supportingDocumentService = supportingDocumentService;
  }

  public GeneralDetailSummaryView getGeneralDetailSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var generalDetail = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail);
    return generalDetail.map(
            detail -> {
              var quadrantAndBlocks = quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(detail)
                  .stream()
                  .toList();

              var licences = licencePersistenceService.getLicencesByGeneralDetail(detail)
                  .stream()
                  .toList();
              return createSummaryViewFromGeneralDetail(detail, quadrantAndBlocks, licences);
            })
        .orElse(GeneralDetailSummaryView.empty(submissionDetail.getSubmissionType(),
            getResourceTypeDisplay(submissionDetail)));
  }

  private GeneralDetailSummaryView createSummaryViewFromGeneralDetail(GeneralDetail detail,
                                                                      List<QuadrantAndBlock> quadrantAndBlocks,
                                                                      List<Licence> licences) {
    var licenceApplication = "";
    var isLicenceApplicationInfoSource = false;
    var informationSource = detail.getInformationSource();
    if (informationSource == InformationSource.LICENCE_APPLICATION) {
      licenceApplication = detail.getLicenceApplicationReference() != null
          ? detail.getLicenceApplicationReference()
          : "";
      isLicenceApplicationInfoSource = true;
    }
    return new GeneralDetailSummaryView(
        detail.getSubmissionDetail().getSubmissionType(),
        BooleanUtil.yesNoFromBoolean(detail.getResourceLocatedInUkcs(), ""),
        BooleanUtil.yesNoFromBoolean(detail.getResourceLocatedInEngland(), ""),
        getPercentageLocatedInUkcs(detail),
        getPercentageLocatedInEngland(detail),
        getInformationSourceDisplay(detail),
        isLicenceApplicationInfoSource,
        licenceApplication,
        getQuadBlocksDisplay(quadrantAndBlocks),
        getLicenceDisplay(licences),
        getResourceTypeDisplay(detail.getSubmissionDetail()),
        getDateOfRelinquishmentDisplay(detail)
    );
  }

  private String getDateOfRelinquishmentDisplay(GeneralDetail detail) {
    if (detail.getSubmissionDetail().isRelinquishment()) {
      return DateUtil.formatLongDateOrBlank(detail.getRelinquishmentDate());
    } else {
      return "";
    }
  }

  private String getResourceTypeDisplay(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase();
  }

  private List<String> getQuadBlocksDisplay(List<QuadrantAndBlock> quadrantAndBlocks) {
    return quadrantAndBlocks.stream()
        .sorted(new ReferenceBlockComparator())
        .map(QuadrantAndBlock::getQuadrantBlockReference)
        .toList();
  }

  private List<String> getLicenceDisplay(List<Licence> licences) {
    return licences.stream()
        .sorted(new LicenceReferenceComparator())
        .map(Licence::getLicenceReference)
        .toList();
  }

  private String getInformationSourceDisplay(GeneralDetail detail) {
    var informationSourceDisplay = "";
    var informationSource = detail.getInformationSource();
    if (informationSource != null) {
      if (informationSource == InformationSource.OTHER) {
        informationSourceDisplay = detail.getOtherInformationSource();
      } else {
        informationSourceDisplay = informationSource.getDisplayName();
      }
    }
    return informationSourceDisplay;
  }

  private String getPercentageLocatedInUkcs(GeneralDetail detail) {
    return detail.getPercentageLocatedInUkcs() != null && !detail.getResourceLocatedInUkcs()
        ? detail.getPercentageLocatedInUkcs().toString() + "%"
        : "";
  }

  private String getPercentageLocatedInEngland(GeneralDetail detail) {
    return detail.getPercentageLocatedInEngland() != null && !detail.getResourceLocatedInEngland()
        ? detail.getPercentageLocatedInEngland().toString() + "%"
        : "";
  }

  public SummaryCard getGeneralDetailsSummary(SubmissionDetail submissionDetail) {
    var generalDetailOptional = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail);

    if (generalDetailOptional.isEmpty()) {
      return SummaryCard.emptySummaryCardWithHeading(GENERAL_DETAILS);
    }

    var generalDetail = generalDetailOptional.get();

    var licences = licencePersistenceService.getLicencesByGeneralDetail(generalDetail)
        .stream()
        .sorted(new LicenceReferenceComparator())
        .map(Licence::getLicenceReference)
        .toList();
    var summaryDataView = SummaryDataView.newWithKeyValue(LICENCES, String.join(", ", licences));

    var isOffshore = onshoreOffshoreService.getOnshoreOffshore(submissionDetail) == OnshoreOffshore.OFFSHORE;
    if (isOffshore) {
      summaryDataView.addKeyValue(WHOLLY_WITHIN_UKCS, generalDetail.getResourceLocatedInUkcs());
      if (BooleanUtils.isFalse(generalDetail.getResourceLocatedInUkcs())) {
        summaryDataView.addKeyValue(PERCENT_WITHIN_UKCS, generalDetail.getPercentageLocatedInUkcs());
      }
    } else {
      summaryDataView.addKeyValue(WHOLLY_WITHIN_ENGLAND, generalDetail.getResourceLocatedInEngland());
      if (BooleanUtils.isFalse(generalDetail.getResourceLocatedInEngland())) {
        summaryDataView.addKeyValue(PERCENT_WITHIN_ENGLAND, generalDetail.getPercentageLocatedInEngland());
      }
    }

    var quadrantAndBlocks = quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail)
        .stream()
        .sorted(new ReferenceBlockComparator())
        .map(QuadrantAndBlock::getQuadrantBlockReference)
        .toList();

    summaryDataView.addKeyValue(QUADRANT_AND_BLOCKS, String.join(", ", quadrantAndBlocks));

    addFieldSummaryValues(submissionDetail, generalDetail, summaryDataView);

    List<SummaryFileViewRow> summaryFileViews = new ArrayList<>();
    if (!submissionDetail.isCessationOfProductionSubmission()) {
      addFieldReportFileIfPresent(summaryFileViews, submissionDetail);
    }

    return SummaryCard.simpleSummaryWithFiles(GENERAL_DETAILS, summaryDataView, summaryFileViews);
  }

  private void addFieldReportFileIfPresent(List<SummaryFileViewRow> summaryFileViews,
                                           SubmissionDetail submissionDetail) {
    var cardHeading = getFieldReportTypeForSubmissionDetail(submissionDetail.getSubmissionType());

    var uploadedFiles = supportingDocumentService.getUploadedFiles(submissionDetail, FileDocumentType.FIELD_REPORT);
    if (uploadedFiles.isEmpty()) {
      summaryFileViews.add(SummaryFileViewRow.empty(cardHeading));
    } else {
      var fieldReportFile = uploadedFiles.get(0);
      var downloadUrl = ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
          .download(submissionDetail.getId(), fieldReportFile.getId()));

      summaryFileViews.add(SummaryFileViewRow.from(cardHeading, fieldReportFile, downloadUrl));
    }
  }

  private void addFieldSummaryValues(SubmissionDetail submissionDetail,
                                     GeneralDetail generalDetail,
                                     SummaryDataView summaryDataView) {
    if (submissionDetail.isCessationOfProductionSubmission()) {
      summaryDataView.addKeyValue(FIELD_STOPPED_PRODUCING_DATE,
          DateUtil.formatDateOrBlank(generalDetail.getFieldStoppedProducingDate()));
    }

    if (submissionDetail.isFieldDevelopmentPlanSubmission() || submissionDetail.isFieldDevelopmentPlanAddendumSubmission()) {
      summaryDataView.addKeyValue(CAPITAL_PROJECT_SCOPE_DELIVERED, generalDetail.getCapitalProjectScopeDelivered());
    }

    if (submissionDetail.isFieldSubmission()) {
      if (DriveMechanism.OTHER.equals(generalDetail.getCurrentDriveMechanism())) {
        summaryDataView.addKeyValue(CURRENT_DRIVE_MECHANISM, generalDetail.getOtherDriveMechanism());
      } else {
        var currentDriveMechanism = generalDetail.getCurrentDriveMechanism() != null
            ? generalDetail.getCurrentDriveMechanism().getDisplayName()
            : "";
        summaryDataView.addKeyValue(CURRENT_DRIVE_MECHANISM, currentDriveMechanism);
      }
    }
  }

  private String getFieldReportTypeForSubmissionDetail(SubmissionType submissionType) {
    return switch (submissionType) {
      case CREATE_CSR -> FINAL_CONCEPT_SELECT_REPORT;
      case CREATE_FDP -> FINAL_FIELD_DEVELOPMENT_PLAN;
      case CREATE_FDPA -> FINAL_FIELD_DEVELOPMENT_PLAN_ADDENDUM;
      case CREATE_COP -> CESSATION_OF_PRODUCTION_REPORT;
      default -> throw new IllegalArgumentException("There is no field report type for submission type %s"
          .formatted(submissionType.getTypeName()));
    };
  }
}
