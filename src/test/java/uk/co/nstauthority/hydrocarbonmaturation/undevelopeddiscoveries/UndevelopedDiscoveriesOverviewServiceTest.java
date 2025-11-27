package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistoryService;
import uk.co.nstauthority.hydrocarbonmaturation.notify.RemiAbsoluteUrlUtil;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableSortDirection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.Tag;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TagColour;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformationService;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveriesOverviewServiceTest {

  private static SummaryTableView EXPECTED_SUMMARY_TABLE;

  @Mock
  private UndevelopedDiscoveryService undevelopedDiscoveryService;

  @Mock
  private UndevelopedDiscoveryInformationService informationService;

  @Mock
  private ViewResourceRecordService resourceRecordService;

  @Mock
  private RegulatorManagedInformationSummaryService regulatorSummaryService;

  @Mock
  private LicenceQueryService licenceQueryService;

  @Mock
  private ResourceGroupService resourceGroupService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private FieldStatusHistoryService fieldStatusHistoryService;

  @Mock
  private FieldPersistenceService fieldPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private UndevelopedDiscoveriesOverviewService undevelopedDiscoveriesOverviewService;
  private final String area = "area";
  private final Integer licenceId = 1;

  @BeforeEach
  void setUp() {
    var request = new MockHttpServletRequest();
    request.setContextPath("/remi");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    EXPECTED_SUMMARY_TABLE = SummaryTableView.sortableTableBuilder()
        .withDefaultSortDirection(SummaryTableSortDirection.ASCENDING)
        .withDefaultSortIndex(0)
        .newWithHeadings(UndevelopedDiscoveryOverviewHeadings.getDisplayNames())
        .build();
  }

  @Test
  void getOverviewData_allData() {
    var undevelopedDiscovery = setupAndGetUndevelopedDiscovery();

    // WHEN an undeveloped discovery has associated information
    mockGetLatestInformation(
        undevelopedDiscovery,
        LocalDate.of(2002, 1, 11),
        BigDecimal.ONE,
        BigDecimal.valueOf(2),
        BigDecimal.valueOf(3)
    );

    var resource = undevelopedDiscovery.getResource();
    resource.setActive(false);

    // AND is tied to a field
    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setFieldStatus(FieldStatus.STATUS_200);
    mockGetFieldStatusesReturnsField(fieldStatusHistory, resource);

    var licenceIds = List.of(licenceId);
    var resourceName = "Some resource name";
    mockGetResourceRecord(resource.getId(), resourceName, licenceIds);

    mockGetRegulatorView(resource);

    // AND is in a resource group with other tracked resources (therefore is a duplicate)
    var duplicateDiscovery = setupAndGetUndevelopedDiscovery();
    var duplicateValue = mockDuplicateEntryAndGetExpectedValue(duplicateDiscovery);

    mockDuplicateResources(List.of(resource, duplicateDiscovery.getResource()),
        List.of(resource, duplicateDiscovery.getResource()));

    // AND the resource is licenced
    mockLatestApprovedSubmissionDetail(SubmissionType.CREATE_DISCOVERY, resource);
    mockLicenceStatus(LicenceStatus.EXTANT);

    mockGetAllTrackedUndevelopedDiscoveries(List.of(undevelopedDiscovery, duplicateDiscovery));

    // THEN the row has tags
    var expectedDiscoveryValue = new SummaryTableValue(
        resourceName,
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        List.of(
            new Tag("Archived", TagColour.GREY),
            new Tag("Duplicate", TagColour.RED),
            new Tag("%1$s, %1$s".formatted(FieldStatus.STATUS_200.getDisplayName()), TagColour.YELLOW)
        )
    );

    var expectedPercentageValue1 = new SummaryTableValue("1.00%", 0, null, List.of());
    var expectedPercentageValue2 = new SummaryTableValue("1.00%", 1, null, List.of());

    // AND the expected values for name, year, licenced, mmboe (1/2/3 C), % gas
    EXPECTED_SUMMARY_TABLE
        .addRow(expectedDiscoveryValue, "2002", "Yes", area, "1", "2", "3", expectedPercentageValue1, "1")
        .addRow(duplicateValue, null, "No", area, "1", "2", "3", expectedPercentageValue2, "1");

    var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(2, 1, "2.00", "4.00", "6.00");
    var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

    var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();

    assertThat(overviewData)
        .usingRecursiveComparison()
        .isEqualTo(expectedOverviewData);
  }

  @Test
  void getOverviewData_buildsData_noTags() {
    var undevelopedDiscovery = setupAndGetUndevelopedDiscovery();

    mockGetLatestInformation(
        undevelopedDiscovery,
        LocalDate.of(2002, 1, 11),
        BigDecimal.ONE,
        BigDecimal.valueOf(2),
        BigDecimal.valueOf(3)
    );

    var resource = undevelopedDiscovery.getResource();

    var licenceIds = List.of(licenceId);
    var resourceName = "Some resource name";
    mockGetResourceRecord(resource.getId(), resourceName, licenceIds);

    mockGetRegulatorView(resource);

    // WHEN there are no field status histories
    var field = new Field();
    var resourceLink = new ResourceLink();
    resourceLink.setFieldResource(resource);
    when(resourceLinkService.getResourceLinksByDiscovery(resource))
        .thenReturn(List.of(resourceLink, resourceLink));
    when(fieldPersistenceService.getFieldByResource(resource))
        .thenReturn(Optional.of(field));
    when(fieldStatusHistoryService.getMostRecentFieldStatusHistory(field))
        .thenReturn(Optional.empty());

    // AND resource is not a duplicate
    mockDuplicateResources(List.of(resource), List.of());

    mockGetAllTrackedUndevelopedDiscoveries(List.of(undevelopedDiscovery));

    // THEN there are no tags
    var expectedDiscoveryValue = new SummaryTableValue(
        resourceName,
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        List.of()
    );

    EXPECTED_SUMMARY_TABLE
        .addRow(
            expectedDiscoveryValue,
            "2002",
            "No",
            area,
            "1",
            "2",
            "3",
            new SummaryTableValue("1.00%", 0, null, List.of()),
            "1"
        );

    var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(1, 0, "1.00", "2.00", "3.00");
    var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

    var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
    assertThat(overviewData)
        .usingRecursiveComparison()
        .isEqualTo(expectedOverviewData);
  }

  @Test
  void getOverviewData_buildsData_notLicenced_hasBeenRelinquished() {
    var undevelopedDiscovery = setupAndGetUndevelopedDiscovery();

    mockGetLatestInformation(
        undevelopedDiscovery,
        LocalDate.of(2002, 1, 11),
        BigDecimal.ONE,
        BigDecimal.valueOf(2),
        BigDecimal.valueOf(3)
    );

    var resource = undevelopedDiscovery.getResource();

    var licenceIds = List.of(licenceId);
    var resourceName = "Some resource name";
    mockGetResourceRecord(resource.getId(), resourceName, licenceIds);

    mockGetRegulatorView(resource);

    mockDuplicateResources(List.of(resource), List.of());

    // WHEN resource has been relinquished
    mockLatestApprovedSubmissionDetail(SubmissionType.RELINQUISHMENT_DISCOVERY, resource);
    mockLicenceStatus(LicenceStatus.EXTANT);

    mockGetAllTrackedUndevelopedDiscoveries(List.of(undevelopedDiscovery));

    var expectedDiscoveryValue = new SummaryTableValue(
        resourceName,
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        List.of()
    );

    // THEN it is not licenced
    var expectedLicencedValue = "No";
    EXPECTED_SUMMARY_TABLE
        .addRow(
            expectedDiscoveryValue,
            "2002",
            expectedLicencedValue,
            area,
            "1",
            "2",
            "3",
            new SummaryTableValue("1.00%", 0, null, List.of()),
            "1"
        );

    var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(1, 0, "1.00", "2.00", "3.00");
    var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

    var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
    assertThat(overviewData)
        .usingRecursiveComparison()
        .isEqualTo(expectedOverviewData);
  }

  @Test
  void getOverviewData_buildsData_notLicenced_noAssociatedLicences() {
    var undevelopedDiscovery = setupAndGetUndevelopedDiscovery();

    mockGetLatestInformation(
        undevelopedDiscovery,
        LocalDate.of(2002, 1, 11),
        BigDecimal.ONE,
        BigDecimal.valueOf(2),
        BigDecimal.valueOf(3)
    );

    var resource = undevelopedDiscovery.getResource();

    // WHEN there are no associated licences
    List<Integer> licenceIds = List.of();
    var resourceName = "Some resource name";
    mockGetResourceRecord(resource.getId(), resourceName, licenceIds);

    mockGetRegulatorView(resource);

    mockDuplicateResources(List.of(resource), List.of());

    // AND the resource has not been relinquished
    mockLatestApprovedSubmissionDetail(SubmissionType.UPDATE_DISCOVERY, resource);

    mockGetAllTrackedUndevelopedDiscoveries(List.of(undevelopedDiscovery));

    var expectedDiscoveryValue = new SummaryTableValue(
        resourceName,
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        List.of()
    );

    // THEN it is not licenced
    var expectedLicencedValue = "No";
    EXPECTED_SUMMARY_TABLE
        .addRow(
            expectedDiscoveryValue,
            "2002",
            expectedLicencedValue,
            area,
            "1",
            "2",
            "3",
            new SummaryTableValue("1.00%", 0, null, List.of()),
            "1"
        );

    var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(1, 0, "1.00", "2.00", "3.00");
    var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

    var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
    assertThat(overviewData)
        .usingRecursiveComparison()
        .isEqualTo(expectedOverviewData);
  }

  @Test
  void getOverviewData_buildsData_notLicenced_noExtantLicences() {
    var undevelopedDiscovery = setupAndGetUndevelopedDiscovery();

    mockGetLatestInformation(
        undevelopedDiscovery,
        LocalDate.of(2002, 1, 11),
        BigDecimal.ONE,
        BigDecimal.valueOf(2),
        BigDecimal.valueOf(3)
    );

    var resource = undevelopedDiscovery.getResource();

    // WHEN there are no associated licences
    var licenceIds = List.of(licenceId);
    var resourceName = "Some resource name";
    mockGetResourceRecord(resource.getId(), resourceName, licenceIds);

    mockGetRegulatorView(resource);

    mockDuplicateResources(List.of(resource), List.of());

    // AND the resource has not been relinquished but has no EXTANT licences
    mockLatestApprovedSubmissionDetail(SubmissionType.UPDATE_DISCOVERY, resource);
    mockLicenceStatus(LicenceStatus.REVOKED);

    mockGetAllTrackedUndevelopedDiscoveries(List.of(undevelopedDiscovery));

    var expectedDiscoveryValue = new SummaryTableValue(
        resourceName,
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        List.of()
    );

    // THEN it is not licenced
    var expectedLicencedValue = "No";
    EXPECTED_SUMMARY_TABLE
        .addRow(
            expectedDiscoveryValue,
            "2002",
            expectedLicencedValue,
            area,
            "1",
            "2",
            "3",
            new SummaryTableValue("1.00%", 0, null, List.of()),
            "1"
        );

    var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(1, 0, "1.00", "2.00", "3.00");
    var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

    var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
    assertThat(overviewData)
        .usingRecursiveComparison()
        .isEqualTo(expectedOverviewData);
  }

  @Test
  void getOverviewData_buildsData_noAssociatedInformation() {
    // WHEN there is an undeveloped discovery with no information entries
    var undevelopedDiscovery = setupAndGetUndevelopedDiscovery();

    var resource = undevelopedDiscovery.getResource();

    // AND there are no associated licences to the resource
    List<Integer> licenceIds = List.of();
    var resourceName = "Some resource name";
    mockGetResourceRecord(resource.getId(), resourceName, licenceIds);

    mockGetRegulatorView(resource);

    mockDuplicateResources(List.of(resource), List.of());

    // AND the resource is not licenced
    mockLatestApprovedSubmissionDetail(SubmissionType.UPDATE_DISCOVERY, resource);

    mockGetAllTrackedUndevelopedDiscoveries(List.of(undevelopedDiscovery));

    var expectedDiscoveryValue = new SummaryTableValue(
        resourceName,
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        List.of()
    );

    // THEN the table has no information specific data: year, mmboe, % gas, complexity
    EXPECTED_SUMMARY_TABLE
        .addRow(
            expectedDiscoveryValue,
            null,
            "No",
            area,
            null,
            null,
            null,
            new SummaryTableValue("", 0, null, List.of()),
            null
        );

    var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(1, 0, "0.00", "0.00", "0.00");
    var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

    var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
    assertThat(overviewData)
        .usingRecursiveComparison()
        .isEqualTo(expectedOverviewData);
  }

  @Nested
  class getOverviewData_sortedRows {
    @Test
    void whenFirstUndevelopedDiscoveryHasNullDate() {
      // WHEN there is a table with a null information date
      var nullDateUndevelopedDiscovery = setupAndGetUndevelopedDiscovery();
      var tableValueWithNullDate = mockEntryWithNullDateAndGetExpectedValue("Null date entry",
          nullDateUndevelopedDiscovery);

      // AND an entry with a non-null information date
      var nonNullDateUndevelopedDiscovery = setupAndGetUndevelopedDiscovery();
      var tableValueWithNonNullDate = mockNormalEntryAndGetExpectedValue("Non null entry",
          LocalDate.of(2002, 1, 11), nonNullDateUndevelopedDiscovery);

      // AND the null date is first
      mockGetAllTrackedUndevelopedDiscoveries(List.of(nullDateUndevelopedDiscovery, nonNullDateUndevelopedDiscovery));

      var expectedPercentageValue1 = new SummaryTableValue("1.00%", 0, null, List.of());
      var expectedPercentageValue2 = new SummaryTableValue("1.00%", 1, null, List.of());

      // THEN the non-null information is first in the table
      EXPECTED_SUMMARY_TABLE
          .addRow(tableValueWithNonNullDate, "2002", "No", area, "1", "2", "3", expectedPercentageValue2, "1")
          .addRow(tableValueWithNullDate, null, "No", area, "1", "2", "3", expectedPercentageValue1, "1");

      var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(2, 0, "2.00", "4.00", "6.00");
      var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

      var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
      assertThat(overviewData)
          .usingRecursiveComparison()
          .isEqualTo(expectedOverviewData);
    }

    @Test
    void whenSecondUndevelopedDiscoveryHasNullDate() {
      // WHEN there is a table with a null information date
      var nullDateUndevelopedDiscovery = setupAndGetUndevelopedDiscovery();
      var tableValueWithNullDate = mockEntryWithNullDateAndGetExpectedValue("Null date entry",
          nullDateUndevelopedDiscovery);

      // AND an entry with a non-null information date
      var nonNullDateUndevelopedDiscovery = setupAndGetUndevelopedDiscovery();
      var tableValueWithNonNullDate = mockNormalEntryAndGetExpectedValue("Non null entry",
          LocalDate.of(2002, 1, 11), nonNullDateUndevelopedDiscovery);

      // AND the null date is second
      mockGetAllTrackedUndevelopedDiscoveries(List.of(nonNullDateUndevelopedDiscovery, nullDateUndevelopedDiscovery));

      var expectedPercentageValue1 = new SummaryTableValue("1.00%", 1, null, List.of());
      var expectedPercentageValue2 = new SummaryTableValue("1.00%", 0, null, List.of());

      // THEN the non-null information is first in the table
      EXPECTED_SUMMARY_TABLE
          .addRow(tableValueWithNonNullDate, "2002", "No", area, "1", "2", "3", expectedPercentageValue2, "1")
          .addRow(tableValueWithNullDate, null, "No", area, "1", "2", "3", expectedPercentageValue1, "1");

      var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(2, 0, "2.00", "4.00", "6.00");
      var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

      var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
      assertThat(overviewData)
          .usingRecursiveComparison()
          .isEqualTo(expectedOverviewData);
    }

    @Test
    void whenYearsAreEqual_thenRowsAreSortedByResourceName() {
      var sameDate = LocalDate.of(2002, 1, 11);

      // WHEN there is a table with resource name beginning with B
      var secondDiscovery = setupAndGetUndevelopedDiscovery();
      var expectedSecondValue = mockNormalEntryAndGetExpectedValue("BBBBBBB", sameDate,
          secondDiscovery);

      // AND an entry on the same date with a resource name beginning with A
      var firstDiscovery = setupAndGetUndevelopedDiscovery();
      var expectedFirstValue = mockNormalEntryAndGetExpectedValue("AAAAAAA", sameDate,
          firstDiscovery);

      mockGetAllTrackedUndevelopedDiscoveries(List.of(secondDiscovery, firstDiscovery));

      var expectedPercentageValue1 = new SummaryTableValue("1.00%", 0, null, List.of());
      var expectedPercentageValue2 = new SummaryTableValue("1.00%", 1, null, List.of());

      // THEN the entry beginning with A is first
      EXPECTED_SUMMARY_TABLE
          .addRow(expectedFirstValue, "2002", "No", area, "1", "2", "3", expectedPercentageValue2, "1")
          .addRow(expectedSecondValue, "2002", "No", area, "1", "2", "3", expectedPercentageValue1, "1");

      var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(2, 0, "2.00", "4.00", "6.00");
      var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

      var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
      assertThat(overviewData)
          .usingRecursiveComparison()
          .isEqualTo(expectedOverviewData);
    }

    @Test
    void whenYearsAreDifferent_thenRowsAreSortedByYear() {
      // WHEN there is an entry from 2002
      var oldestDiscovery = setupAndGetUndevelopedDiscovery();
      var oldestValue = mockNormalEntryAndGetExpectedValue("hahahaha",
          LocalDate.of(2002, 1, 11), oldestDiscovery);

      // AND an entry from 2024
      var newestDiscovery = setupAndGetUndevelopedDiscovery();
      var newestValue = mockNormalEntryAndGetExpectedValue("hahahaha",
          LocalDate.of(2024, 1, 11), newestDiscovery);

      mockGetAllTrackedUndevelopedDiscoveries(List.of(oldestDiscovery, newestDiscovery));

      var expectedPercentageValue1 = new SummaryTableValue("1.00%", 0, null, List.of());
      var expectedPercentageValue2 = new SummaryTableValue("1.00%", 1, null, List.of());

      // THEN the newest entry (2024) is first
      EXPECTED_SUMMARY_TABLE
          .addRow(newestValue, "2024", "No", area, "1", "2", "3", expectedPercentageValue2, "1")
          .addRow(oldestValue, "2002", "No", area, "1", "2", "3", expectedPercentageValue1, "1");

      var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(2, 0, "2.00", "4.00", "6.00");
      var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

      var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
      assertThat(overviewData)
          .usingRecursiveComparison()
          .isEqualTo(expectedOverviewData);
    }

    @Test
    void whenPercentageIsDifferent_percentageSortValuesAreCorrect() {
      // WHEN a lower percentage would be ranked higher in a string comparison ( "2" > "1" )
      var lowerPercentageDiscovery = setupAndGetUndevelopedDiscovery();
      var lowerPercentGas = BigDecimal.valueOf(2.1);
      var lowPercentValue = mockEntryWithPercentageGasAndGetExpectedValue(
          "hahahaha",
          LocalDate.of(2002, 1, 11),
          lowerPercentageDiscovery,
          lowerPercentGas
      );

      var higherPercentageDiscovery = setupAndGetUndevelopedDiscovery();
      var higherPercentGas = BigDecimal.valueOf(12.1);
      var highPercentValue = mockEntryWithPercentageGasAndGetExpectedValue(
          "hahahaha",
          LocalDate.of(2002, 1, 11),
          higherPercentageDiscovery,
          higherPercentGas
      );

      mockGetAllTrackedUndevelopedDiscoveries(List.of(lowerPercentageDiscovery, higherPercentageDiscovery));

      // THEN the sort value represents the numerical comparison of the percentages
      var generatedLowPercentValue = new SummaryTableValue("2.10%", 0, null, List.of());
      var generatedHighPercentValue = new SummaryTableValue("12.10%", 1, null, List.of());

      EXPECTED_SUMMARY_TABLE
          .addRow(lowPercentValue, "2002", "No", area, "1", "2", "3", generatedLowPercentValue, "1")
          .addRow(highPercentValue, "2002", "No", area, "1", "2", "3", generatedHighPercentValue, "1");

      var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(2, 0, "2.00", "4.00", "6.00");
      var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

      var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
      assertThat(overviewData)
          .usingRecursiveComparison()
          .isEqualTo(expectedOverviewData);
    }

    @Test
    void whenPercentageIsNull_percentageSortValuesAreCorrect() {
      // WHEN a lower percentage would be ranked higher in a string comparison ( "2" > "1" )
      var lowerPercentageDiscovery = setupAndGetUndevelopedDiscovery();
      var lowPercentValue = mockEntryWithPercentageGasAndGetExpectedValue(
          "hahahaha",
          LocalDate.of(2002, 1, 11),
          lowerPercentageDiscovery,
          null
      );

      var higherPercentageDiscovery = setupAndGetUndevelopedDiscovery();
      var higherPercentGas = BigDecimal.valueOf(12.1);
      var highPercentValue = mockEntryWithPercentageGasAndGetExpectedValue(
          "hahahaha",
          LocalDate.of(2002, 1, 11),
          higherPercentageDiscovery,
          higherPercentGas
      );

      mockGetAllTrackedUndevelopedDiscoveries(List.of(lowerPercentageDiscovery, higherPercentageDiscovery));

      // THEN the sort value represents the numerical comparison of the percentages
      var generatedLowPercentValue = new SummaryTableValue("", 0, null, List.of());
      var generatedHighPercentValue = new SummaryTableValue("12.10%", 1, null, List.of());

      EXPECTED_SUMMARY_TABLE
          .addRow(lowPercentValue, "2002", "No", area, "1", "2", "3", generatedLowPercentValue, "1")
          .addRow(highPercentValue, "2002", "No", area, "1", "2", "3", generatedHighPercentValue, "1");

      var expectedOverviewStats = new UndevelopedDiscoveryOverviewStats(2, 0, "2.00", "4.00", "6.00");
      var expectedOverviewData = new UndevelopedDiscoveryOverviewData(EXPECTED_SUMMARY_TABLE, expectedOverviewStats);

      var overviewData = undevelopedDiscoveriesOverviewService.getOverviewData();
      assertThat(overviewData)
          .usingRecursiveComparison()
          .isEqualTo(expectedOverviewData);
    }

    private SummaryTableValue mockEntryWithNullDateAndGetExpectedValue(String resourceName,
                                                                       UndevelopedDiscovery undevelopedDiscovery) {
      mockGetLatestInformation(
          undevelopedDiscovery,
          null,
          BigDecimal.ONE,
          BigDecimal.valueOf(2),
          BigDecimal.valueOf(3)
      );

      var resource = undevelopedDiscovery.getResource();

      // WHEN there are no associated licences
      List<Integer> licenceIds = List.of();
      mockGetResourceRecord(resource.getId(), resourceName, licenceIds);

      mockGetRegulatorView(resource);

      // AND the resource has not been relinquished
      mockLatestApprovedSubmissionDetail(SubmissionType.UPDATE_DISCOVERY, resource);

      return new SummaryTableValue(
          resourceName,
          RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
          List.of()
      );
    }

    private SummaryTableValue mockNormalEntryAndGetExpectedValue(String resourceName, LocalDate date,
                                                                 UndevelopedDiscovery undevelopedDiscovery) {
      return mockEntryWithPercentageGasAndGetExpectedValue(resourceName, date, undevelopedDiscovery, BigDecimal.ONE);
    }
  }

  private SummaryTableValue mockEntryWithPercentageGasAndGetExpectedValue(String resourceName, LocalDate date,
                                                                          UndevelopedDiscovery undevelopedDiscovery,
                                                                          BigDecimal percentageGas) {
    mockGetLatestInformationWithPercentageGas(
        undevelopedDiscovery,
        date,
        BigDecimal.ONE,
        BigDecimal.valueOf(2),
        BigDecimal.valueOf(3),
        percentageGas
    );

    var resource = undevelopedDiscovery.getResource();

    // WHEN there are no associated licences
    List<Integer> licenceIds = List.of();
    mockGetResourceRecord(resource.getId(), resourceName, licenceIds);

    mockGetRegulatorView(resource);

    // AND the resource has not been relinquished
    mockLatestApprovedSubmissionDetail(SubmissionType.UPDATE_DISCOVERY, resource);

    mockGetAllTrackedUndevelopedDiscoveries(List.of(undevelopedDiscovery));

    return new SummaryTableValue(
        resourceName,
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        List.of()
    );
  }

  private void mockGetLatestInformation(UndevelopedDiscovery undevelopedDiscovery, LocalDate date, BigDecimal mmboeOneC,
                                        BigDecimal mmboeTwoC, BigDecimal mmboeThreeC) {
    mockGetLatestInformationWithPercentageGas(undevelopedDiscovery, date, mmboeOneC, mmboeTwoC, mmboeThreeC,
        BigDecimal.ONE);
  }

  private void mockGetLatestInformationWithPercentageGas(UndevelopedDiscovery undevelopedDiscovery, LocalDate date,
                                                         BigDecimal mmboeOneC,
                                                         BigDecimal mmboeTwoC, BigDecimal mmboeThreeC,
                                                         BigDecimal percentageGas) {
    var information = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        date, undevelopedDiscovery, mmboeOneC, mmboeTwoC, mmboeThreeC
    );
    information.setPercentageGas(percentageGas);

    when(informationService.getLatestInformation(undevelopedDiscovery))
        .thenReturn(Optional.of(information));
  }

  private void mockGetAllTrackedUndevelopedDiscoveries(List<UndevelopedDiscovery> undevelopedDiscoveries) {
    when(undevelopedDiscoveryService.getAllTrackedUndevelopedDiscoveries())
        .thenReturn(undevelopedDiscoveries);
  }

  private UndevelopedDiscovery setupAndGetUndevelopedDiscovery() {
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    resource.setActive(true);

    var undevelopedDiscovery = new UndevelopedDiscovery();
    undevelopedDiscovery.setResource(resource);

    return undevelopedDiscovery;
  }

  private void mockGetResourceRecord(UUID resourceId, String resourceName, List<Integer> licenceIds) {
    var resourceRecord = new ResourceRecordSummaryView(
        ResourceType.DISCOVERY,
        null,
        resourceName,
        "COI",
        "RT9",
        null,
        null,
        licenceIds,
        List.of(),
        null,
        null,
        null,
        null
    );

    when(resourceRecordService.getResourceRecordOrThrow(resourceId))
        .thenReturn(resourceRecord);
  }

  private void mockLatestApprovedSubmissionDetail(SubmissionType submissionType, Resource resource) {
    var latestApprovedDetail = new SubmissionDetail();
    latestApprovedDetail.setSubmissionType(submissionType);

    when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
        .thenReturn(Optional.of(latestApprovedDetail));
  }

  private void mockLicenceStatus(LicenceStatus status) {
    var licenceView = new LicenceDto(null, null, status);
    when(licenceQueryService.getLicenceById(licenceId))
        .thenReturn(Optional.of(licenceView));
  }

  private void mockGetRegulatorView(Resource resource) {
    var regulatorView = new RegulatorManagedInformationSummaryView(
        null,
        "area",
        "",
        "",
        "",
        false,
        List.of(SummaryFileViewRow.empty("")),
        "",
        "",
        "",
        ""
    );

    when(regulatorSummaryService.getRegulatorManagedInformationSummaryFromResource(resource))
        .thenReturn(regulatorView);
  }

  private void mockGetFieldStatusesReturnsField(FieldStatusHistory fieldStatusHistory, Resource resource) {
    var field = new Field();

    var resourceLink = new ResourceLink();
    resourceLink.setFieldResource(resource);

    var fieldStatusHistoryOptional = Optional.of(fieldStatusHistory);

    when(resourceLinkService.getResourceLinksByDiscovery(resource))
        .thenReturn(List.of(resourceLink, resourceLink));
    when(fieldPersistenceService.getFieldByResource(resource))
        .thenReturn(Optional.of(field));
    when(fieldStatusHistoryService.getMostRecentFieldStatusHistory(field))
        .thenReturn(fieldStatusHistoryOptional);
  }

  private void mockDuplicateResources(List<Resource> resourcesInQuery, List<Resource> duplicateResources) {
    var sharedResourceGroupId = UUID.randomUUID();
    var trackedResourceGroupLinks = duplicateResources
        .stream()
        .map(resource -> {
          var resourceGroupLink = new ResourceGroupLink();
          resourceGroupLink.setResource(resource);
          resourceGroupLink.setResourceGroupId(sharedResourceGroupId);
          return resourceGroupLink;
        })
        .toList();

    when(resourceGroupService.getAllResourceGroupLinksForResources(resourcesInQuery))
        .thenReturn(trackedResourceGroupLinks);
  }

  private SummaryTableValue mockDuplicateEntryAndGetExpectedValue(UndevelopedDiscovery undevelopedDiscovery) {
    var resourceName = "duplicate";

    mockGetLatestInformation(
        undevelopedDiscovery,
        null,
        BigDecimal.ONE,
        BigDecimal.valueOf(2),
        BigDecimal.valueOf(3)
    );

    var resource = undevelopedDiscovery.getResource();

    List<Integer> licenceIds = List.of();
    mockGetResourceRecord(resource.getId(), resourceName, licenceIds);

    mockGetRegulatorView(resource);

    mockLatestApprovedSubmissionDetail(SubmissionType.UPDATE_DISCOVERY, resource);

    mockGetAllTrackedUndevelopedDiscoveries(List.of(undevelopedDiscovery));

    // THEN there is a duplicate tag
    return new SummaryTableValue(
        resourceName,
        RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(resource),
        List.of(new Tag("Duplicate", TagColour.RED))
    );
  }
}