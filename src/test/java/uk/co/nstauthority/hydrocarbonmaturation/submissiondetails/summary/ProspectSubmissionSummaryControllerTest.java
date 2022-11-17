package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;

@WithMockUser
@ContextConfiguration(classes = ProspectSubmissionSummaryController.class)
class ProspectSubmissionSummaryControllerTest extends AbstractControllerTest {

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @MockBean
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @MockBean
  private SubmissionSummaryFormService submissionSummaryFormService;

  @MockBean
  private ProspectSubmissionService prospectSubmissionService;

  @MockBean
  private ProspectSummaryService prospectSummaryService;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

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
  private AdditionalInformationSummaryView additionalInformationSummaryView;
  private OpportunityDetailsSummaryView opportunityDetailsSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    generalDetailSummaryView =  GeneralDetailSummaryView.empty();
    sourceAndSealSummaryView =  SourceAndSealSummaryView.empty();
    hydrocarbonCaseSummaryView = HydrocarbonCaseSummaryView.empty();
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        "formation",
        "age",
        "lithology",
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
        new ArrayList<>(),
        new HashMap<>()
    );
    chanceOfSuccessSummaryView = ChanceOfSuccessSummaryView.empty();
    oilCaseVolumetricsSummaryView = new ProbabilityDistributionsFormSummaryView(new ArrayList<>());
    oilCaseOutputsSummaryView = OilCaseOutputsSummaryView.empty();
    gasCaseVolumetricsSummaryView = new ProbabilityDistributionsFormSummaryView(new ArrayList<>());
    mixedCaseVolumetricsSummaryView = new ProbabilityDistributionsFormSummaryView(new ArrayList<>());
    mixedCaseOutputsSummaryView = MixedCaseOutputsSummaryView.empty();
    additionalInformationSummaryView = AdditionalInformationSummaryView.empty();
    opportunityDetailsSummaryView = OpportunityDetailsSummaryView.empty();
  }

  @Test
  void showSubmissionSummary_assertStatusOk() throws Exception {
    var subNavList = List.of(new SubNavigationItem("Gas case outputs", "#gascaseoutputs"));

    when(prospectSummaryService.getProspectSubNavigation(submissionDetail)).thenReturn(subNavList);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(sourceAndSealSummaryView);
    when(prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(hydrocarbonCaseSummaryView);
    when(prospectSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(reservoirPropertiesSummaryView);
    when(prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(chanceOfSuccessSummaryView);
    when(prospectSummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilCaseVolumetricsSummaryView);
    when(prospectSummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(gasCaseVolumetricsSummaryView);
    when(prospectSummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilCaseOutputsSummaryView);
    when(prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(mixedCaseVolumetricsSummaryView);
    when(prospectSummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(mixedCaseOutputsSummaryView);
    when(prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(trapPropertiesSummaryView);
    when(prospectSummaryService.getAdditionalInformationSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(additionalInformationSummaryView);
    when(prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(opportunityDetailsSummaryView);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId, null))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/prospectSubmissionSummary"));
  }

  @Test
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionSummaryFormService.validate(any(), any(), any())).thenReturn(ReverseRouter.emptyBindingResult());

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).processSubmission(submissionDetailId, null, null)))
              .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))));

    verify(submissionDetailService, times(1)).completeSubmission(submissionDetail);
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    var subNavList = List.of(new SubNavigationItem("Gas case outputs", "#gascaseoutputs"));

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(prospectSummaryService.getProspectSubNavigation(submissionDetail)).thenReturn(subNavList);
    when(prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(sourceAndSealSummaryView);
    when(prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(hydrocarbonCaseSummaryView);
    when(prospectSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(reservoirPropertiesSummaryView);
    when(prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(chanceOfSuccessSummaryView);
    when(prospectSummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilCaseVolumetricsSummaryView);
    when(prospectSummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(gasCaseVolumetricsSummaryView);
    when(prospectSummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilCaseOutputsSummaryView);
    when(prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(mixedCaseVolumetricsSummaryView);
    when(prospectSummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(mixedCaseOutputsSummaryView);
    when(prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(trapPropertiesSummaryView);
    when(prospectSummaryService.getAdditionalInformationSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(additionalInformationSummaryView);
    when(prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(opportunityDetailsSummaryView);
    when(prospectSubmissionService.isSubmissionSubmittable(submissionDetail))
        .thenReturn(true);

    ModelMap modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId, null))))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))),
            entry("submittable", true),
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
            entry("additionalInformationSummaryView", additionalInformationSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView),
            entry("subNavigation", subNavList)
    );
  }
}