package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.doReturn;
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
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@ContextConfiguration(classes = DiscoverySubmissionSummaryController.class)
class DiscoverySubmissionSummaryControllerTest extends AbstractControllerTest {

  public static final String VIEW_NAME = "remi/submissiondetails/summary/discoverySubmissionSummary";
  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  private static UUID submissionDetailId;

  private GasFluidPropertySummaryView gasFluidPropertySummaryView;
  private OilFluidPropertySummaryView oilFluidPropertySummaryView;
  private GeneralFluidPropertySummaryView generalFluidPropertySummaryView;
  private PermeabilityDetailSummaryView permeabilityDetailSummaryView;
  private GeneralDetailSummaryView generalDetailSummaryView;
  private ReservoirPropertiesSummaryView reservoirPropertiesSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    gasFluidPropertySummaryView =  GasFluidPropertySummaryView.empty();
    oilFluidPropertySummaryView =  OilFluidPropertySummaryView.empty();
    generalFluidPropertySummaryView = GeneralFluidPropertySummaryView.empty();
    permeabilityDetailSummaryView = PermeabilityDetailSummaryView.empty();
    generalDetailSummaryView = GeneralDetailSummaryView.empty(submissionDetail.getSubmissionType(), "Prospect");
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(SubmissionType.CREATE_DISCOVERY,
        "", "", "", "", "", List.of(), List.of());
  }

  @SecurityTest
  void showSubmissionSummary_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockCreateDiscoverySubmissionSummaryModelAndView();

    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME));
  }

  @SecurityTest
  void showSubmissionSummary_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().is4xxClientError());
  }

  @SecurityTest
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).processSubmission(submissionDetailId)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))));

    verify(submissionDetailService, times(1)).completeSubmission(submissionDetail);
  }

  @SecurityTest
  void processSubmission__andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).processSubmission(submissionDetailId)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockCreateDiscoverySubmissionSummaryModelAndView();

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))),
            entry("gasFluidPropertiesSummaryView", gasFluidPropertySummaryView),
            entry("generalFluidPropertiesSummaryView", generalFluidPropertySummaryView),
            entry("oilFluidPropertiesSummaryView", oilFluidPropertySummaryView),
            entry("permeabilitySummaryView", permeabilityDetailSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("gasOnlyDiscovery", false),
            entry("oilOnlyDiscovery", false),
            entry("mixedDiscovery", false)
        );
  }

  private void mockCreateDiscoverySubmissionSummaryModelAndView() {
    var modelAndView = new ModelAndView(VIEW_NAME)
        .addObject("generalDetailsSummaryView", generalDetailSummaryView)
        .addObject("gasFluidPropertiesSummaryView", gasFluidPropertySummaryView)
        .addObject("generalFluidPropertiesSummaryView", generalFluidPropertySummaryView)
        .addObject("oilFluidPropertiesSummaryView", oilFluidPropertySummaryView)
        .addObject("permeabilitySummaryView", permeabilityDetailSummaryView)
        .addObject("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView)
        .addObject("gasOnlyDiscovery", false)
        .addObject("oilOnlyDiscovery", false)
        .addObject("mixedDiscovery", false);
    doReturn(modelAndView).when(submissionSummaryModelService).createDiscoverySubmissionSummaryModelAndView(submissionDetail,
        VIEW_NAME);
  }
}