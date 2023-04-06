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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSubmissionService;

@ExtendWith(MockitoExtension.class)
class OpportunityTaskListSectionServiceTest {

  @Mock
  private OpportunityDetailsSubmissionService opportunityDetailsSubmissionService;

  @Mock
  private TrapPropertiesSubmissionService trapPropertiesSubmissionService;

  @Mock
  private SourceAndSealSubmissionService sourceAndSealSubmissionService;

  @Mock
  private HydrocarbonCaseSubmissionService hydrocarbonCaseSubmissionService;

  @Mock
  private ChanceOfSuccessSubmissionService chanceOfSuccessSubmissionService;

  @InjectMocks
  private OpportunityTaskListSectionService opportunityTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getSection_prospectSubmission() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    when(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    var taskListSectionOptional = opportunityTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Opportunity details",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetail.getId(), null))),
            new TaskListItem("Trap properties",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetail.getId()))),
            new TaskListItem("Source and seal",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetail.getId()))),
            new TaskListItem("Hydrocarbon case",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(HydrocarbonCaseController.class).getHydrocarbonCase(submissionDetail.getId()))),
            new TaskListItem("Chance of success",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetail.getId())))),
            "Opportunity",
            20
        );
  }

  @Test
  void getSection_postDrillSubmission() {
    when(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    var taskListSectionOptional = opportunityTaskListSectionService.getSection(submissionDetail);
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Opportunity details",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetail.getId(), null))),
            new TaskListItem("Trap properties",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetail.getId()))),
            new TaskListItem("Source and seal",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetail.getId())))),
            "Opportunity",
            20
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT"})
  void isSectionSubmittable_whenProspectSubmissionAndSubmittable_assertTrue(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    when(chanceOfSuccessSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(trapPropertiesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(hydrocarbonCaseSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(opportunityTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT"})
  void isSectionSubmittable_whenProspectSubmissionAndNotSubmittable_assertFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    when(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(opportunityTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"POST_DRILL_DRY_HOLE", "CREATE_DISCOVERY_FROM_PROSPECT"})
  void isSectionSubmittable_whenNotProspectSubmissionAndSubmittable_assertTrue(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    when(trapPropertiesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(opportunityTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"POST_DRILL_DRY_HOLE", "CREATE_DISCOVERY_FROM_PROSPECT"})
  void isSectionSubmittable_whenNotProspectSubmissionAndNotSubmittable_assertFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    when(trapPropertiesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);
    when(sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertFalse(opportunityTaskListSectionService.isSectionSubmittable(submissionDetail));
  }
}