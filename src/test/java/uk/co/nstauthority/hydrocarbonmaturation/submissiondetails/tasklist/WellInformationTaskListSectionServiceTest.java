package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWellsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWellsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells.PlannedUnsanctionedWellsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells.PlannedUnsanctionedWellsSubmissionService;

@ExtendWith(MockitoExtension.class)
class WellInformationTaskListSectionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();
  @Mock
  private PlannedSanctionedWellsSubmissionService plannedSanctionedWellsSubmissionService;
  @Mock
  private PlannedUnsanctionedWellsSubmissionService plannedUnsanctionedWellsSubmissionService;
  @InjectMocks
  private WellInformationTaskListSectionService wellInformationTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"}
  )
  void getSection_fdpOrFdpa_expectSections(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var isSanctionedSubmittable = true;
    when(plannedSanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isSanctionedSubmittable);
    var isUnsanctionedSubmittable = true;
    when(plannedUnsanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isUnsanctionedSubmittable);


    var taskListSectionOptional = wellInformationTaskListSectionService.getSection(submissionDetail, USER);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
                new TaskListItem(PlannedSanctionedWellsController.PAGE_NAME,
                    TaskListLabel.notStartedOrComplete(isSanctionedSubmittable),
                    ReverseRouter.route(on(PlannedSanctionedWellsController.class)
                        .getPlannedSanctionedWells(submissionDetail.getId()))),
                new TaskListItem(PlannedUnsanctionedWellsController.PAGE_NAME,
                    TaskListLabel.notStartedOrComplete(isUnsanctionedSubmittable),
                    ReverseRouter.route(on(PlannedUnsanctionedWellsController.class)
                        .getPlannedUnsanctionedWells(submissionDetail.getId())))),
            WellInformationTaskListSectionService.SECTION_NAME,
            15
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void getSection_fdpOrFdpa_expectEmpty(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    assertThat(wellInformationTaskListSectionService.getSection(submissionDetail, USER)).isEmpty();
  }

  @Test
  void isSectionSubmittable_allSubSectionsSubmittable_thenTrue() {
    var isSanctionedSubmittable = true;
    when(plannedSanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isSanctionedSubmittable);
    var isUnsanctionedSubmittable = true;
    when(plannedUnsanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isUnsanctionedSubmittable);

    assertTrue(wellInformationTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }

  @Test
  void isSectionSubmittable_notAllSubSectionsSubmittable_thenFalse() {
    var isSanctionedSubmittable = false;
    when(plannedSanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail, USER))
        .thenReturn(isSanctionedSubmittable);

    assertFalse(wellInformationTaskListSectionService.isSectionSubmittable(submissionDetail, USER));
  }
}