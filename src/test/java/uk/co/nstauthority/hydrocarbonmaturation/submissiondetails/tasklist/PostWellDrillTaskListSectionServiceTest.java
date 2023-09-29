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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class PostWellDrillTaskListSectionServiceTest {

  @Mock
  private DryWellDetailsSubmissionService dryWellDetailsSubmissionService;

  @Mock
  private DrillStemTestSubmissionService drillStemTestSubmissionService;

  @Mock
  private DiscoveryWellDetailsSubmissionService discoveryWellDetailsSubmissionService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @InjectMocks
  private PostWellDrillTaskListSectionService postWellDrillTaskListSectionService;

  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @Test
  void getSection_postDrillSubmission() {
    var taskListSectionOptional = postWellDrillTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Dry well details",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(DryWellDetailsController.class).getDryWellDetails(submissionDetail.getId(), null)))),
            "Post well drill",
            30
        );
  }

  @Test
  void getSection_prospectSubmission() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var taskListSectionOptional = postWellDrillTaskListSectionService.getSection(submissionDetail);

    assertThat(taskListSectionOptional)
        .isNotPresent();
  }

  @Test
  void isSectionSubmittable_prospectSubmission_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    when(reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    assertTrue(postWellDrillTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_postDrillSubmissionWhenSubmittable_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    assertTrue(postWellDrillTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_postDrillSubmissionWhenOneSectionNotSubmittable_assertFalse() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);
    when(reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    assertFalse(postWellDrillTaskListSectionService.isSectionSubmittable(submissionDetail));
  }


  @Test
  void isSectionSubmittable_discoverySubmissionWhenSubmittable_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    when(discoveryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail))
        .thenReturn(true);
    when(drillStemTestSubmissionService.isSectionSubmittable(reservoirSubmissionDetail))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    assertTrue(postWellDrillTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_discoverySubmissionWhenOneSectionNotSubmittable_assertFalse() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    when(discoveryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail))
        .thenReturn(false);
    when(reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    assertFalse(postWellDrillTaskListSectionService.isSectionSubmittable(submissionDetail));
  }
}