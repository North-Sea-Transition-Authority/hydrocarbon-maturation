package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import com.google.common.collect.Streams;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
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
import uk.co.nstauthority.hydrocarbonmaturation.notify.RemiAbsoluteUrlUtil;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableSortDirection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TableHeadingStyle;
import uk.co.nstauthority.hydrocarbonmaturation.summary.Tag;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TagColour;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformation;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BigDecimalUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalFormatUtil;

@Service
public class UndevelopedDiscoveriesOverviewService {

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

  public UndevelopedDiscoveryOverviewData getOverviewData() {
    List<UndevelopedDiscoveryInformation> allTrackedInformation = undevelopedDiscoveryService
        .getAllTrackedUndevelopedDiscoveries()
        .stream()
        .map(this::getDiscoveryInformation)
        .toList();

    var duplicateResourceIds = getDuplicateResourceIds(allTrackedInformation);

    var rows = allTrackedInformation
        .stream()
        .map(information -> generateRowValues(information, duplicateResourceIds))
        .toList();

    return new UndevelopedDiscoveryOverviewData(
        getOverviewSummaryTableView(rows),
        getOverviewStats(rows)
    );
  }

  private SummaryTableView getOverviewSummaryTableView(List<UndevelopedDiscoveryRowValues> rows) {
    var tableView = getBaseTableView();

    var sortedRowsStream = rows.stream()
        .sorted(this::comparePercentages);

    Streams.mapWithIndex(sortedRowsStream, this::toRow)
        .sorted(this::compareRows)
        .forEach(row -> tableView.tableRows().add(row));

    return tableView;
  }

  private UndevelopedDiscoveryOverviewStats getOverviewStats(List<UndevelopedDiscoveryRowValues> rows) {
    var undevelopedDiscoveries = rows.size();

    var licensedUndevelopedDiscoveries = (int) rows.stream()
        .filter(UndevelopedDiscoveryRowValues::licensed)
        .count();

    var mmboeOneC = sumMmboe(
        rows,
        row -> BigDecimalUtil.getValueOrZero(row.mmboeOneC())
    );

    var mmboeTwoC = sumMmboe(
        rows,
        row -> BigDecimalUtil.getValueOrZero(row.mmboeTwoC())
    );

    var mmboeThreeC = sumMmboe(
        rows,
        row -> BigDecimalUtil.getValueOrZero(row.mmboeThreeC())
    );

    return new UndevelopedDiscoveryOverviewStats(
        undevelopedDiscoveries,
        licensedUndevelopedDiscoveries,
        mmboeOneC,
        mmboeTwoC,
        mmboeThreeC
    );
  }

  private SummaryTableView getBaseTableView() {
    return SummaryTableView.sortableTableBuilder()
        .newWithHeadings(UndevelopedDiscoveryOverviewHeadings.getDisplayNames())
        .withHeadingStyle(TableHeadingStyle.COLUMN)
        .withDefaultSortDirection(SummaryTableSortDirection.ASCENDING)
        .withDefaultSortIndex(0)
        .build();
  }

  private UndevelopedDiscoveryRowValues generateRowValues(UndevelopedDiscoveryInformation information,
                                                          List<UUID> duplicateResourceIds) {
    var resource = information.getUndevelopedDiscovery().getResource();

    var resourceRecord = resourceRecordService.getResourceRecordOrThrow(resource.getId());
    var regulatorView = regulatorSummaryService.getRegulatorManagedInformationSummaryFromResource(resource);
    var isLicensed = resourceIsLicensed(resourceRecord, resource);

    var percentageGasFormatted = information.getPercentageGas() == null
        ? ""
        : "%s%%".formatted(DecimalFormatUtil.bigDecimalToTwoDecimalPlace(information.getPercentageGas()));

    var discoveryValue = new SummaryTableValue(
        resourceRecord.resourceName(),
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        getTags(resource, duplicateResourceIds)
    );

    var year = information.getInformationDate() == null
        ? null
        : information.getInformationDate().getYear();

    return new UndevelopedDiscoveryRowValues(
        discoveryValue,
        year,
        isLicensed,
        regulatorView.geographicalArea(),
        information.getOilEquivalentRecoverableOneC(),
        information.getOilEquivalentRecoverableTwoC(),
        information.getOilEquivalentRecoverableThreeC(),
        percentageGasFormatted,
        information.getCalculatedComplexity()
    );
  }

