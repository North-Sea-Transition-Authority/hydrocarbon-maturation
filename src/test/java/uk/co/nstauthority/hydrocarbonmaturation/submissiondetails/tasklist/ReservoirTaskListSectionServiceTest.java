package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

@ExtendWith(MockitoExtension.class)
class ReservoirTaskListSectionServiceTest {

  @Mock
  private ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @Mock
  private PermeabilitySubmissionService permeabilitySubmissionService;

  @InjectMocks
  private ReservoirTaskListSectionService reservoirTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getSection() {
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
                ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetail.getId()))),
            new TaskListItem("Permeability",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(submissionDetail.getId(), null)))),
            "Reservoir",
            40
        );
  }

  @Test
  void isSectionSubmittable_whenPostDrillDryWellItemsSubmittable_assertTrue() {
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertTrue(submittable);
  }

  @Test
  void isSectionSubmittable_whenPostDrillDryWellItemNotSubmittable_assertFalse() {
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertFalse(submittable);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT"})
  void isSectionSubmittable_whenProspectItemNotSubmittable_assertFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertFalse(submittable);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT"})
  void isSectionSubmittable_whenProspectItemsSubmittable_assertTrue(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    var submittable = reservoirTaskListSectionService.isSectionSubmittable(submissionDetail);

    assertTrue(submittable);
  }
}