package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import com.google.common.collect.Streams;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.notify.RemiAbsoluteUrlUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableSortDirection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TableHeadingStyle;
import uk.co.nstauthority.hydrocarbonmaturation.summary.Tag;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TagColour;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformation;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BigDecimalUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalFormatUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class UndevelopedDiscoveriesOverviewService {

  private final UndevelopedDiscoveryService undevelopedDiscoveryService;
  private final UndevelopedDiscoveryInformationService informationService;
  private final LicenceQueryService licenceQueryService;
  private final ResourceGroupService resourceGroupService;
  private final UndevelopedDiscoveriesQueryService undevelopedDiscoveriesQueryService;

  @Autowired
  public UndevelopedDiscoveriesOverviewService(UndevelopedDiscoveryService undevelopedDiscoveryService,
                                               UndevelopedDiscoveryInformationService informationService,
                                               LicenceQueryService licenceQueryService,
                                               ResourceGroupService resourceGroupService,
                                               UndevelopedDiscoveriesQueryService undevelopedDiscoveriesQueryService) {
    this.undevelopedDiscoveryService = undevelopedDiscoveryService;
    this.informationService = informationService;
    this.licenceQueryService = licenceQueryService;
    this.resourceGroupService = resourceGroupService;
    this.undevelopedDiscoveriesQueryService = undevelopedDiscoveriesQueryService;
  }

  public UndevelopedDiscoveryOverviewData getOverviewData() {
    List<UndevelopedDiscovery> allTrackedInformation = undevelopedDiscoveryService
        .getAllTrackedUndevelopedDiscoveries();

    var latestInformation = getLatestDiscoveryInformation(allTrackedInformation);

    var resources = latestInformation
        .stream()
        .map(information -> information.getUndevelopedDiscovery().getResource())
        .toList();

    var trackedResourceGroupLinks = resourceGroupService.getAllResourceGroupLinksForResources(resources);

    var duplicateResourceIds = getDuplicateResourceIds(trackedResourceGroupLinks);

    var resourceRecords = undevelopedDiscoveriesQueryService.getLatestResourceRecords(
        resources.stream().map(Resource::getId).toList()
    );

    var allLicenceIds = resourceRecords.stream()
        .flatMap(resource -> resource.licenceIds() != null ? resource.licenceIds().stream() : Stream.empty())
        .toList();

    var allExtantLicenceIds = licenceQueryService.getExtantLicencesByIds(NumberUtil.toListOfStrings(allLicenceIds))
        .stream()
        .map(LicenceDto::licenceId)
        .toList();

    List<UUID> licencedResourceIds =
        resourceRecords.stream()
            .filter(
                resource -> resource.licenceIds() != null
                    && resource.licenceIds().stream().anyMatch(allExtantLicenceIds::contains)
            )
            .map(UndevelopedDiscoveryResourceDto::resourceId)
            .toList();

    Map<UUID, Boolean> groupLicensedMap = getGroupLicensedMap(resources, trackedResourceGroupLinks);

    Map<UUID, String> geographicalAreaMap = resourceRecords
        .stream()
        .collect(Collectors.toMap(
            UndevelopedDiscoveryResourceDto::resourceId,
            resource -> resource.geographicalArea() != null ? resource.geographicalArea().getDisplayName() : ""
        ));

    Map<UUID, String> resourceNameMap = resourceRecords
        .stream()
        .collect(Collectors.toMap(
            UndevelopedDiscoveryResourceDto::resourceId,
            UndevelopedDiscoveryResourceDto::resourceName)
        );

    Map<UUID, SubmissionType> submissionTypeMap = resourceRecords
        .stream()
        .collect(Collectors.toMap(
            UndevelopedDiscoveryResourceDto::resourceId,
            UndevelopedDiscoveryResourceDto::submissionType)
        );

    var resourceIds = resourceRecords.stream().map(UndevelopedDiscoveryResourceDto::resourceId).toList();

    Map<UUID, List<UndevelopedDiscoveryFieldDto>> fieldDtoMap = undevelopedDiscoveriesQueryService.getLinkedFields(resourceIds)
        .stream()
        .collect(Collectors.groupingBy(UndevelopedDiscoveryFieldDto::discoveryResourceId));

    var rows = latestInformation
        .stream()
        .map(information -> generateRowValues(
            information,
            duplicateResourceIds,
            resourceNameMap,
            geographicalAreaMap,
            submissionTypeMap,
            licencedResourceIds,
            groupLicensedMap,
            fieldDtoMap))
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

    var groupLicensedUndevelopedDiscoveries = (int) rows.stream()
        .filter(UndevelopedDiscoveryRowValues::groupLicensed)
        .count();

    var groupLicensedRows = rows.stream().filter(UndevelopedDiscoveryRowValues::groupLicensed).toList();
    var groupUnlicensedRows = rows.stream().filter(row -> !row.groupLicensed()).toList();

    var totalMmboeOneC = sumMmboe(rows, row -> BigDecimalUtil.getValueOrZero(row.mmboeOneC()));
    var totalMmboeTwoC = sumMmboe(rows, row -> BigDecimalUtil.getValueOrZero(row.mmboeTwoC()));
    var totalMmboeThreeC = sumMmboe(rows, row -> BigDecimalUtil.getValueOrZero(row.mmboeThreeC()));

    var groupLicensedMmboeOneC = sumMmboe(groupLicensedRows, row -> BigDecimalUtil.getValueOrZero(row.mmboeOneC()));
    var groupLicensedMmboeTwoC = sumMmboe(groupLicensedRows, row -> BigDecimalUtil.getValueOrZero(row.mmboeTwoC()));
    var groupLicensedMmboeThreeC = sumMmboe(groupLicensedRows, row -> BigDecimalUtil.getValueOrZero(row.mmboeThreeC()));

    var groupUnlicensedMmboeOneC = sumMmboe(groupUnlicensedRows, row -> BigDecimalUtil.getValueOrZero(row.mmboeOneC()));
    var groupUnlicensedMmboeTwoC = sumMmboe(groupUnlicensedRows, row -> BigDecimalUtil.getValueOrZero(row.mmboeTwoC()));
    var groupUnlicensedMmboeThreeC = sumMmboe(groupUnlicensedRows, row -> BigDecimalUtil.getValueOrZero(row.mmboeThreeC()));

    return new UndevelopedDiscoveryOverviewStats(
        undevelopedDiscoveries,
        licensedUndevelopedDiscoveries,
        groupLicensedUndevelopedDiscoveries,
        totalMmboeOneC,
        totalMmboeTwoC,
        totalMmboeThreeC,
        groupLicensedMmboeOneC,
        groupLicensedMmboeTwoC,
        groupLicensedMmboeThreeC,
        groupUnlicensedMmboeOneC,
        groupUnlicensedMmboeTwoC,
        groupUnlicensedMmboeThreeC
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
                                                          List<UUID> duplicateResourceIds,
                                                          Map<UUID, String> resourceNameMap,
                                                          Map<UUID, String> geographicalAreaMap,
                                                          Map<UUID, SubmissionType> submissionTypeMap,
                                                          List<UUID> licencedResourceIds,
                                                          Map<UUID, Boolean> groupLicensedMap,
                                                          Map<UUID, List<UndevelopedDiscoveryFieldDto>> fieldDtoMap) {
    var resource = information.getUndevelopedDiscovery().getResource();

    var isLicensed = licencedResourceIds.contains(resource.getId())
        && !submissionTypeMap.get(resource.getId()).equals(SubmissionType.RELINQUISHMENT_DISCOVERY);

    var percentageGasFormatted = information.getPercentageGas() == null
        ? ""
        : "%s%%".formatted(DecimalFormatUtil.bigDecimalToTwoDecimalPlace(information.getPercentageGas()));

    var discoveryValue = new SummaryTableValue(
        resourceNameMap.get(resource.getId()),
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        getTags(resource, duplicateResourceIds, fieldDtoMap)
    );

    var year = information.getInformationDate() == null
        ? null
        : information.getInformationDate().getYear();

    return new UndevelopedDiscoveryRowValues(
        discoveryValue,
        year,
        isLicensed,
        groupLicensedMap.get(resource.getId()),
        geographicalAreaMap.get(resource.getId()),
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
            rowValues.groupLicensed(),
            rowValues.geographicalArea(),
            rowValues.mmboeOneC(),
            rowValues.mmboeTwoC(),
            rowValues.mmboeThreeC(),
            new SummaryTableValue(rowValues.percentageGas(), (int) percentSortValue, null, List.of()),
            rowValues.complexity())
        .build();
  }

  private List<Tag> getTags(Resource resource,
                            List<UUID> duplicateResourceIds,
                            Map<UUID, List<UndevelopedDiscoveryFieldDto>> fieldDtoMap
  ) {
    var tags = new ArrayList<Tag>();

    if (BooleanUtils.isFalse(resource.getActive())) {
      tags.add(new Tag("Archived", TagColour.GREY));
    }

    var isDuplicate = duplicateResourceIds.contains(resource.getId());
    if (isDuplicate) {
      tags.add(new Tag("Duplicate", TagColour.RED));
    }

    var fieldDto = fieldDtoMap.get(resource.getId());

    if (fieldDto != null) {
      var fieldStatusCsv = fieldDto
          .stream()
          .map(field -> field.fieldStatus() != null ? field.fieldStatus().getDisplayName() : null)
          .collect(Collectors.joining(", "));

      if (!fieldStatusCsv.isEmpty()) {
        tags.add(new Tag(fieldStatusCsv, TagColour.YELLOW));
      }
    }

    return tags;
  }

  private List<UUID> getDuplicateResourceIds(List<ResourceGroupLink> trackedResourceGroupLinks) {
    var resourceGroupIdToCountMap = new HashMap<ResourceGroup, Integer>();
    trackedResourceGroupLinks.forEach(trackedResourceGroupLink ->
        resourceGroupIdToCountMap.merge(trackedResourceGroupLink.getResourceGroup(), 1, Integer::sum));

    return trackedResourceGroupLinks
        .stream()
        .filter(resourceGroupLink -> !resourceGroupIdToCountMap.get(resourceGroupLink.getResourceGroup()).equals(1))
        .map(resourceGroupLink -> resourceGroupLink.getResource().getId())
        .toList();
  }

  private Map<UUID, Boolean> getGroupLicensedMap(List<Resource> resources,
                                                 List<ResourceGroupLink> trackedResourceGroupLinks) {
    var resourceIdToGroup = trackedResourceGroupLinks.stream()
        .collect(Collectors.toMap(
            link -> link.getResource().getId(),
            ResourceGroupLink::getResourceGroup
        ));

    var groups = trackedResourceGroupLinks.stream()
        .map(ResourceGroupLink::getResourceGroup)
        .distinct()
        .toList();

    var allGroupLinks = resourceGroupService.getAllResourceGroupLinksForGroups(groups);

    var groupToAllResourceIds = allGroupLinks.stream()
        .collect(Collectors.groupingBy(
            ResourceGroupLink::getResourceGroup,
            Collectors.mapping(link -> link.getResource().getId(), Collectors.toList())
        ));

    var allGroupResourceIds = allGroupLinks.stream()
        .map(link -> link.getResource().getId())
        .distinct()
        .toList();

    var relevantRecords = undevelopedDiscoveriesQueryService.getLatestResourceRecords(allGroupResourceIds);

    var allLicenceIds = relevantRecords.stream()
        .flatMap(record -> record.licenceIds() != null ? record.licenceIds().stream() : Stream.empty())
        .toList();

    var extantLicenceIds = licenceQueryService.getExtantLicencesByIds(NumberUtil.toListOfStrings(allLicenceIds))
        .stream()
        .map(LicenceDto::licenceId)
        .toList();

    var licencedResourceIds = relevantRecords.stream()
        .filter(record -> record.licenceIds() != null
            && record.licenceIds().stream().anyMatch(extantLicenceIds::contains))
        .map(UndevelopedDiscoveryResourceDto::resourceId)
        .toList();

    return resources.stream()
        .collect(Collectors.toMap(
            Resource::getId,
            resource -> groupToAllResourceIds.get(resourceIdToGroup.get(resource.getId()))
                .stream().anyMatch(licencedResourceIds::contains)
        ));
  }

  private List<UndevelopedDiscoveryInformation> getLatestDiscoveryInformation(List<UndevelopedDiscovery> undevelopedDiscoveries) {
    return informationService.getLatestDiscoveryInformation(undevelopedDiscoveries);
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
    var rowOnePercentage = !rowOne.percentageGas().isBlank() ? rowOne.percentageGas() : String.valueOf(0);
    var rowTwoPercentage = !rowTwo.percentageGas().isBlank() ? rowTwo.percentageGas() : String.valueOf(0);
    return new BigDecimal(rowOnePercentage.replace("%", ""))
        .compareTo(new BigDecimal(rowTwoPercentage.replace("%", "")));
  }

  private BigDecimal sumMmboe(List<UndevelopedDiscoveryRowValues> rows,
                              Function<UndevelopedDiscoveryRowValues, BigDecimal> getMmboeBigDecimal) {
    return rows.stream()
        .map(getMmboeBigDecimal)
        .reduce((a, b) -> a.add(b, MathContext.DECIMAL64))
        .orElse(BigDecimal.ZERO);
  }
}
