package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWellsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class GeneralDetailSummaryService implements SummarySectionService<SubmissionDetail> {

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

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final LicencePersistenceService licencePersistenceService;
  private final OnshoreOffshoreService onshoreOffshoreService;

  private final PlannedSanctionedWellsSummaryService plannedSanctionedWellsSummaryService;

  @Autowired
  public GeneralDetailSummaryService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                     QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                     LicencePersistenceService licencePersistenceService,
                                     OnshoreOffshoreService onshoreOffshoreService,
                                     PlannedSanctionedWellsSummaryService plannedSanctionedWellsSummaryService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.licencePersistenceService = licencePersistenceService;
    this.onshoreOffshoreService = onshoreOffshoreService;
    this.plannedSanctionedWellsSummaryService = plannedSanctionedWellsSummaryService;
  }

  public GeneralDetailSummaryView getGeneralDetailSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var generalDetail = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail);
    return generalDetail.map(detail -> {
      var quadrantAndBlocks = quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(detail)
              .stream()
              .sorted(new ReferenceBlockComparator())
              .toList();
      var licences = licencePersistenceService.getLicencesByGeneralDetail(detail)
          .stream()
          .sorted(new LicenceReferenceComparator())
          .toList();
      return createSummaryViewFromGeneralDetail(detail, quadrantAndBlocks, licences);
    })
    .orElse(GeneralDetailSummaryView.empty(submissionDetail.getSubmissionType(), getResourceTypeDisplay(submissionDetail)));
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
      return DateUtil.formatDateOrBlank(detail.getRelinquishmentDate());
    } else {
      return "";
    }
  }

  private String getResourceTypeDisplay(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase();
  }

  private List<String> getQuadBlocksDisplay(List<QuadrantAndBlock> quadrantAndBlocks) {
    return quadrantAndBlocks.stream()
        .map(QuadrantAndBlock::getQuadrantBlockReference)
        .toList();
  }

  private List<String> getLicenceDisplay(List<Licence> licences) {
    return licences.stream()
        .sorted(Comparator.comparing(Licence::getLicenceReference))
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

  @Override
  public Optional<SummarySection> getSummarySection(SubmissionDetail submissionDetail) {
    if (!submissionDetail.isFieldSubmission()) {
      return Optional.empty();
    }
    var summaryItems = List.of(getSummaryItem(submissionDetail));
    return Optional.of(new SummarySection(GENERAL_DETAIL_SECTION_ORDER, summaryItems, ""));
  }

  private SummaryItem getSummaryItem(SubmissionDetail submissionDetail) {
    var summaryCards = new ArrayList<SummaryCard>();
    summaryCards.add(getGeneralDetailsSummaryCard(submissionDetail));

    if (submissionDetail.isFieldDevelopmentPlanAddendumSubmission()
        || submissionDetail.isFieldDevelopmentPlanSubmission()) {
      summaryCards.add(plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail));
      summaryCards.add(plannedSanctionedWellsSummaryService.getWellTypesSummaryCard(submissionDetail));
      summaryCards.add(plannedSanctionedWellsSummaryService.getHoleCompletionTypesSummaryCard(submissionDetail));
      summaryCards.add(plannedSanctionedWellsSummaryService.getPlannedCompletionTypesSummaryCard(submissionDetail));
      summaryCards.add(plannedSanctionedWellsSummaryService.getArtificialLiftMechanismsSummaryCard(submissionDetail));
    }

    return SummaryItem.withCards(GENERAL_DETAILS, summaryCards);
  }

  private SummaryCard getGeneralDetailsSummaryCard(SubmissionDetail submissionDetail) {
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

    return SummaryCard.simpleSummaryCardWithHeading(GENERAL_DETAILS, summaryDataView);
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
}
