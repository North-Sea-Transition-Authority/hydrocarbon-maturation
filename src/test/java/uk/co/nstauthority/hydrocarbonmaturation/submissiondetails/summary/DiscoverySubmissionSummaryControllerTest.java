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
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.DiscoverySubmissionSummaryController.PAGE_NAME;

import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;

@ContextConfiguration(classes = DiscoverySubmissionSummaryController.class)
class DiscoverySubmissionSummaryControllerTest extends AbstractSubmissionControllerTest {

  public static final String VIEW_NAME = "remi/submissiondetails/summary/discoverySubmissionSummary";

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @MockBean
  private SubmissionSummaryFormService submissionSummaryFormService;

  @MockBean
  private SubmissionReadyService submissionReadyService;

  @MockBean
  private SubmissionApprovalService submissionApprovalService;

  private static UUID submissionDetailId;

  private GasFluidPropertySummaryView gasFluidPropertySummaryView;
  private OilFluidPropertySummaryView oilFluidPropertySummaryView;
  private GeneralFluidPropertySummaryView generalFluidPropertySummaryView;
  private PermeabilityDetailSummaryView permeabilityDetailSummaryView;
  private GeneralDetailSummaryView generalDetailSummaryView;
  private ReservoirPropertiesSummaryView reservoirPropertiesSummaryView;
  private MixedCaseOutputsSummaryView mixedCaseOutputsSummaryView;
  private GasCaseOutputsSummaryView gasCaseOutputsSummaryView;
  private OilCaseOutputsSummaryView oilCaseOutputsSummaryView;
  private ProbabilityDistributionsFormSummaryView probabilityDistributionsFormSummaryView;
  private DrillStemTestSummaryView drillStemTestSummaryView;
  private SourceAndSealSummaryView sourceAndSealSummaryView;
  private OpportunityDetailsSummaryView opportunityDetailsSummaryView;
  private DiscoveryWellDetailsSummaryView discoveryWellDetailsSummaryView;
  private TrapPropertiesSummaryView trapPropertiesSummaryView;
  private ResourceDetailSummaryView resourceDetailSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    gasFluidPropertySummaryView =  GasFluidPropertySummaryView.empty();
    oilFluidPropertySummaryView =  OilFluidPropertySummaryView.empty();
    generalFluidPropertySummaryView = GeneralFluidPropertySummaryView.empty();
    permeabilityDetailSummaryView = PermeabilityDetailSummaryView.empty();
    generalDetailSummaryView = GeneralDetailSummaryView.empty(submissionDetail.getSubmissionType(), "Prospect");
    drillStemTestSummaryView = DrillStemTestSummaryView.empty();
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(SubmissionType.CREATE_DISCOVERY,
        "", "", "", "", "", "", "", List.of(), List.of());
    mixedCaseOutputsSummaryView = MixedCaseOutputsSummaryView.empty();
    gasCaseOutputsSummaryView = GasCaseOutputsSummaryView.empty();
    oilCaseOutputsSummaryView = OilCaseOutputsSummaryView.empty();
    probabilityDistributionsFormSummaryView = new ProbabilityDistributionsFormSummaryView(List.of());
    opportunityDetailsSummaryView = OpportunityDetailsSummaryView.empty();
    discoveryWellDetailsSummaryView = DiscoveryWellDetailsSummaryView.empty();
    sourceAndSealSummaryView = SourceAndSealSummaryView.empty();
    trapPropertiesSummaryView = TrapPropertiesSummaryView.empty();
    resourceDetailSummaryView = ResourceDetailSummaryView.empty("Prospect");
    submissionDetail.setSubmissionType(SubmissionType.UPDATE_DISCOVERY);
  }

  @SecurityTest
  void showSubmissionSummary_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockCreateDiscoverySubmissionSummaryModelAndView();
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME));
  }

  @SecurityTest
  void showSubmissionSummary_whenNotDiscovery_assertStatusForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
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
  void showSubmissionSummary_andUserNotInResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().is4xxClientError());
  }

  @SecurityTest
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionSummaryFormService.validate(any(), any(), any())).thenReturn(ReverseRouter.emptyBindingResult());
    mockCreateDiscoverySubmissionSummaryModelAndView();
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).processSubmission(submissionDetailId, null, null)))
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

    mockCreateDiscoverySubmissionSummaryModelAndView();

    mockMvc.perform(
        post(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).processSubmission(submissionDetailId, null, null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(submissionApprovalService, never()).submitSubmission(any());
  }

  @SecurityTest
  void processSubmission_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).processSubmission(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionReadyService.isSubmissionSubmittable(submissionDetail)).thenReturn(true);
    mockCreateDiscoverySubmissionSummaryModelAndView();

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageName", PAGE_NAME),
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))),
            entry("submittable", true),
            entry("isRegulator", false),
            entry("gasFluidPropertiesSummaryView", gasFluidPropertySummaryView),
            entry("generalFluidPropertiesSummaryView", generalFluidPropertySummaryView),
            entry("oilFluidPropertiesSummaryView", oilFluidPropertySummaryView),
            entry("permeabilitySummaryView", permeabilityDetailSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("drillStemTestSummaryView", drillStemTestSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("oilCaseVolumetricsSummaryView", probabilityDistributionsFormSummaryView),
            entry("oilCaseOutputsSummaryView", oilCaseOutputsSummaryView),
            entry("gasCaseVolumetricsSummaryView", probabilityDistributionsFormSummaryView),
            entry("gasCaseOutputsSummaryView", gasCaseOutputsSummaryView),
            entry("mixedCaseVolumetricsSummaryView", probabilityDistributionsFormSummaryView),
            entry("mixedCaseOutputsSummaryView", mixedCaseOutputsSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView),
            entry("discoveryWellDetailsSummaryView", discoveryWellDetailsSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("resourceDetailSummaryView", resourceDetailSummaryView),
            entry("gasOnlyDiscovery", false),
            entry("oilOnlyDiscovery", false),
            entry("mixedDiscovery", false),
            entry("accordionId", submissionDetailId)
        );
  }

  private void mockCreateDiscoverySubmissionSummaryModelAndView() {
    var modelAndView = new ModelAndView(VIEW_NAME)
        .addObject("generalDetailsSummaryView", generalDetailSummaryView)
        .addObject("gasFluidPropertiesSummaryView", gasFluidPropertySummaryView)
        .addObject("generalFluidPropertiesSummaryView", generalFluidPropertySummaryView)
        .addObject("oilFluidPropertiesSummaryView", oilFluidPropertySummaryView)
        .addObject("permeabilitySummaryView", permeabilityDetailSummaryView)
        .addObject("drillStemTestSummaryView", drillStemTestSummaryView)
        .addObject("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView)
        .addObject("oilCaseVolumetricsSummaryView", probabilityDistributionsFormSummaryView)
        .addObject("oilCaseOutputsSummaryView", oilCaseOutputsSummaryView)
        .addObject("gasCaseVolumetricsSummaryView", probabilityDistributionsFormSummaryView)
        .addObject("gasCaseOutputsSummaryView", gasCaseOutputsSummaryView)
        .addObject("mixedCaseVolumetricsSummaryView", probabilityDistributionsFormSummaryView)
        .addObject("mixedCaseOutputsSummaryView", mixedCaseOutputsSummaryView)
        .addObject("opportunityDetailsSummaryView", opportunityDetailsSummaryView)
        .addObject("discoveryWellDetailsSummaryView", discoveryWellDetailsSummaryView)
        .addObject("sourceAndSealSummaryView", sourceAndSealSummaryView)
        .addObject("trapPropertiesSummaryView", trapPropertiesSummaryView)
        .addObject("resourceDetailSummaryView", resourceDetailSummaryView)
        .addObject("gasOnlyDiscovery", false)
        .addObject("oilOnlyDiscovery", false)
        .addObject("mixedDiscovery", false)
        .addObject("submittable", true)
        .addObject("accordionId", submissionDetail.getId());
    when(submissionSummaryModelService.createDiscoverySubmissionSummaryModelAndView(
        submissionDetail, VIEW_NAME)
    ).thenReturn(modelAndView);
    when(submissionSummaryFormService.isRegulator()).thenReturn(false);

  }
}