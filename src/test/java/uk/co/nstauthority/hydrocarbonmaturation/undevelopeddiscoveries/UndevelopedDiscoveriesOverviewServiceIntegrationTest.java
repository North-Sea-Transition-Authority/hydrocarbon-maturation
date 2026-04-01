package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformation;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation.UndevelopedDiscoveryInformationStatus;

@Transactional
@IntegrationTest
class UndevelopedDiscoveriesOverviewServiceIntegrationTest {

  @Autowired
  private UndevelopedDiscoveriesOverviewService overviewService;

  @Autowired
  private EntityManager entityManager;

  @MockitoBean
  private LicenceQueryService licenceQueryService;

  @BeforeEach
  void setUpRequestContext() {
    var mockRequest = new MockHttpServletRequest();
    mockRequest.setContextPath("/remi");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
  }

  @AfterEach
  void clearRequestContext() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void getOverviewData_withDiscoveryNotTracked_notIncludedInResults() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    persistUndevelopedDiscovery(resource, Boolean.FALSE);

    var result = overviewService.getOverviewData();

    assertThat(result.overviewStats().undevelopedDiscoveries()).isZero();
  }

  @Test
  void getOverviewData_withTrackedDiscoveryAndExtantLicence_discoveryIsLicensed() {
    var licenceId = 100;

    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    var submissionDetail = SubmissionIntegrationTestUtils.createSubmissionDetail(
        submission, SubmissionType.CREATE_DISCOVERY, entityManager);
    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Alpha Discovery", 1, entityManager);
    var generalDetail = SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager);
    SubmissionIntegrationTestUtils.createLicence(licenceId, "P100", generalDetail, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        resource, ResponsibleTeamType.EXPLORATION, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);

    var undevelopedDiscovery = persistUndevelopedDiscovery(resource, Boolean.TRUE);
    persistDiscoveryInformation(undevelopedDiscovery, LocalDate.of(2022, 1, 1));

    when(licenceQueryService.getExtantLicencesByIds(List.of(String.valueOf(licenceId))))
        .thenReturn(List.of(new LicenceDto("P100", licenceId, LicenceStatus.EXTANT)));

    var result = overviewService.getOverviewData();

    assertThat(result.overviewStats().undevelopedDiscoveries()).isEqualTo(1);
    assertThat(result.overviewStats().licensedUndevelopedDiscoveries()).isEqualTo(1);
    assertThat(result.summaryTableView().tableRows()).hasSize(2); // 1 header + 1 data row
  }

  @Test
  void getOverviewData_withTrackedDiscoveryAndNoExtantLicence_discoveryIsUnlicensed() {
    var licenceId = 200;

    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    var submissionDetail = SubmissionIntegrationTestUtils.createSubmissionDetail(
        submission, SubmissionType.CREATE_DISCOVERY, entityManager);
    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Beta Discovery", 1, entityManager);
    var generalDetail = SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager);
    SubmissionIntegrationTestUtils.createLicence(licenceId, "P200", generalDetail, entityManager);

    var undevelopedDiscovery = persistUndevelopedDiscovery(resource, Boolean.TRUE);
    persistDiscoveryInformation(undevelopedDiscovery, LocalDate.of(2021, 6, 15));

    when(licenceQueryService.getExtantLicencesByIds(List.of(String.valueOf(licenceId))))
        .thenReturn(List.of());

    var result = overviewService.getOverviewData();

    assertThat(result.overviewStats().undevelopedDiscoveries()).isEqualTo(1);
    assertThat(result.overviewStats().licensedUndevelopedDiscoveries()).isZero();
    assertThat(result.summaryTableView().tableRows()).hasSize(2); // 1 header + 1 data row
  }

  @Test
  void getOverviewData_withTwoDiscoveriesInSameResourceGroup_bothTaggedAsDuplicate() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource1 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource1, "Gamma Discovery");
    var discovery1 = persistUndevelopedDiscovery(resource1, Boolean.TRUE);
    persistDiscoveryInformation(discovery1, LocalDate.of(2020, 1, 1));

    var resource2 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource2, "Delta Discovery");
    var discovery2 = persistUndevelopedDiscovery(resource2, Boolean.TRUE);
    persistDiscoveryInformation(discovery2, LocalDate.of(2020, 1, 1));

    var resourceGroup = new ResourceGroup();
    entityManager.persist(resourceGroup);
    entityManager.flush();

    var link1 = new ResourceGroupLink();
    link1.setResource(resource1);
    link1.setResourceGroup(resourceGroup);
    entityManager.persist(link1);

    var link2 = new ResourceGroupLink();
    link2.setResource(resource2);
    link2.setResourceGroup(resourceGroup);
    entityManager.persist(link2);
    entityManager.flush();

    var result = overviewService.getOverviewData();

    assertThat(result.overviewStats().undevelopedDiscoveries()).isEqualTo(2);

    var discoveryColumnIndex = UndevelopedDiscoveryOverviewHeadings.DISCOVERY.getColumnIndex();
    var rows = result.summaryTableView().tableRows();
    assertThat(rows).hasSize(3); // 1 header + 2 data rows
    var dataRows = rows.subList(1, rows.size());
    dataRows.forEach(row ->
        assertThat(row.rowValues().get(discoveryColumnIndex).tags())
            .anyMatch(tag -> "DUPLICATE".equals(tag.tagText()))
    );
  }

  @Test
  void getOverviewData_withMultipleTrackedDiscoveries_mmboeStatsAreAggregated() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource1 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource1, "Epsilon Discovery");
    var discovery1 = persistUndevelopedDiscovery(resource1, Boolean.TRUE);
    persistDiscoveryInformation(
        discovery1, LocalDate.of(2022, 1, 1),
        new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00"));

    var resource2 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource2, "Zeta Discovery");
    var discovery2 = persistUndevelopedDiscovery(resource2, Boolean.TRUE);
    persistDiscoveryInformation(
        discovery2, LocalDate.of(2021, 1, 1),
        new BigDecimal("5.00"), new BigDecimal("15.00"), new BigDecimal("25.00"));

    var result = overviewService.getOverviewData();

    assertThat(result.overviewStats().undevelopedDiscoveries()).isEqualTo(2);
    assertThat(result.overviewStats().mmboeOneC()).isEqualTo("15.00");
    assertThat(result.overviewStats().mmboeTwoC()).isEqualTo("35.00");
    assertThat(result.overviewStats().mmboeThreeC()).isEqualTo("55.00");
  }

  @Test
  void getOverviewData_withLinkedFieldWithStatus_discoveryRowContainsFieldStatusTag() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var discoveryResource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(discoveryResource, "Theta Discovery");
    var undevelopedDiscovery = persistUndevelopedDiscovery(discoveryResource, Boolean.TRUE);
    persistDiscoveryInformation(undevelopedDiscovery, LocalDate.of(2023, 3, 1));

    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Linked Field", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_300, entityManager);
    SubmissionIntegrationTestUtils.createFieldResourceLink(discoveryResource, fieldResource, entityManager);

    var result = overviewService.getOverviewData();

    var discoveryColumnIndex = UndevelopedDiscoveryOverviewHeadings.DISCOVERY.getColumnIndex();
    var dataRow = result.summaryTableView().tableRows().get(1);
    assertThat(dataRow.rowValues().get(discoveryColumnIndex).tags())
        .anyMatch(tag -> tag.tagText().equals(FieldStatus.STATUS_300.getDisplayName().toUpperCase()));
  }

  @Test
  void getOverviewData_withNoTrackedDiscoveries_returnsEmptyStats() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var result = overviewService.getOverviewData();

    assertThat(result.overviewStats().undevelopedDiscoveries()).isZero();
    assertThat(result.overviewStats().licensedUndevelopedDiscoveries()).isZero();
    assertThat(result.summaryTableView().tableRows()).hasSize(1); // header only
  }

  @Test
  void getOverviewData_withArchivedResource_archivedTagApplied() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource = SubmissionIntegrationTestUtils.createArchivedResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource, "Archived Discovery");
    var undevelopedDiscovery = persistUndevelopedDiscovery(resource, Boolean.TRUE);
    persistDiscoveryInformation(undevelopedDiscovery, LocalDate.of(2021, 1, 1));

    var result = overviewService.getOverviewData();

    var discoveryColumnIndex = UndevelopedDiscoveryOverviewHeadings.DISCOVERY.getColumnIndex();
    var dataRow = result.summaryTableView().tableRows().get(1);
    assertThat(dataRow.rowValues().get(discoveryColumnIndex).tags())
        .anyMatch(tag -> "ARCHIVED".equals(tag.tagText()));
  }

  @Test
  void getOverviewData_withRelinquishmentDiscoveryAndExtantLicence_notCountedAsLicensed() {
    var licenceId = 300;

    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    var submissionDetail = SubmissionIntegrationTestUtils.createSubmissionDetail(
        submission, SubmissionType.RELINQUISHMENT_DISCOVERY, entityManager);
    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, "Relinquished Discovery", 1, entityManager);
    var generalDetail = SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager);
    SubmissionIntegrationTestUtils.createLicence(licenceId, "P300", generalDetail, entityManager);

    var undevelopedDiscovery = persistUndevelopedDiscovery(resource, Boolean.TRUE);
    persistDiscoveryInformation(undevelopedDiscovery, LocalDate.of(2020, 6, 1));

    when(licenceQueryService.getExtantLicencesByIds(List.of(String.valueOf(licenceId))))
        .thenReturn(List.of(new LicenceDto("P300", licenceId, LicenceStatus.EXTANT)));

    var result = overviewService.getOverviewData();

    assertThat(result.overviewStats().undevelopedDiscoveries()).isEqualTo(1);
    assertThat(result.overviewStats().licensedUndevelopedDiscoveries()).isZero();
  }

  @Test
  void getOverviewData_withNullInformationDate_yearColumnIsNull() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource, "Undated Discovery");
    var undevelopedDiscovery = persistUndevelopedDiscovery(resource, Boolean.TRUE);
    persistDiscoveryInformation(undevelopedDiscovery, null);

    var result = overviewService.getOverviewData();

    var yearColumnIndex = UndevelopedDiscoveryOverviewHeadings.YEAR_ADDED.getColumnIndex();
    var dataRow = result.summaryTableView().tableRows().get(1);
    assertThat(dataRow.rowValues().get(yearColumnIndex).value()).isNull();
  }

  @Test
  void getOverviewData_withNullInformationDate_rowSortedToEnd() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource1 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource1, "Dated Discovery");
    var discovery1 = persistUndevelopedDiscovery(resource1, Boolean.TRUE);
    persistDiscoveryInformation(discovery1, LocalDate.of(2021, 1, 1));

    var resource2 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource2, "Undated Discovery");
    var discovery2 = persistUndevelopedDiscovery(resource2, Boolean.TRUE);
    persistDiscoveryInformation(discovery2, null);

    var result = overviewService.getOverviewData();

    var discoveryColumnIndex = UndevelopedDiscoveryOverviewHeadings.DISCOVERY.getColumnIndex();
    var dataRows = result.summaryTableView().tableRows().subList(1, result.summaryTableView().tableRows().size());
    assertThat(dataRows)
        .extracting(row -> row.rowValues().get(discoveryColumnIndex).value())
        .containsExactly("Dated Discovery", "Undated Discovery");
  }

  @Test
  void getOverviewData_withMultipleDiscoveries_rowsSortedByYearDescendingThenNameAscending() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource1 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource1, "Beta Discovery");
    var discovery1 = persistUndevelopedDiscovery(resource1, Boolean.TRUE);
    persistDiscoveryInformation(discovery1, LocalDate.of(2020, 1, 1));

    var resource2 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource2, "Alpha Discovery");
    var discovery2 = persistUndevelopedDiscovery(resource2, Boolean.TRUE);
    persistDiscoveryInformation(discovery2, LocalDate.of(2020, 1, 1));

    var resource3 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource3, "Gamma Discovery");
    var discovery3 = persistUndevelopedDiscovery(resource3, Boolean.TRUE);
    persistDiscoveryInformation(discovery3, LocalDate.of(2022, 1, 1));

    var result = overviewService.getOverviewData();

    var discoveryColumnIndex = UndevelopedDiscoveryOverviewHeadings.DISCOVERY.getColumnIndex();
    var dataRows = result.summaryTableView().tableRows().subList(1, result.summaryTableView().tableRows().size());
    assertThat(dataRows)
        .extracting(row -> row.rowValues().get(discoveryColumnIndex).value())
        .containsExactly("Gamma Discovery", "Alpha Discovery", "Beta Discovery");
  }

  @Test
  void getOverviewData_withMultipleLinkedFields_fieldStatusTagContainsBothFieldStatuses() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var discoveryResource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(discoveryResource, "Multi-field Discovery");
    var undevelopedDiscovery = persistUndevelopedDiscovery(discoveryResource, Boolean.TRUE);
    persistDiscoveryInformation(undevelopedDiscovery, LocalDate.of(2022, 1, 1));

    var fieldResource1 = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field1 = FieldIntegrationTestUtils.createField("Field One", fieldResource1, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field1, FieldStatus.STATUS_300, entityManager);
    SubmissionIntegrationTestUtils.createFieldResourceLink(discoveryResource, fieldResource1, entityManager);

    var fieldResource2 = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field2 = FieldIntegrationTestUtils.createField("Field Two", fieldResource2, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field2, FieldStatus.STATUS_500, entityManager);
    SubmissionIntegrationTestUtils.createFieldResourceLink(discoveryResource, fieldResource2, entityManager);

    var result = overviewService.getOverviewData();

    var discoveryColumnIndex = UndevelopedDiscoveryOverviewHeadings.DISCOVERY.getColumnIndex();
    var dataRow = result.summaryTableView().tableRows().get(1);
    assertThat(dataRow.rowValues().get(discoveryColumnIndex).tags())
        .anyMatch(tag -> tag.tagText().contains(FieldStatus.STATUS_300.getDisplayName().toUpperCase())
            && tag.tagText().contains(FieldStatus.STATUS_500.getDisplayName().toUpperCase()));
  }

  @Test
  void getOverviewData_withSingleDiscoveryInResourceGroup_notTaggedAsDuplicate() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource, "Solo Discovery");
    var undevelopedDiscovery = persistUndevelopedDiscovery(resource, Boolean.TRUE);
    persistDiscoveryInformation(undevelopedDiscovery, LocalDate.of(2021, 1, 1));

    var resourceGroup = new ResourceGroup();
    entityManager.persist(resourceGroup);
    entityManager.flush();

    var link = new ResourceGroupLink();
    link.setResource(resource);
    link.setResourceGroup(resourceGroup);
    entityManager.persist(link);
    entityManager.flush();

    var result = overviewService.getOverviewData();

    var discoveryColumnIndex = UndevelopedDiscoveryOverviewHeadings.DISCOVERY.getColumnIndex();
    var dataRow = result.summaryTableView().tableRows().get(1);
    assertThat(dataRow.rowValues().get(discoveryColumnIndex).tags())
        .noneMatch(tag -> "DUPLICATE".equals(tag.tagText()));
  }

  @Test
  void getOverviewData_withGeographicalArea_geographicalAreaColumnPopulated() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource, "Area Discovery");
    var undevelopedDiscovery = persistUndevelopedDiscovery(resource, Boolean.TRUE);
    persistDiscoveryInformation(undevelopedDiscovery, LocalDate.of(2021, 1, 1));
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        resource, ResponsibleTeamType.EXPLORATION, GeographicalArea.CENTRAL_NORTH_SEA, entityManager);

    var result = overviewService.getOverviewData();

    var geographicalAreaColumnIndex = UndevelopedDiscoveryOverviewHeadings.GEOGRAPHICAL_AREA.getColumnIndex();
    var dataRow = result.summaryTableView().tableRows().get(1);
    assertThat(dataRow.rowValues().get(geographicalAreaColumnIndex).value())
        .isEqualTo(GeographicalArea.CENTRAL_NORTH_SEA.getDisplayName());
  }

  @Test
  void getOverviewData_withPercentageGas_percentageGasColumnFormatted() {
    when(licenceQueryService.getExtantLicencesByIds(any())).thenReturn(List.of());

    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    createMinimalSubmission(resource, "Gas Discovery");
    var undevelopedDiscovery = persistUndevelopedDiscovery(resource, Boolean.TRUE);

    var information = new UndevelopedDiscoveryInformation();
    information.setUndevelopedDiscovery(undevelopedDiscovery);
    information.setInformationDate(LocalDate.of(2021, 1, 1));
    information.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);
    information.setPercentageGas(new BigDecimal("75.50"));
    entityManager.persist(information);
    entityManager.flush();

    var result = overviewService.getOverviewData();

    var percentageGasColumnIndex = UndevelopedDiscoveryOverviewHeadings.PERCENTAGE_GAS.getColumnIndex();
    var dataRow = result.summaryTableView().tableRows().get(1);
    assertThat(dataRow.rowValues().get(percentageGasColumnIndex).value()).isEqualTo("75.50%");
  }

  private UndevelopedDiscovery persistUndevelopedDiscovery(Resource resource, Boolean isUndevelopedDiscovery) {
    var undevelopedDiscovery = new UndevelopedDiscovery();
    undevelopedDiscovery.setIsUndevelopedDiscovery(isUndevelopedDiscovery);
    undevelopedDiscovery.setResource(resource);
    entityManager.persist(undevelopedDiscovery);
    entityManager.flush();
    return undevelopedDiscovery;
  }

  private void persistDiscoveryInformation(
      UndevelopedDiscovery undevelopedDiscovery, LocalDate informationDate) {
    persistDiscoveryInformation(undevelopedDiscovery, informationDate, null, null, null);
  }

  private void persistDiscoveryInformation(
      UndevelopedDiscovery undevelopedDiscovery, LocalDate informationDate,
      BigDecimal mmboeOneC, BigDecimal mmboeTwoC, BigDecimal mmboeThreeC) {
    var information = new UndevelopedDiscoveryInformation();
    information.setUndevelopedDiscovery(undevelopedDiscovery);
    information.setInformationDate(informationDate);
    information.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);
    information.setOilEquivalentRecoverableOneC(mmboeOneC);
    information.setOilEquivalentRecoverableTwoC(mmboeTwoC);
    information.setOilEquivalentRecoverableThreeC(mmboeThreeC);
    entityManager.persist(information);
    entityManager.flush();
  }

  private void createMinimalSubmission(Resource resource, String resourceName) {
    var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    var submissionDetail = SubmissionIntegrationTestUtils.createSubmissionDetail(
        submission, SubmissionType.CREATE_DISCOVERY, entityManager);
    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, resourceName, 1, entityManager);
    SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager);
  }
}