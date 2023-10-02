package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySubmissionService;

@ExtendWith(MockitoExtension.class)
class AverageHydrocarbonPropertiesTaskListSectionServiceTest {

  @Mock
  private GasFluidPropertySubmissionService gasFluidPropertySubmissionService;

  @Mock
  private GeneralFluidPropertySubmissionService generalFluidPropertySubmissionService;

  @Mock
  private OilFluidPropertySubmissionService oilFluidPropertySubmissionService;

  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @InjectMocks
  private AverageHydrocarbonPropertiesTaskListSectionService averageHydrocarbonPropertiesTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
  }

//  @Test
//  void getSection_whenDiscoveryWellDetailsNotPresent_assertOneItem() {
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.empty());
//
//    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);
//
//    assertThat(taskListSectionOptional.get())
//        .extracting(
//            TaskListSection::displayName,
//            TaskListSection::displayOrder,
//            TaskListSection::items)
//        .containsExactly(
//            "Average hydrocarbon properties",
//            60,
//            List.of(
//                new TaskListItem("General properties",
//                    TaskListLabel.NOT_COMPLETE,
//                    ReverseRouter.route(on(GeneralFluidPropertyController.class)
//                        .getGeneralFluidProperties(submissionDetail.getId()))))
//        );
//  }

  @Test
  void getSection_whenProspectSubmission_assertEmptySection() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);

    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);

    assertThat(taskListSectionOptional).isNotPresent();
  }

  // TODO - these tests no longer work with moved well test results due to hydrocarbons being per reservoir
//  @Test
//  void getSection_whenOilOnly_assertOilSections() {
//    var discoveryWellDetail = new DiscoveryWellDetail();
//    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
//
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.of(discoveryWellDetail));
//    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);
//
//    assertThat(taskListSectionOptional.get())
//        .extracting(
//            TaskListSection::displayName,
//            TaskListSection::displayOrder,
//            TaskListSection::items)
//        .containsExactly(
//            "Average hydrocarbon properties",
//            60,
//            List.of(
//                new TaskListItem(
//                    "Oil properties",
//                    TaskListLabel.NOT_COMPLETE,
//                    ReverseRouter.route(on(OilFluidPropertyController.class)
//                        .getOilFluidProperties(submissionDetail.getId()))),
//                new TaskListItem("General properties",
//                    TaskListLabel.NOT_COMPLETE,
//                    ReverseRouter.route(on(GeneralFluidPropertyController.class)
//                        .getGeneralFluidProperties(submissionDetail.getId()))))
//        );
//  }
//
//  @Test
//  void getSection_whenGasOnly_assertSectionGasItems() {
//    var discoveryWellDetail = new DiscoveryWellDetail();
//    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
//
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.of(discoveryWellDetail));
//    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);
//
//    assertThat(taskListSectionOptional.get())
//        .extracting(
//            TaskListSection::displayName,
//            TaskListSection::displayOrder,
//            TaskListSection::items)
//        .containsExactly(
//            "Average hydrocarbon properties",
//            60,
//            List.of(
//                new TaskListItem(
//                    "Gas properties",
//                    TaskListLabel.NOT_COMPLETE,
//                    ReverseRouter.route(on(GasFluidPropertyController.class)
//                        .getGasFluidProperties(submissionDetail.getId()))),
//                new TaskListItem("General properties",
//                    TaskListLabel.NOT_COMPLETE,
//                    ReverseRouter.route(on(GeneralFluidPropertyController.class)
//                        .getGeneralFluidProperties(submissionDetail.getId()))))
//        );
//  }
//
//  @Test
//  void getSection_whenMixed_assertSectionGasItems() {
//    var discoveryWellDetail = new DiscoveryWellDetail();
//    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
//
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.of(discoveryWellDetail));
//    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);
//
//    assertThat(taskListSectionOptional.get())
//        .extracting(
//            TaskListSection::displayName,
//            TaskListSection::displayOrder,
//            TaskListSection::items)
//        .containsExactly(
//            "Average hydrocarbon properties",
//            60,
//            List.of(
//                new TaskListItem("Oil properties",
//                    TaskListLabel.NOT_COMPLETE,
//                    ReverseRouter.route(on(OilFluidPropertyController.class)
//                        .getOilFluidProperties(submissionDetail.getId()))),
//                new TaskListItem(
//                    "Gas properties",
//                    TaskListLabel.NOT_COMPLETE,
//                    ReverseRouter.route(on(GasFluidPropertyController.class)
//                        .getGasFluidProperties(submissionDetail.getId()))),
//                new TaskListItem("General properties",
//                    TaskListLabel.NOT_COMPLETE,
//                    ReverseRouter.route(on(GeneralFluidPropertyController.class)
//                        .getGeneralFluidProperties(submissionDetail.getId()))))
//        );
//  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT", "POST_DRILL_DRY_HOLE"})
  void isSectionSubmittable_whenNotDiscovery_assertTrue(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var submittable = averageHydrocarbonPropertiesTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertTrue(submittable);
  }
  // TODO - these tests no longer work with moved well test results due to hydrocarbons being per reservoir
