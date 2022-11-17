package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@ContextConfiguration(classes = PostDrillDryWellSubmissionSummaryController.class)
class PostDrillDryWellSubmissionSummaryControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private PostDrillDryWellSummaryService postDrillDryWellSummaryService;

  @MockBean
  private SubmissionDetailService submissionDetailService;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;
  private DryWellDetailsSummaryView dryWellDetailsSummaryView;
  private PermeabilityDetailSummaryView permeabilityDetailSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    dryWellDetailsSummaryView =  DryWellDetailsSummaryView.empty();
    permeabilityDetailSummaryView = PermeabilityDetailSummaryView.empty();
  }

  @Test
  void showSubmissionSummary_assertStatusOk() throws Exception {
    var subNavList = List.of(new SubNavigationItem("item1", "#item1"));

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(postDrillDryWellSummaryService.getDryWellDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(dryWellDetailsSummaryView);
    when(postDrillDryWellSummaryService.getPostDrillDryWellSubNavigation(submissionDetail))
        .thenReturn(subNavList);
    when(postDrillDryWellSummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(permeabilityDetailSummaryView);

    mockMvc.perform(
        get(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class)
            .showSubmissionSummary(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/dryWellSubmissionSummary"));
  }

  @Test
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class).processSubmission(submissionDetailId)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))));

    verify(submissionDetailService, times(1)).completeSubmission(submissionDetail);
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    var subNavList = List.of(new SubNavigationItem("item1", "#item1"));

    when(postDrillDryWellSummaryService.getDryWellDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(dryWellDetailsSummaryView);
    when(postDrillDryWellSummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(permeabilityDetailSummaryView);
    when(postDrillDryWellSummaryService.getPostDrillDryWellSubNavigation(submissionDetail))
        .thenReturn(subNavList);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    ModelMap modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class)
            .showSubmissionSummary(submissionDetailId)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))),
            entry("dryWellDetailsSummaryView", dryWellDetailsSummaryView),
            entry("permeabiltySummaryView", permeabilityDetailSummaryView),
            entry("subNavigation", subNavList)
        );
  }
}