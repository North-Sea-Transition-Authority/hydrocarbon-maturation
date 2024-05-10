package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistoryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.Tag;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TagColour;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformation;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalFormatUtil;

@Service
public class UndevelopedDiscoveriesOverviewService {

  static final String DISCOVERY_HEADING = "Discovery";
  static final String YEAR_ADDED_HEADING = "Year added";
  static final String LICENSED_HEADING = "Licensed";
  static final String AREA_HEADING = "Geographical area";
  static final String OIL_EQUIVALENT_1C_HEADING = "mmboe (1C)";
  static final String OIL_EQUIVALENT_2C_HEADING = "mmboe (2C)";
  static final String OIL_EQUIVALENT_3C_HEADING = "mmboe (3C)";
  static final String PERCENTAGE_GAS_HEADING = "Percentage gas";
  static final String COMPLEXITY_HEADING = "Complexity";
  private final UndevelopedDiscoveryService undevelopedDiscoveryService;
  private final UndevelopedDiscoveryInformationService informationService;
  private final ViewResourceRecordService resourceRecordService;
  private final RegulatorManagedInformationSummaryService regulatorSummaryService;
  private final LicenceQueryService licenceQueryService;
  private final ResourceGroupService resourceGroupService;
  private final ResourceLinkService resourceLinkService;
  private final FieldStatusHistoryService fieldStatusHistoryService;
  private final FieldPersistenceService fieldPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public UndevelopedDiscoveriesOverviewService(UndevelopedDiscoveryService undevelopedDiscoveryService,
                                               UndevelopedDiscoveryInformationService informationService,
                                               ViewResourceRecordService resourceRecordService,
                                               RegulatorManagedInformationSummaryService regulatorSummaryService,
                                               LicenceQueryService licenceQueryService,
                                               ResourceGroupService resourceGroupService,
                                               ResourceLinkService resourceLinkService,
                                               FieldStatusHistoryService fieldStatusHistoryService,
                                               FieldPersistenceService fieldPersistenceService,
                                               SubmissionDetailService submissionDetailService) {
    this.undevelopedDiscoveryService = undevelopedDiscoveryService;
    this.informationService = informationService;
    this.resourceRecordService = resourceRecordService;
    this.regulatorSummaryService = regulatorSummaryService;
    this.licenceQueryService = licenceQueryService;
    this.resourceGroupService = resourceGroupService;
    this.resourceLinkService = resourceLinkService;
    this.fieldStatusHistoryService = fieldStatusHistoryService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  public SummaryTableView getOverviewSummaryTableView() {
    var tableView = getBaseTableView();

    var latestInformationPerUndevelopedDiscovery = undevelopedDiscoveryService.getAllTrackedUndevelopedDiscoveries()
        .stream()
        .map(informationService::getLatestInformation)
        .flatMap(Optional::stream);

    var tableRows = latestInformationPerUndevelopedDiscovery
        .sorted(Comparator.comparing(UndevelopedDiscoveryInformation::getInformationDate).reversed())
        .map(this::toRow);

    tableRows.forEach(tableView::addRow);

    return tableView;
  }

  private SummaryTableView getBaseTableView() {
    return SummaryTableView.newWithHeading(
        DISCOVERY_HEADING,
        YEAR_ADDED_HEADING,
        LICENSED_HEADING,
        AREA_HEADING,
        OIL_EQUIVALENT_1C_HEADING,
        OIL_EQUIVALENT_2C_HEADING,
        OIL_EQUIVALENT_3C_HEADING,
        PERCENTAGE_GAS_HEADING,
        COMPLEXITY_HEADING
    );
  }

  private SummaryTableRow toRow(UndevelopedDiscoveryInformation information) {
    var resource = information.getUndevelopedDiscovery()
        .getResource();

    var resourceRecord = resourceRecordService.getResourceRecordOrThrow(resource.getId());
    var regulatorView = regulatorSummaryService.getRegulatorManagedInformationSummaryFromResource(resource);
    var isLicensed = resourceIsLicensed(resourceRecord, resource);

    var percentageGasFormatted = information.getPercentageGas() == null
        ? ""
        : "%s%%".formatted(DecimalFormatUtil.bigDecimalToTwoDecimalPlace(information.getPercentageGas()));

    var discoveryValue = new SummaryTableValue(
        resourceRecord.resourceNameAndCentreOfInterest(),
        ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId())),
        getTags(resource)
    );

    return SummaryTableRow.builder()
        .withValues(
            discoveryValue,
            information.getInformationDate().getYear(),
            isLicensed,
            regulatorView.geographicalArea(),
            information.getOilEquivalentRecoverableOneC(),
            information.getOilEquivalentRecoverableTwoC(),
            information.getOilEquivalentRecoverableThreeC(),
            percentageGasFormatted,
            information.getCalculatedComplexity())
        .build();
  }

  private Boolean resourceIsLicensed(ResourceRecordSummaryView resourceRecord, Resource resource) {
    var extantLicenses = resourceRecord.licenceIds() == null
        ? List.of()
        : getExtantLicenceViews(resourceRecord.licenceIds());

    var latestApprovedDetail = submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource);
    var hasBeenRelinquished = latestApprovedDetail
        .map(this::submissionTypeIsRelinquished)
        .orElse(Boolean.FALSE);

    return !extantLicenses.isEmpty() && !hasBeenRelinquished;
  }

  private List<LicenceView> getExtantLicenceViews(List<Integer> licenseIds) {
    return licenseIds
        .stream()
        .map(licenceQueryService::getLicenceById)
        .flatMap(Optional::stream)
        .filter(licenseView -> licenseView.licenceStatus().equals(LicenceStatus.EXTANT))
        .toList();
  }

  private List<FieldStatus> getFieldStatuses(Resource resource) {
    var resourceLinks = resourceLinkService.getResourceLinksByDiscovery(resource);

    var fields = resourceLinks
        .stream()
        .map(ResourceLink::getFieldResource)
        .map(fieldPersistenceService::getFieldByResource)
        .flatMap(Optional::stream);

    return fields
        .map(fieldStatusHistoryService::getMostRecentFieldStatusHistory)
        .flatMap(Optional::stream)
        .map(FieldStatusHistory::getFieldStatus)
        .sorted(Comparator.comparing(FieldStatus::getDisplayOrder))
        .toList();
  }

  private List<Tag> getTags(Resource resource) {
    var tags = new ArrayList<Tag>();

    var isDuplicate = resourceGroupService.isInGroupAndIsNotPreferredView(resource);
    if (Boolean.TRUE.equals(isDuplicate)) {
      tags.add(new Tag("Duplicate", TagColour.RED));
    }

    var fieldStatusCsv = getFieldStatuses(resource)
        .stream()
        .map(FieldStatus::getDisplayName)
        .collect(Collectors.joining(", "));

    if (!fieldStatusCsv.isEmpty()) {
      tags.add(new Tag(fieldStatusCsv, TagColour.YELLOW));
    }

    return tags;
  }

  private Boolean submissionTypeIsRelinquished(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmissionType().equals(SubmissionType.RELINQUISHMENT_DISCOVERY);
  }
}
