package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistoryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.Tag;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TagColour;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformation;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformationService;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveriesOverviewServiceTest {

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

  @Nested
  class getOverviewSummaryTableView {

    private Resource resource;
    private final String resourceName = "resource name";
    private final String area = "area";
    private final Integer licenceId = 1;

    @BeforeEach
    void setUp() {
      setUpUndevelopedDiscoveriesAndInformation();
    }

    @Test
    void getOverviewSummaryTableView_buildsTable() {
      var fieldStatusHistory = new FieldStatusHistory();
      fieldStatusHistory.setFieldStatus(FieldStatus.STATUS_200);

      setUpToRowMethodRequirements(Boolean.TRUE, fieldStatusHistory);
      mockHasBeenRelinquished(SubmissionType.CREATE_DISCOVERY);
      mockExtantLicences(Boolean.TRUE, licenceId);

      var expectedDiscoveryValue = new SummaryTableValue(
          resourceName,
          ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId())),
          List.of(
              new Tag("Duplicate", TagColour.RED),
              new Tag("%1$s, %1$s".formatted(FieldStatus.STATUS_200.getDisplayName()), TagColour.YELLOW)
          )
      );

      var expectedTableView = SummaryTableView.newWithHeading(
              UndevelopedDiscoveriesOverviewService.DISCOVERY_HEADING,
              UndevelopedDiscoveriesOverviewService.YEAR_ADDED_HEADING,
              UndevelopedDiscoveriesOverviewService.LICENSED_HEADING,
              UndevelopedDiscoveriesOverviewService.AREA_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_1C_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_2C_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_3C_HEADING,
              UndevelopedDiscoveriesOverviewService.PERCENTAGE_GAS_HEADING,
              UndevelopedDiscoveriesOverviewService.COMPLEXITY_HEADING)
          .addRow(
              expectedDiscoveryValue,
              "2002",
              "Yes",
              area,
              "1",
              "1",
              "1",
              "1.00%",
              "1"
          );

      var overviewSummaryTableView = undevelopedDiscoveriesOverviewService.getOverviewSummaryTableView();
      assertThat(overviewSummaryTableView)
          .usingRecursiveComparison()
          .isEqualTo(expectedTableView);
    }

    @Test
    void getOverviewSummaryTableView_buildsTable_noTags() {
      setUpToRowMethodRequirements(Boolean.FALSE, null);
      mockHasBeenRelinquished(SubmissionType.CREATE_DISCOVERY);
      mockExtantLicences(Boolean.TRUE, licenceId);

      var expectedDiscoveryValue = new SummaryTableValue(
          resourceName,
          ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId())),
          List.of()
      );

      var expectedTableView = SummaryTableView.newWithHeading(
              UndevelopedDiscoveriesOverviewService.DISCOVERY_HEADING,
              UndevelopedDiscoveriesOverviewService.YEAR_ADDED_HEADING,
              UndevelopedDiscoveriesOverviewService.LICENSED_HEADING,
              UndevelopedDiscoveriesOverviewService.AREA_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_1C_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_2C_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_3C_HEADING,
              UndevelopedDiscoveriesOverviewService.PERCENTAGE_GAS_HEADING,
              UndevelopedDiscoveriesOverviewService.COMPLEXITY_HEADING)
          .addRow(
              expectedDiscoveryValue,
              "2002",
              "Yes",
              area,
              "1",
              "1",
              "1",
              "1.00%",
              "1"
          );

      var overviewSummaryTableView = undevelopedDiscoveriesOverviewService.getOverviewSummaryTableView();
      assertThat(overviewSummaryTableView)
          .usingRecursiveComparison()
          .isEqualTo(expectedTableView);
    }

    @Test
    void getOverviewSummaryTableView_buildsTable_notLicenced_hasBeenRelinquished() {
      setUpToRowMethodRequirements(Boolean.FALSE, null);
      mockHasBeenRelinquished(SubmissionType.RELINQUISHMENT_DISCOVERY);
      mockExtantLicences(Boolean.TRUE, licenceId);

      var expectedDiscoveryValue = new SummaryTableValue(
          resourceName,
          ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId())),
          List.of()
      );

      var expectedTableView = SummaryTableView.newWithHeading(
              UndevelopedDiscoveriesOverviewService.DISCOVERY_HEADING,
              UndevelopedDiscoveriesOverviewService.YEAR_ADDED_HEADING,
              UndevelopedDiscoveriesOverviewService.LICENSED_HEADING,
              UndevelopedDiscoveriesOverviewService.AREA_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_1C_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_2C_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_3C_HEADING,
              UndevelopedDiscoveriesOverviewService.PERCENTAGE_GAS_HEADING,
              UndevelopedDiscoveriesOverviewService.COMPLEXITY_HEADING)
          .addRow(
              expectedDiscoveryValue,
              "2002",
              "No",
              area,
              "1",
              "1",
              "1",
              "1.00%",
              "1"
          );

      var overviewSummaryTableView = undevelopedDiscoveriesOverviewService.getOverviewSummaryTableView();
      assertThat(overviewSummaryTableView)
          .usingRecursiveComparison()
          .isEqualTo(expectedTableView);
    }

    @Test
    void getOverviewSummaryTableView_buildsTable_notLicenced_noExtantLicences() {
      setUpToRowMethodRequirements(Boolean.FALSE, null);
      mockHasBeenRelinquished(SubmissionType.CREATE_DISCOVERY);
      mockExtantLicences(Boolean.FALSE, licenceId);

      var expectedDiscoveryValue = new SummaryTableValue(
          resourceName,
          ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId())),
          List.of()
      );

      var expectedTableView = SummaryTableView.newWithHeading(
              UndevelopedDiscoveriesOverviewService.DISCOVERY_HEADING,
              UndevelopedDiscoveriesOverviewService.YEAR_ADDED_HEADING,
              UndevelopedDiscoveriesOverviewService.LICENSED_HEADING,
              UndevelopedDiscoveriesOverviewService.AREA_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_1C_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_2C_HEADING,
              UndevelopedDiscoveriesOverviewService.OIL_EQUIVALENT_3C_HEADING,
              UndevelopedDiscoveriesOverviewService.PERCENTAGE_GAS_HEADING,
              UndevelopedDiscoveriesOverviewService.COMPLEXITY_HEADING)
          .addRow(
              expectedDiscoveryValue,
              "2002",
              "No",
              area,
              "1",
              "1",
              "1",
              "1.00%",
              "1"
          );

      var overviewSummaryTableView = undevelopedDiscoveriesOverviewService.getOverviewSummaryTableView();
      assertThat(overviewSummaryTableView)
          .usingRecursiveComparison()
          .isEqualTo(expectedTableView);
    }

    private void setUpUndevelopedDiscoveriesAndInformation() {
      resource = new Resource();
      resource.setId(UUID.randomUUID());

      var undevelopedDiscovery = new UndevelopedDiscovery();
      undevelopedDiscovery.setResource(resource);

      var information = new UndevelopedDiscoveryInformation();
      information.setUndevelopedDiscovery(undevelopedDiscovery);
      information.setInformationDate(LocalDate.of(2002, 1, 11));
      information.setOilEquivalentRecoverableOneC(BigDecimal.ONE);
      information.setOilEquivalentRecoverableTwoC(BigDecimal.ONE);
      information.setOilEquivalentRecoverableThreeC(BigDecimal.ONE);
      information.setPercentageGas(BigDecimal.ONE);
      information.setCalculatedComplexity(BigDecimal.ONE);

      when(undevelopedDiscoveryService.getAllTrackedUndevelopedDiscoveries())
          .thenReturn(List.of(undevelopedDiscovery));
      when(informationService.getLatestInformation(undevelopedDiscovery))
          .thenReturn(Optional.of(information));
    }

    private void setUpToRowMethodRequirements(Boolean isDuplicate,
                                              FieldStatusHistory fieldStatusHistory) {
      var resourceRecord = new ResourceRecordSummaryView(
          null,
          resourceName,
          null,
          null,
          List.of(licenceId),
          List.of(),
          null,
          null,
          null
      );

      var regulatorView = new RegulatorManagedInformationSummaryView(
          null,
          area,
          false,
          Map.of()
      );


      var field = new Field();

      var resourceLink = new ResourceLink();
      resourceLink.setFieldResource(resource);

      Optional<FieldStatusHistory> fieldStatusHistoryOptional = fieldStatusHistory == null
          ? Optional.empty()
          : Optional.of(fieldStatusHistory);

      when(resourceRecordService.getResourceRecordOrThrow(resource.getId()))
          .thenReturn(resourceRecord);
      when(regulatorSummaryService.getRegulatorManagedInformationSummaryFromResource(resource))
          .thenReturn(regulatorView);
      when(resourceGroupService.isInGroupAndIsNotPreferredView(resource))
          .thenReturn(isDuplicate);
      when(resourceLinkService.getResourceLinksByDiscovery(resource))
          .thenReturn(List.of(resourceLink, resourceLink));
      when(fieldPersistenceService.getFieldByResource(resource))
          .thenReturn(Optional.of(field));
      when(fieldStatusHistoryService.getMostRecentFieldStatusHistory(field))
          .thenReturn(fieldStatusHistoryOptional);
    }

    private void mockHasBeenRelinquished(SubmissionType submissionType) {
      var latestApprovedDetail = new SubmissionDetail();
      latestApprovedDetail.setSubmissionType(submissionType);
      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
          .thenReturn(Optional.of(latestApprovedDetail));
    }

    private void mockExtantLicences(Boolean returnLicenceView, Integer licenceId) {
      if (Boolean.TRUE.equals(returnLicenceView)) {
        var licenceView = new LicenceView(null, null, LicenceStatus.EXTANT);
        when(licenceQueryService.getLicenceById(licenceId))
            .thenReturn(Optional.of(licenceView));
      } else {
        when(licenceQueryService.getLicenceById(licenceId))
            .thenReturn(Optional.empty());
      }
    }
  }
}
