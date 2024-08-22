package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.ReservoirAdditionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirAdditionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirAdditionsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirAdditionsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ReservoirTaskListSectionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @Mock
  private PermeabilitySubmissionService permeabilitySubmissionService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private ReservoirAdditionsFormValidator reservoirAdditionsFormValidator;

  @Mock
  private ReservoirAdditionsFormService reservoirAdditionsFormService;

  @InjectMocks
  private ReservoirTaskListSectionService reservoirTaskListSectionService;

  @Test
  void getSection_forProspect() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    var taskListSectionOptional = reservoirTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(taskListSectionOptional).isPresent();
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder
        )
        .containsExactly(
            List.of(
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class)
                        .getReservoirProperties(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            ),
            "Reservoirs",
            40
        );
  }

  @Test
  void getSection_forDryWell() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    var taskListSectionOptional = reservoirTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(taskListSectionOptional).isPresent();
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder
        )
        .containsExactly(
            List.of(
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class)
                        .getReservoirProperties(submissionDetail.getId(), reservoirSubmissionDetail.getId()))),
                new TaskListItem("Permeability",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class)
                        .getPermeabilityPage(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            ),
            "Reservoirs",
            40
        );
  }

  @Test
  void getSection_forDiscovery_withReservoirsAdded() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)).thenReturn(true);

    var form = new ReservoirAdditionForm(ReservoirAdditionStatus.YES_LATER);
    when(reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail))
        .thenReturn(form);

    var taskListSectionOptional = reservoirTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(taskListSectionOptional).isPresent();
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder
        )
        .containsExactly(
            List.of(
                new TaskListItem("Discovery reservoirs",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(
                        submissionDetail.getId(), null)))
            ),
            "Reservoirs",
            40
        );
  }

  @Test
  void getSection_forDiscovery_withoutReservoirsAdded() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)).thenReturn(false);

    var form = new ReservoirAdditionForm(ReservoirAdditionStatus.NO);
    when(reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail))
        .thenReturn(form);

    when(reservoirAdditionsFormValidator.isValid(
        eq(form),
        any(BindingResult.class),
        eq(submissionDetail),
        eq(ValidationType.FULL)))
        .thenReturn(false);

    var taskListSectionOptional = reservoirTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(taskListSectionOptional).isPresent();
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder
        )
        .containsExactly(
            List.of(
                new TaskListItem("Discovery reservoirs",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetail.getId(), null)))
            ),
            "Reservoirs",
            40
        );
  }

  @Test
  void getSection_forField_withReservoirsAdded() {
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_CSR);
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)).thenReturn(true);

    var form = new ReservoirAdditionForm(ReservoirAdditionStatus.YES_LATER);
    when(reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail))
        .thenReturn(form);

    var taskListSectionOptional = reservoirTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(taskListSectionOptional).isPresent();
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder
        )
        .containsExactly(
            List.of(
                new TaskListItem("Field reservoirs",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(
                        submissionDetail.getId(), null)))
            ),
            "Reservoirs",
            40
        );
  }

  @Test
  void getSection_forField_withoutReservoirsAdded() {
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_CSR);
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)).thenReturn(false);

    var form = new ReservoirAdditionForm(ReservoirAdditionStatus.YES_LATER);
    when(reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail))
        .thenReturn(form);

    var taskListSectionOptional = reservoirTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(taskListSectionOptional).isPresent();
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder
        )
        .containsExactly(
            List.of(
                new TaskListItem("Field reservoirs",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetail.getId(), null)))
            ),
            "Reservoirs",
            40
        );
  }

  @Test
  void isSectionSubmittable_whenSelectNoMoreToAddAndChangeAnswersToInvalid_notComplete() {
    var submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_CSR);
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)).thenReturn(true);

    var form = new ReservoirAdditionForm(ReservoirAdditionStatus.NO);
    when(reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail))
        .thenReturn(form);

    when(reservoirAdditionsFormValidator.isValid(
        eq(form),
        any(BindingResult.class),
        eq(submissionDetail),
        eq(ValidationType.FULL)))
        .thenReturn(false);

    var taskListSectionOptional = reservoirTaskListSectionService.getSection(submissionDetail, USER);

    assertThat(taskListSectionOptional).isPresent();
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder
        )
        .containsExactly(
            List.of(
                new TaskListItem("Field reservoirs",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetail.getId(), null)))
            ),
            "Reservoirs",
            40
        );
  }

  @Test
  void isSectionSubmittable_whenDiscoverySubmittable_assertTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail, USER);
    assertTrue(submittable);
  }

  @Test
  void isSectionSubmittable_whenPostDrillDryWellItemsSubmittable_assertTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail, USER);

    assertTrue(submittable);
  }

  @Test
  void isSectionSubmittable_whenPostDrillDryWellItemNotSubmittable_assertFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail, USER);

    assertFalse(submittable);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT"})
  void isSectionSubmittable_whenProspectItemNotSubmittable_assertFalse(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmissionType(submissionType);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail, USER);

    assertFalse(submittable);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT"})
  void getSection_whenProspectItemsSubmittable_assertTrue(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmissionType(submissionType);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail, USER);

    assertTrue(submittable);
  }
}