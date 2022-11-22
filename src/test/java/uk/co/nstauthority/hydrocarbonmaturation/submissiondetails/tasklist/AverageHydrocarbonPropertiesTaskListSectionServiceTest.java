package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertyController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertyController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySubmissionService;

@ExtendWith(MockitoExtension.class)
class AverageHydrocarbonPropertiesTaskListSectionServiceTest {

  @Mock
  private GasFluidPropertySubmissionService gasFluidPropertySubmissionService;

  @Mock
  private GeneralFluidPropertySubmissionService generalFluidPropertySubmissionService;

  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @InjectMocks
  private AverageHydrocarbonPropertiesTaskListSectionService averageHydrocarbonPropertiesTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void getSection_whenDiscoveryWellDetailsNotPresent_assertOneItem() {
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);

    assertThat(taskListSectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder,
            TaskListSection::items)
        .containsExactly(
            "Average hydrocarbon properties",
            60,
            List.of(
                new TaskListItem("General properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(GeneralFluidPropertyController.class)
                        .getGeneralFluidProperties(submissionDetail.getId()))))
        );
  }

  @Test
  void getSection_whenProspectSubmission_assertEmptySection() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);

    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);

    assertThat(taskListSectionOptional).isNotPresent();
  }

  @Test
  void getSection_whenOilOnly_assertOilSections() {
    var discoveryWellDetail = new DiscoveryWellDetail();
    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);

    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetail));
    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);

    assertThat(taskListSectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder,
            TaskListSection::items)
        .containsExactly(
            "Average hydrocarbon properties",
            60,
            List.of(
                new TaskListItem("General properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(GeneralFluidPropertyController.class)
                        .getGeneralFluidProperties(submissionDetail.getId()))))
        );
  }

  @Test
  void getSection_whenGasOnly_assertSectionGasItems() {
    var discoveryWellDetail = new DiscoveryWellDetail();
    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);

    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetail));
    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);

    assertThat(taskListSectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder,
            TaskListSection::items)
        .containsExactly(
            "Average hydrocarbon properties",
            60,
            List.of(new TaskListItem(
                "Gas properties",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(GasFluidPropertyController.class)
                    .getGasFluidProperties(submissionDetail.getId()))),
                new TaskListItem("General properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(GeneralFluidPropertyController.class)
                        .getGeneralFluidProperties(submissionDetail.getId()))))
        );
  }

  @Test
  void getSection_whenMixed_assertSectionGasItems() {
    var discoveryWellDetail = new DiscoveryWellDetail();
    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetail));
    var taskListSectionOptional = averageHydrocarbonPropertiesTaskListSectionService.getSection(submissionDetail);

    assertThat(taskListSectionOptional.get())
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder,
            TaskListSection::items)
        .containsExactly(
            "Average hydrocarbon properties",
            60,
            List.of(new TaskListItem(
                "Gas properties",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(GasFluidPropertyController.class)
                    .getGasFluidProperties(submissionDetail.getId()))),
                new TaskListItem("General properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(GeneralFluidPropertyController.class)
                        .getGeneralFluidProperties(submissionDetail.getId()))))
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT", "POST_DRILL_DRY_HOLE"})
  void isSectionSubmittable_whenNotDiscovery_assertTrue(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var submittable = averageHydrocarbonPropertiesTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertTrue(submittable);
  }

  @Test
  void isSectionSubmittable_whenDiscovery_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(gasFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    var submittable = averageHydrocarbonPropertiesTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertTrue(submittable);
  }
}