  private SummaryTableRow toRow(UndevelopedDiscoveryRowValues rowValues, long percentSortValue) {
    return SummaryTableRow.builder()
        .withValues(
            rowValues.discovery(),
            rowValues.yearAdded(),
            rowValues.licensed(),
            rowValues.geographicalArea(),
            rowValues.mmboeOneC(),
            rowValues.mmboeTwoC(),
            rowValues.mmboeThreeC(),
            new SummaryTableValue(rowValues.percentageGas(), (int) percentSortValue, null, List.of()),
            rowValues.complexity())
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

  private List<Tag> getTags(Resource resource, List<UUID> duplicateResourceIds) {
    var tags = new ArrayList<Tag>();

    var isDuplicate = duplicateResourceIds.contains(resource.getId());
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

  private List<UUID> getDuplicateResourceIds(List<UndevelopedDiscoveryInformation> trackedInformationList) {
    // all resources of tracked information entries
    var trackedResources = trackedInformationList
        .stream()
        .map(information -> information.getUndevelopedDiscovery().getResource())
        .toList();

    // all resource group links for tracked resources
    var trackedResourceGroupLinks = resourceGroupService.getAllResourceGroupLinksForResources(trackedResources);

    // hashmap to count the number of tracked items in each resource group
    var resourceGroupIdToCountMap = new HashMap<UUID, Integer>();
    trackedResourceGroupLinks.forEach(trackedResourceGroupLink ->
        resourceGroupIdToCountMap.merge(trackedResourceGroupLink.getResourceGroupId(), 1, Integer::sum));

    // return ID for each resource in a group with more than one tracked resource
    return trackedResourceGroupLinks
        .stream()
        .filter(resourceGroupLink -> !resourceGroupIdToCountMap.get(resourceGroupLink.getResourceGroupId()).equals(1))
        .map(resourceGroupLink -> resourceGroupLink.getResource().getId())
        .toList();
  }

  private Boolean submissionTypeIsRelinquished(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmissionType().equals(SubmissionType.RELINQUISHMENT_DISCOVERY);
  }

  private UndevelopedDiscoveryInformation getDiscoveryInformation(UndevelopedDiscovery undevelopedDiscovery) {
    return informationService.getLatestInformation(undevelopedDiscovery)
        .orElseGet(() -> getEmptyInformation(undevelopedDiscovery));
  }

  private UndevelopedDiscoveryInformation getEmptyInformation(UndevelopedDiscovery undevelopedDiscovery) {
    var undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(undevelopedDiscovery);
    return undevelopedDiscoveryInformation;
  }

  private int compareRows(SummaryTableRow rowOne,
                          SummaryTableRow rowTwo) {
    var yearAddedColumnIndex = UndevelopedDiscoveryOverviewHeadings.YEAR_ADDED.getColumnIndex();
    var rowOneYearAdded = rowOne.rowValues().get(yearAddedColumnIndex).value();
    var rowTwoYearAdded = rowTwo.rowValues().get(yearAddedColumnIndex).value();

    var discoveryNameColumnIndex = UndevelopedDiscoveryOverviewHeadings.DISCOVERY.getColumnIndex();
    var rowOneDiscoveryName = rowOne.rowValues().get(discoveryNameColumnIndex).value();
    var rowTwoDiscoveryName = rowTwo.rowValues().get(discoveryNameColumnIndex).value();

    if (rowOneYearAdded == null) {
      return 1;
    } else if (rowTwoYearAdded == null) {
      return -1;
    }

    var yearsCompared = rowTwoYearAdded.compareTo(rowOneYearAdded);
    if (yearsCompared == 0) {
      // if years are equal, sort by name
      return rowOneDiscoveryName.toLowerCase().compareTo(rowTwoDiscoveryName.toLowerCase());
    } else {
      return yearsCompared;
    }
  }

  private int comparePercentages(UndevelopedDiscoveryRowValues rowOne,
                                 UndevelopedDiscoveryRowValues rowTwo) {
    return new BigDecimal(rowOne.percentageGas().replace("%", ""))
        .compareTo(new BigDecimal(rowTwo.percentageGas().replace("%", "")));
  }

  private BigDecimal sumMmboe(List<UndevelopedDiscoveryRowValues> rows,
                              Function<UndevelopedDiscoveryRowValues, BigDecimal> getMmboeBigDecimal) {
    return rows.stream()
        .map(getMmboeBigDecimal)
        .reduce((a, b) -> a.add(b, MathContext.DECIMAL64))
        .orElse(BigDecimal.ZERO);
  }
}
