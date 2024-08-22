package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedVolTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes.FdpaIncreasedVolumesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes.FdpaIncreasedVolumesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes.FdpaPromotedVolumesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes.FdpaVolumesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals.FieldCombinedCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals.FieldCombinedCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics.FieldGasCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics.FieldGasCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class TotalVolumesTaskListSectionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  @Mock
  private DiscoveryOilCaseVolumetricTotalsSubmissionService discoveryOilCaseVolumetricTotalsSubmissionService;
  @Mock
  private DiscoveryGasCaseVolumetricTotalsSubmissionService discoveryGasCaseVolumetricTotalsSubmissionService;
  @Mock
  private DiscoveryCombinedVolTotalsSubmissionService discoveryCombinedVolTotalsSubmissionService;
  @Mock
  private FieldOilCaseVolumetricTotalsSubmissionService fieldOilCaseVolumetricTotalsSubmissionService;
  @Mock
  private FieldGasCaseVolumetricTotalsSubmissionService fieldGasCaseVolumetricTotalsSubmissionService;
  @Mock
  private FieldCombinedCaseVolumetricTotalsSubmissionService fieldCombinedCaseVolumetricTotalsSubmissionService;
  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;
  @Mock
  private FdpaPromotedVolumesSubmissionService fdpaPromotedVolumesSubmissionService;
  @Mock
  private FdpaIncreasedVolumesSubmissionService fdpaIncreasedVolumesSubmissionService;
  @InjectMocks
  private TotalVolumesTaskListSectionService totalVolumesTaskListSectionService;

  private ReservoirSubmissionDetail rsd1;
  private ReservoirSubmissionDetail rsd2;

  @BeforeEach
  void setUp() {
    rsd1 = new ReservoirSubmissionDetail();
    rsd2 = new ReservoirSubmissionDetail();
  }

  @Test
  void sectionIsPresent_canGetSection_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.UPDATE_DISCOVERY);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);

    assertTrue(totalVolumesTaskListSectionService.sectionIsPresent(submissionDetail, USER));
  }

  @Test
  void sectionIsPresent_canNotGetSection_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.PROSPECT_UPDATE);

    assertFalse(totalVolumesTaskListSectionService.sectionIsPresent(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allProspectSubmissionTypes")
  void getSection_isProspect_thenReturnEmpty(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    assertThat(totalVolumesTaskListSectionService.getSection(submissionDetail, USER))
        .isEmpty();
  }

  @Test
  void getSection_isProspect_thenReturnEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.POST_DRILL_DRY_HOLE);

    assertThat(totalVolumesTaskListSectionService.getSection(submissionDetail, USER))
        .isEmpty();
  }

  @Test
  void getSection_discovery_noReservoirSubmissionDetails_thenReturnEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);

    List<ReservoirSubmissionDetail> reservoirSubmissionDetails = List.of();

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);

    assertThat(totalVolumesTaskListSectionService.getSection(submissionDetail, USER))
        .isEmpty();
  }

  @Test
  void getSection_field_noReservoirSubmissionDetails_thenReturnEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    List<ReservoirSubmissionDetail> reservoirSubmissionDetails = List.of();

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);

    assertThat(totalVolumesTaskListSectionService.getSection(submissionDetail, USER))
        .isEmpty();
  }

  @Test
  void getSection_discovery_oneReservoirSubmissionDetail_thenReturnEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);

    var reservoirSubmissionDetails = List.of(new ReservoirSubmissionDetail());

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);

    assertThat(totalVolumesTaskListSectionService.getSection(submissionDetail, USER))
        .isEmpty();
  }

  @Test
  void getSection_field_oneReservoirSubmissionDetail_thenPresent() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        SubmissionType.CREATE_CSR);

    var reservoirSubmissionDetails = List.of(rsd1);
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)).thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    assertThat(totalVolumesTaskListSectionService.getSection(submissionDetail, USER)).isPresent();
  }

  @Test
  void getSection_noHydrocarbonCase_thenReturnEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);

    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var reservoirSubmissionDetail2 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var reservoirSubmissionDetails = List.of(reservoirSubmissionDetail, reservoirSubmissionDetail2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);

    assertThat(totalVolumesTaskListSectionService.getSection(submissionDetail, USER))
        .isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void getSection_discovery_oilCase_thenAssertSectionProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(sectionOptional).isPresent();
    assertThat(sectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder
        ).containsExactly(
            "Total volumes for %s".formatted(ResourceType.DISCOVERY.getDisplayName().toLowerCase()),
            50
        );
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void getSection_discovery_gasCase_thenAssertSectionProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(sectionOptional).isPresent();
    assertThat(sectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder
        ).containsExactly(
            "Total volumes for %s".formatted(ResourceType.DISCOVERY.getDisplayName().toLowerCase()),
            50
        );
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void getSection_discovery_combinedCase_thenAssertSectionProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(sectionOptional).isPresent();
    assertThat(sectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder
        ).containsExactly(
            "Total volumes for %s".formatted(ResourceType.DISCOVERY.getDisplayName().toLowerCase()),
            50
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSection_field_oilCase_thenAssertSectionProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)).thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(sectionOptional).isPresent();
    assertThat(sectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder
        ).containsExactly(
            "Total volumes for %s".formatted(ResourceType.FIELD.getDisplayName().toLowerCase()),
            50
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSection_field_gasCase_thenAssertSectionProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(sectionOptional).isPresent();
    assertThat(sectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder
        ).containsExactly(
            "Total volumes for %s".formatted(ResourceType.FIELD.getDisplayName().toLowerCase()),
            50
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSection_field_combinedCase_thenAssertSectionProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(fieldCombinedCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(sectionOptional).isPresent();
    assertThat(sectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder
        ).containsExactly(
            "Total volumes for %s".formatted(ResourceType.FIELD.getDisplayName().toLowerCase()),
            50
        );
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void getSection_discovery_oilCase_thenAssertTaskListItems(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);
    assertThat(sectionOptional).isPresent();

    var taskListItems = sectionOptional.get().items();
    assertThat(taskListItems)
        .extracting(
            TaskListItem::displayName,
            TaskListItem::label,
            TaskListItem::actionUrl
        ).containsExactly(
            Tuple.tuple(
                "Total oil case volumetrics",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class)
                    .getDiscoveryOilCaseVolumetricTotals(submissionDetail.getId()))
            )
        );
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void getSection_discovery_gasCase_thenAssertProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);
    assertThat(sectionOptional).isPresent();

    var taskListItems = sectionOptional.get().items();
    assertThat(taskListItems)
        .extracting(
            TaskListItem::displayName,
            TaskListItem::label,
            TaskListItem::actionUrl
        ).containsExactly(
            Tuple.tuple(
                "Total gas case volumetrics",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(DiscoveryGasCaseVolumetricTotalsController.class)
                    .getDiscoveryGasCaseVolumetricTotals(submissionDetail.getId()))
            )
        );
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void getSection_discovery_mixedCase_thenAssertProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);
    assertThat(sectionOptional).isPresent();

    var taskListItems = sectionOptional.get().items();
    assertThat(taskListItems)
        .extracting(
            TaskListItem::displayName,
            TaskListItem::label,
            TaskListItem::actionUrl
        ).containsExactly(
            Tuple.tuple(
                "Total oil and gas case volumetrics",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(DiscoveryCombinedCaseVolTotalsController.class)
                    .getDiscoveryCombinedCaseVolumetricTotals(submissionDetail.getId()))
            )
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypesWithoutFdpa")
  void getSection_field_oilCase_thenAssertTaskListItems(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);
    assertThat(sectionOptional).isPresent();

    var taskListItems = sectionOptional.get().items();
    assertThat(taskListItems)
        .extracting(
            TaskListItem::displayName,
            TaskListItem::label,
            TaskListItem::actionUrl
        ).containsExactly(
            Tuple.tuple(
                "Total oil case volumetrics",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(FieldOilCaseVolumetricTotalsController.class)
                    .getFieldOilCaseVolumetricTotals(submissionDetail.getId()))
            )
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypesWithoutFdpa")
  void getSection_field_gasCase_thenAssertProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);
    assertThat(sectionOptional).isPresent();

    var taskListItems = sectionOptional.get().items();
    assertThat(taskListItems)
        .extracting(
            TaskListItem::displayName,
            TaskListItem::label,
            TaskListItem::actionUrl
        ).containsExactly(
            Tuple.tuple(
                "Total gas case volumetrics",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class)
                    .getFieldGasCaseVolumetricTotals(submissionDetail.getId()))
            )
        );
  }

  @Test
  void getSection_fdpa_thenAssertProperties() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        SubmissionType.CREATE_FDPA);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);
    when(fdpaPromotedVolumesSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);
    when(fdpaIncreasedVolumesSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);
    assertThat(sectionOptional).isPresent();

    var taskListItems = sectionOptional.get().items();
    assertThat(taskListItems)
        .extracting(
            TaskListItem::displayName,
            TaskListItem::label,
            TaskListItem::actionUrl
        ).containsExactly(
            Tuple.tuple(
                "Total gas case volumetrics",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class)
                    .getFieldGasCaseVolumetricTotals(submissionDetail.getId()))
            ),
            Tuple.tuple(
                "FDPA promoted volumes",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(FdpaVolumesController.class)
                    .getFdpaPromotedVolumes(submissionDetail.getId()))
            ),
            Tuple.tuple(
                "FDPA increased volumes",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                    .getFdpaIncreasedVolumes(submissionDetail.getId()))
            )
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypesWithoutFdpa")
  void getSection_field_mixedCase_thenAssertProperties(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(
        submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails))
        .thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(fieldCombinedCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);

    var sectionOptional = totalVolumesTaskListSectionService.getSection(submissionDetail, USER);
    assertThat(sectionOptional).isPresent();

    var taskListItems = sectionOptional.get().items();
    assertThat(taskListItems)
        .extracting(
            TaskListItem::displayName,
            TaskListItem::label,
            TaskListItem::actionUrl
        ).containsExactly(
            Tuple.tuple(
                "Total oil and gas case volumetrics",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(FieldCombinedCaseVolumetricTotalsController.class)
                    .getFieldCombinedCaseVolumetricTotals(submissionDetail.getId()))
            )
        );
  }

  @ParameterizedTest
  @MethodSource("allProspectSubmissionTypes")
  void isSectionSubmittable_prospect_thenTrue(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @Test
  void isSectionSubmittable_postDrillDryHole_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.POST_DRILL_DRY_HOLE);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void isSectionSubmittable_discovery_noReservoirs_thenFalse(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    assertFalse(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void isSectionSubmittable_discovery_oneReservoirs_thenTrue(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = mock(List.class);
    when(reservoirSubmissionDetails.size()).thenReturn(1);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void isSectionSubmittable_discovery_oilCaseIsSubmittable_thenTrue(SubmissionType submissionType) {
    var isOilCaseSectionSubmittable = true;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)).thenReturn(true);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isOilCaseSectionSubmittable);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void isSectionSubmittable_discovery_oilCaseIsNotSubmittable_thenFalse(SubmissionType submissionType) {
    var isOilCaseSectionSubmittable = false;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)).thenReturn(true);
    when(discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isOilCaseSectionSubmittable);
    assertFalse(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void isSectionSubmittable_discovery_gasCaseIsSubmittable_thenTrue(SubmissionType submissionType) {
    var isGasSectionSubmittable = true;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);
    when(discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isGasSectionSubmittable);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void isSectionSubmittable_discovery_gasCaseIsNotSubmittable_thenFalse(SubmissionType submissionType) {
    var isGasSectionSubmittable = false;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);
    when(discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isGasSectionSubmittable);
    assertFalse(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void isSectionSubmittable_discovery_combinedCaseIsSubmittable_thenTrue(SubmissionType submissionType) {
    var isCombinedCaseSectionSubmittable = true;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails))
        .thenReturn(true);
    when(discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isCombinedCaseSectionSubmittable);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allDiscoverySubmissionTypes")
  void isSectionSubmittable_discovery_combinedCaseIsNotSubmittable_thenFalse(SubmissionType submissionType) {
    var isCombinedCaseSectionSubmittable = false;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails))
        .thenReturn(isCombinedCaseSectionSubmittable);
    assertFalse(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypesWithoutFdpa")
  void isSectionSubmittable_field_oilCaseIsSubmittable_thenTrue(SubmissionType submissionType) {
    var isOilCaseSectionSubmittable = true;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)).thenReturn(true);
    when(fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isOilCaseSectionSubmittable);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void isSectionSubmittable_field_oilCaseIsNotSubmittable_thenFalse(SubmissionType submissionType) {
    var isOilCaseSectionSubmittable = false;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)).thenReturn(true);
    when(fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isOilCaseSectionSubmittable);
    assertFalse(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypesWithoutFdpa")
  void isSectionSubmittable_field_gasCaseIsSubmittable_thenTrue(SubmissionType submissionType) {
    var isGasCaseSectionSubmittable = true;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);
    when(fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isGasCaseSectionSubmittable);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @Test
  void isSectionSubmittable_fdpaIsSubmittable_thenTrue() {
    var isSectionSubmittable = true;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDPA);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);
    when(fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isSectionSubmittable);
    when(fdpaPromotedVolumesSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);
    when(fdpaIncreasedVolumesSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(true);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void isSectionSubmittable_field_gasCaseIsNotSubmittable_thenFalse(SubmissionType submissionType) {
    var isGasCaseSectionSubmittable = false;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);
    when(fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isGasCaseSectionSubmittable);
    assertFalse(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypesWithoutFdpa")
  void isSectionSubmittable_field_combinedCaseIsSubmittable_thenTrue(SubmissionType submissionType) {
    var isCombinedCaseSectionSubmittable = true;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails))
        .thenReturn(true);
    when(fieldCombinedCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isCombinedCaseSectionSubmittable);
    assertTrue(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void isSectionSubmittable_field_combinedCaseIsNotSubmittable_thenFalse(SubmissionType submissionType) {
    var isCombinedCaseSectionSubmittable = false;

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var reservoirSubmissionDetails = List.of(rsd1, rsd2);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails))
        .thenReturn(true);
    when(fieldCombinedCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isCombinedCaseSectionSubmittable);
    assertFalse(totalVolumesTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  private static Stream<Arguments> allProspectSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isProspectSubmission)
        .map(Arguments::of);
  }

  private static Stream<Arguments> allDiscoverySubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isDiscoverySubmission)
        .filter(submissionType -> !submissionType.equals(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .map(Arguments::of);
  }

  private static Stream<SubmissionType> allFieldSubmissionTypesBase() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .filter(submissionType -> !submissionType.equals(SubmissionType.CREATE_FIELD));
  }

  private static Stream<Arguments> allFieldSubmissionTypesWithoutFdpa() {
    return allFieldSubmissionTypesBase()
        .filter(submissionType -> !submissionType.equals(SubmissionType.CREATE_FDPA))
        .map(Arguments::of);
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return allFieldSubmissionTypesBase()
        .map(Arguments::of);
  }
}