//  @Test
//  void isSectionSubmittable_whenDiscovery_andAllItemsComplete() {
//    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
//    var discoveryWellDetail = new DiscoveryWellDetail();
//    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
//
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.of(discoveryWellDetail));
//
//    when(gasFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
//    when(oilFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
//    when(generalFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
//    var submittable = averageHydrocarbonPropertiesTaskListSectionService.isSectionSubmittable(submissionDetail);
//
//    assertTrue(submittable);
//  }
//
//  @Test
//  void isSectionSubmittable_whenDiscovery_andGasNotComplete() {
//    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
//    var discoveryWellDetail = new DiscoveryWellDetail();
//    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
//
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.of(discoveryWellDetail));
//
//    when(oilFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
//    when(gasFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);
//    var submittable = averageHydrocarbonPropertiesTaskListSectionService.isSectionSubmittable(submissionDetail);
//
//    assertFalse(submittable);
//  }
//
//  @Test
//  void isSectionSubmittable_whenDiscovery_andOilNotComplete() {
//    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
//    var discoveryWellDetail = new DiscoveryWellDetail();
//    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
//
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.of(discoveryWellDetail));
//
//    when(oilFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);
//    var submittable = averageHydrocarbonPropertiesTaskListSectionService.isSectionSubmittable(submissionDetail);
//
//    assertFalse(submittable);
//  }
//
//  @Test
//  void isSectionSubmittable_whenDiscovery_andGeneralPropertiesNotComplete() {
//    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
//    var discoveryWellDetail = new DiscoveryWellDetail();
//    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
//
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.of(discoveryWellDetail));
//
//    when(gasFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
//    when(oilFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
//    when(generalFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);
//    var submittable = averageHydrocarbonPropertiesTaskListSectionService.isSectionSubmittable(submissionDetail);
//
//    assertFalse(submittable);
//  }
//
//  @Test
//  void isSectionSubmittable_whenDiscovery_andOilOnly_andOilNotComplete() {
//    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
//    var discoveryWellDetail = new DiscoveryWellDetail();
//    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
//
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.of(discoveryWellDetail));
//
//    when(oilFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);
//    var submittable = averageHydrocarbonPropertiesTaskListSectionService.isSectionSubmittable(submissionDetail);
//
//    assertFalse(submittable);
//  }
//
//  @Test
//  void isSectionSubmittable_whenDiscovery_andGasOnly_andGasNotComplete() {
//    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
//    var discoveryWellDetail = new DiscoveryWellDetail();
//    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
//
//    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
//        .thenReturn(Optional.of(discoveryWellDetail));
//
//    when(gasFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);
//    var submittable = averageHydrocarbonPropertiesTaskListSectionService.isSectionSubmittable(submissionDetail);
//
//    assertFalse(submittable);
//  }
}