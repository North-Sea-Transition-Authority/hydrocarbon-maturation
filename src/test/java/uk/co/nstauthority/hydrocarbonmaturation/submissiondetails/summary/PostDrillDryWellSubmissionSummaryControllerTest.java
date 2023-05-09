package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.PostDrillDryWellSubmissionSummaryController.PAGE_NAME;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionApprovalService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;

@ContextConfiguration(classes = PostDrillDryWellSubmissionSummaryController.class)
class PostDrillDryWellSubmissionSummaryControllerTest extends AbstractSubmissionControllerTest {

  public static final String VIEW_NAME = "remi/submissiondetails/summary/dryWellSubmissionSummary";

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @MockBean
  private SubmissionSummaryFormService submissionSummaryFormService;

  @MockBean
  private SubmissionReadyService submissionReadyService;

  @MockBean
  private SubmissionApprovalService submissionApprovalService;

  private static UUID submissionDetailId;
  private DryWellDetailsSummaryView dryWellDetailsSummaryView;
  private PermeabilityDetailSummaryView permeabilityDetailSummaryView;
  private ReservoirPropertiesSummaryView reservoirPropertiesSummaryView;
  private GeneralDetailSummaryView generalDetailSummaryView;
  private SourceAndSealSummaryView sourceAndSealSummaryView;
  private OpportunityDetailsSummaryView opportunityDetailsSummaryView;
  private TrapPropertiesSummaryView trapPropertiesSummaryView;
  private ResourceDetailSummaryView resourceDetailSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    dryWellDetailsSummaryView =  DryWellDetailsSummaryView.empty();
    permeabilityDetailSummaryView = PermeabilityDetailSummaryView.empty();
    generalDetailSummaryView = GeneralDetailSummaryView.empty(SubmissionType.POST_DRILL_DRY_HOLE, "prospect");
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        SubmissionType.POST_DRILL_DRY_HOLE,
        "formation",
        "sub unit",
        "age",
        "group name",
        "lithology",
        "facies",
        "connectivity",
        new ArrayList<>(),
        new ArrayList<>());
    sourceAndSealSummaryView = SourceAndSealSummaryView.empty();
    opportunityDetailsSummaryView = OpportunityDetailsSummaryView.empty();
    trapPropertiesSummaryView = TrapPropertiesSummaryView.empty();
    resourceDetailSummaryView = ResourceDetailSummaryView.empty("Prospect");
  }

  @SecurityTest
  void showSubmissionSummary_whenNotPostDrill_assertStatusForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class)
                .showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showSubmissionSummary_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(
        get(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class)
            .showSubmissionSummary(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME));
  }

  @SecurityTest
  void showSubmissionSummary_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    mockMvc.perform(
            get(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class)
                .showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showSubmissionSummary_andUserNotInResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class)
                .showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void processSubmission_whenInvalidForm_assertOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(SubmissionSummaryForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(submissionSummaryFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();

    mockMvc.perform(
            post(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class).processSubmission(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(submissionApprovalService, never()).submitSubmission(any());
  }

  @Test
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionSummaryFormService.validate(any(), any(), any())).thenReturn(ReverseRouter.emptyBindingResult());
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();
    mockMvc.perform(
        post(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class).processSubmission(submissionDetailId, null, null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))));

    verify(submissionApprovalService, times(1)).submitSubmission(submissionDetail);
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionReadyService.isSubmissionSubmittable(submissionDetail)).thenReturn(true);
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();

    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class)
            .showSubmissionSummary(submissionDetailId)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageName", PAGE_NAME),
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))),
            entry("submittable", true),
            entry("isRegulator", false),
            entry("dryWellDetailsSummaryView", dryWellDetailsSummaryView),
            entry("permeabilitySummaryView", permeabilityDetailSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("resourceDetailSummaryView", resourceDetailSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("accordionId", submissionDetailId)
        );
  }

  private void mockCreatePostDrillDryWellSubmissionSummaryModelAndView() {
    var modelAndView = new ModelAndView(VIEW_NAME)
        .addObject("generalDetailsSummaryView", generalDetailSummaryView)
        .addObject("dryWellDetailsSummaryView", dryWellDetailsSummaryView)
        .addObject("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView)
        .addObject("permeabilitySummaryView", permeabilityDetailSummaryView)
        .addObject("opportunityDetailsSummaryView", opportunityDetailsSummaryView)
        .addObject("sourceAndSealSummaryView", sourceAndSealSummaryView)
        .addObject("trapPropertiesSummaryView", trapPropertiesSummaryView)
        .addObject("resourceDetailSummaryView", resourceDetailSummaryView)
        .addObject("submittable", true)
        .addObject("isSubmissionUpdate", false)
        .addObject("accordionId", submissionDetail.getId());
    when(submissionSummaryModelService.createPostDrillDryWellSubmissionSummaryModelAndView(
        submissionDetail,
        VIEW_NAME)
    ).thenReturn(modelAndView);
    when(submissionSummaryFormService.isRegulator()).thenReturn(false);
  }
}