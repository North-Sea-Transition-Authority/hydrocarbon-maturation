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
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ProspectSubmissionSummaryController.PAGE_NAME;

import java.util.ArrayList;
import java.util.HashMap;
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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionApprovalService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;

@ContextConfiguration(classes = ProspectSubmissionSummaryController.class)
class ProspectSubmissionSummaryControllerTest extends AbstractSubmissionControllerTest {

  public static final String VIEW_NAME = "remi/submissiondetails/summary/prospectSubmissionSummary";

  @MockBean
  private SubmissionSummaryFormService submissionSummaryFormService;

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @MockBean
  private SubmissionReadyService submissionReadyService;

  @MockBean
  private SubmissionApprovalService submissionApprovalService;

  private UUID submissionDetailId;

  private GeneralDetailSummaryView generalDetailSummaryView;
  private SourceAndSealSummaryView sourceAndSealSummaryView;
  private HydrocarbonCaseSummaryView hydrocarbonCaseSummaryView;
  private ChanceOfSuccessSummaryView chanceOfSuccessSummaryView;
  private ReservoirPropertiesSummaryView reservoirPropertiesSummaryView;
  private ProbabilityDistributionsFormSummaryView oilCaseVolumetricsSummaryView;
  private ProbabilityDistributionsFormSummaryView gasCaseVolumetricsSummaryView;
  private OilCaseOutputsSummaryView oilCaseOutputsSummaryView;
  private ProbabilityDistributionsFormSummaryView mixedCaseVolumetricsSummaryView;
  private MixedCaseOutputsSummaryView mixedCaseOutputsSummaryView;
  private TrapPropertiesSummaryView trapPropertiesSummaryView;
  private OpportunityDetailsSummaryView opportunityDetailsSummaryView;
  private GasCaseOutputsSummaryView gasCaseOutputsSummaryView;
  private ResourceDetailSummaryView resourceDetailSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    generalDetailSummaryView =  GeneralDetailSummaryView.empty(SubmissionType.CREATE_PROSPECT, "prospect");
    sourceAndSealSummaryView =  SourceAndSealSummaryView.empty();
    hydrocarbonCaseSummaryView = HydrocarbonCaseSummaryView.empty();
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        SubmissionType.CREATE_PROSPECT,
        "formation",
        "sub unit",
        "age",
        "group name",
        "lithology",
        "facies",
        "connectivity",
        new ArrayList<>(),
        new ArrayList<>());
    trapPropertiesSummaryView = new TrapPropertiesSummaryView(
        "desc",
        "Other",
        "other trap type",
        "1",
        "1",
        "1",
        "10",
        "10",
        "10",
        "East",
        ReservoirDatum.ED50.getDisplayName(),
        "15/10",
        new ArrayList<>(),
        new HashMap<>()
    );
    resourceDetailSummaryView = ResourceDetailSummaryView.empty("prospect");
    chanceOfSuccessSummaryView = ChanceOfSuccessSummaryView.empty();
    oilCaseVolumetricsSummaryView = new ProbabilityDistributionsFormSummaryView(new ArrayList<>());
    oilCaseOutputsSummaryView = OilCaseOutputsSummaryView.empty();
    gasCaseVolumetricsSummaryView = new ProbabilityDistributionsFormSummaryView(new ArrayList<>());
    gasCaseOutputsSummaryView = GasCaseOutputsSummaryView.empty();
    mixedCaseVolumetricsSummaryView = new ProbabilityDistributionsFormSummaryView(new ArrayList<>());
    mixedCaseOutputsSummaryView = MixedCaseOutputsSummaryView.empty();
    opportunityDetailsSummaryView = OpportunityDetailsSummaryView.empty();
  }

  @SecurityTest
  void showSubmissionSummary_whenNotProspect_assertStatusForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class)
                .showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showSubmissionSummary_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockCreateProspectSubmissionSummaryModelAndView();
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME));
  }

  @SecurityTest
  void showSubmissionSummary_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showSubmissionSummary_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class)
                .showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void processSubmission_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectSubmissionSummaryController.class)
                .processSubmission(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionSummaryFormService.validate(any(), any(), any())).thenReturn(ReverseRouter.emptyBindingResult());
    mockCreateProspectSubmissionSummaryModelAndView();

    mockMvc.perform(
        post(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).processSubmission(submissionDetailId, null, null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))));

    verify(submissionApprovalService, times(1)).submitSubmission(submissionDetail);
  }

  @Test
  void processSubmission_whenInvalidForm_assertOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(SubmissionSummaryForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(submissionSummaryFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockCreateProspectSubmissionSummaryModelAndView();

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).processSubmission(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(submissionApprovalService, never()).submitSubmission(any());
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionReadyService.isSubmissionSubmittable(submissionDetail)).thenReturn(true);
    mockCreateProspectSubmissionSummaryModelAndView();

    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageName", PAGE_NAME),
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))),
            entry("submittable", true),
            entry("isRegulator", true),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("hydrocarbonCaseSummaryView", hydrocarbonCaseSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("chanceOfSuccessSummaryView", chanceOfSuccessSummaryView),
            entry("oilCaseVolumetricsSummaryView", oilCaseVolumetricsSummaryView),
            entry("gasCaseVolumetricsSummaryView", gasCaseVolumetricsSummaryView),
            entry("oilCaseOutputsSummaryView", oilCaseOutputsSummaryView),
            entry("mixedCaseVolumetricsSummaryView", mixedCaseVolumetricsSummaryView),
            entry("mixedCaseOutputsSummaryView", mixedCaseOutputsSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("resourceDetailSummaryView", resourceDetailSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView),
            entry("accordionId", submissionDetailId)
    );
  }

  private void mockCreateProspectSubmissionSummaryModelAndView() {
    var modelAndView = new ModelAndView(VIEW_NAME)
        .addObject("generalDetailsSummaryView", generalDetailSummaryView)
        .addObject("sourceAndSealSummaryView", sourceAndSealSummaryView)
        .addObject("chanceOfSuccessSummaryView", chanceOfSuccessSummaryView)
        .addObject("hydrocarbonCaseSummaryView", hydrocarbonCaseSummaryView)
        .addObject("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView)
        .addObject("trapPropertiesSummaryView", trapPropertiesSummaryView)
        .addObject("oilCaseVolumetricsSummaryView", oilCaseVolumetricsSummaryView)
        .addObject("oilCaseOutputsSummaryView", oilCaseOutputsSummaryView)
        .addObject("gasCaseVolumetricsSummaryView", gasCaseVolumetricsSummaryView)
        .addObject("gasCaseOutputsSummaryView", gasCaseOutputsSummaryView)
        .addObject("mixedCaseVolumetricsSummaryView", mixedCaseVolumetricsSummaryView)
        .addObject("mixedCaseOutputsSummaryView", mixedCaseOutputsSummaryView)
        .addObject("opportunityDetailsSummaryView", opportunityDetailsSummaryView)
        .addObject("resourceDetailSummaryView", resourceDetailSummaryView)
        .addObject("submittable", true)
        .addObject("gasCase", true)
        .addObject("oilCase", true)
        .addObject("mixedCase", true)
        .addObject("accordionId", submissionDetail.getId());
    when(submissionSummaryModelService.createProspectSubmissionSummaryModelAndView(
        submissionDetail,
        VIEW_NAME
    )).thenReturn(modelAndView);
    when(submissionSummaryFormService.isRegulator()).thenReturn(true);
  }
}