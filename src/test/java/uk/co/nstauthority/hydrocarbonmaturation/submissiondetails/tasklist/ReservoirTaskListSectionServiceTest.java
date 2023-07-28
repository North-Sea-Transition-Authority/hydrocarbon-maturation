package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservoirTaskListSectionServiceTest {

  @Mock
  private ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @Mock
  private PermeabilitySubmissionService permeabilitySubmissionService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @InjectMocks
  private ReservoirTaskListSectionService reservoirTaskListSectionService;

  private SubmissionDetail submissionDetail;
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getSection() {
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    var taskListSectionOptional = reservoirTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Reservoir properties",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetail.getId(), reservoirSubmissionDetail.getId()))),
            new TaskListItem("Permeability",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null)))),
            "Reservoir",
            40
        );
  }

  @Test
  void isSectionSubmittable_whenPostDrillDryWellItemsSubmittable_assertTrue() {
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertTrue(submittable);
  }

  @Test
  void isSectionSubmittable_whenPostDrillDryWellItemNotSubmittable_assertFalse() {
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertFalse(submittable);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT"})
  void isSectionSubmittable_whenProspectItemNotSubmittable_assertFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertFalse(submittable);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT"})
  void isSectionSubmittable_whenProspectItemsSubmittable_assertTrue(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertTrue(submittable);
  }
}