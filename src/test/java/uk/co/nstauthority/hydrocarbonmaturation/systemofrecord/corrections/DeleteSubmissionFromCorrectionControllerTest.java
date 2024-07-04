package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchProspectAndDiscoveryRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ContextConfiguration(classes = DeleteSubmissionFromCorrectionController.class)
class DeleteSubmissionFromCorrectionControllerTest extends AbstractSubmissionControllerTest {

  private static final Set<RolePermission> VALID_PERMISSIONS = Set.of(
      RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
      RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER);
  private static final String RESOURCE_NAME = "test";
  private static final String SUBMISSION_TYPE_NAME = "submission type name";

  @MockBean
  private SubmissionResourceService submissionResourceService;

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @MockBean
  private CorrectionService correctionService;

  private GeneralDetailSummaryView generalDetailSummaryView;
  private SourceAndSealSummaryView sourceAndSealSummaryView;
  private HydrocarbonCaseSummaryView hydrocarbonCaseSummaryView;
  private ChanceOfSuccessSummaryView chanceOfSuccessSummaryView;
  private ReservoirPropertiesSummaryView reservoirPropertiesSummaryView;
  private ProbabilityDistributionTableView oilCaseVolumetricsTableView;
  private ProbabilityDistributionTableView gasCaseVolumetricsTableView;
  private OilCaseOutputsSummaryView oilCaseOutputsSummaryView;
  private ProbabilityDistributionTableView mixedCaseVolumetricsTableView;
  private MixedCaseOutputsSummaryView mixedCaseOutputsSummaryView;
  private TrapPropertiesSummaryView trapPropertiesSummaryView;
  private OpportunityDetailsSummaryView opportunityDetailsSummaryView;
  private OutlineForMaximumCaseSummaryView outlineForMaximumCaseSummaryView;
  private GasCaseOutputsSummaryView gasCaseOutputsSummaryView;
  private ResourceDetailSummaryView resourceDetailSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID)).thenReturn(submissionDetail);
    when(permissionService.hasPermission(user, VALID_PERMISSIONS))
        .thenReturn(true);
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn(RESOURCE_NAME);
    when(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail)).thenReturn(SUBMISSION_TYPE_NAME);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail)).thenReturn(SUBMISSION_TYPE_NAME);

    generalDetailSummaryView =  GeneralDetailSummaryView.empty(submissionDetail.getSubmissionType(), "prospect");
    sourceAndSealSummaryView =  SourceAndSealSummaryView.empty();
    hydrocarbonCaseSummaryView = HydrocarbonCaseSummaryView.empty();
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        submissionDetail.getSubmissionType(),
        "formation",
        "sub unit",
        "age",
        "group name",
        "lithology",
        "facies",
        "connectivity",
        ProbabilityDistributionTableView.empty(),
        ProbabilityDistributionTableView.empty());
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
        ProbabilityDistributionTableView.empty(),
        new HashMap<>()
    );
    resourceDetailSummaryView = ResourceDetailSummaryView.empty("prospect");
    chanceOfSuccessSummaryView = ChanceOfSuccessSummaryView.empty();
    oilCaseVolumetricsTableView = ProbabilityDistributionTableView.empty();
    oilCaseOutputsSummaryView = OilCaseOutputsSummaryView.empty(new SummaryBehaviour());
    gasCaseVolumetricsTableView = ProbabilityDistributionTableView.empty();
    gasCaseOutputsSummaryView = GasCaseOutputsSummaryView.empty(new SummaryBehaviour());
    mixedCaseVolumetricsTableView = ProbabilityDistributionTableView.empty();
    mixedCaseOutputsSummaryView = MixedCaseOutputsSummaryView.empty(new SummaryBehaviour());
    opportunityDetailsSummaryView = OpportunityDetailsSummaryView.empty();
    outlineForMaximumCaseSummaryView = OutlineForMaximumCaseSummaryView.empty();
  }

  @SecurityTest
  void showDeleteConfirmationPage_canStartCorrection_assertStatusOk() throws Exception {
    when(correctionService.canStartCorrection(submissionDetail.getSubmission().getResource(), user)).thenReturn(true);
    setUpWhenUserIsResponsibleTeam_passSecurity(USER, submissionDetail, VALID_PERMISSIONS);
    mockCreateProspectSubmissionSummaryModelAndView();
    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionFromCorrectionController.class)
            .showDeleteConfirmationPage(submissionDetail.getId(), null)))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void showDeleteConfirmationPage_cantStartCorrection_assertStatusForbidden() throws Exception {
    when(correctionService.canStartCorrection(submissionDetail.getSubmission().getResource(), user)).thenReturn(false);
    setUpWhenUserIsResponsibleTeam_passSecurity(USER, submissionDetail, VALID_PERMISSIONS);
    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionFromCorrectionController.class).showDeleteConfirmationPage(
            submissionDetail.getId(), null)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showDeleteConfirmationPage_wrongPermission_assertStatusForbidden() throws Exception {
    when(permissionService.hasPermission(eq(user), anySet())).thenReturn(false);
    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionFromCorrectionController.class).showDeleteConfirmationPage(
            submissionDetail.getId(), null)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showDeleteConfirmationPage_notAccepted_assertStatusForbidden() throws Exception {
    setUpWhenUserIsResponsibleTeam_passSecurity(USER, submissionDetail, VALID_PERMISSIONS);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionFromCorrectionController.class).showDeleteConfirmationPage(
            submissionDetail.getId(), null)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void showDeleteConfirmationPage_assertModelProperties() throws Exception {
    when(correctionService.canStartCorrection(submissionDetail.getSubmission().getResource(), user)).thenReturn(true);
    mockCreateProspectSubmissionSummaryModelAndView();

    var modelMap = Objects.requireNonNull(mockMvc.perform(get(
            ReverseRouter.route(on(DeleteSubmissionFromCorrectionController.class).showDeleteConfirmationPage(
                submissionDetail.getId(), null)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageName",
                String.format("Are you sure you want to delete this %s?", SUBMISSION_TYPE_NAME.toLowerCase())),
            entry("resourceName", RESOURCE_NAME),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("deleteUrl",
                ReverseRouter.route(on(DeleteSubmissionFromCorrectionController.class)
                    .deleteSubmission(submissionDetail.getId(), null, null))),
            entry("cancelUrl", ReverseRouter.route(on(CorrectionSelectionController.class)
                .startCorrection(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("deleteButtonText", "Delete submission")
        );
  }

  @Test
  void deleteSubmission_whenInitialResourceSubmission_assertRedirection() throws Exception {
    var flashMap = mockMvc.perform(
            post(ReverseRouter.route(on(DeleteSubmissionFromCorrectionController.class)
                .deleteSubmission(submissionDetail.getId(), null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
            .showSearchResourceRecordsScreen(null))))
        .andReturn().getFlashMap();

    verify(correctionService, times(1)).deleteSubmission(submissionDetail, USER);

    assertThat((NotificationBanner) flashMap.get("flash"))
        .extracting(
            NotificationBanner::getTitle,
            NotificationBanner::getContent)
        .containsExactly(
            "%s deleted".formatted(SUBMISSION_TYPE_NAME),
            "%s for %s has been deleted".formatted(SUBMISSION_TYPE_NAME, RESOURCE_NAME));
  }

  @Test
  void deleteSubmission_whenResourceUpdate_assertRedirection() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    var flashMap = mockMvc.perform(
            post(ReverseRouter.route(on(DeleteSubmissionFromCorrectionController.class)
                .deleteSubmission(submissionDetail.getId(), null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(submissionDetail.getSubmission().getResource().getId()))))
        .andReturn().getFlashMap();

    verify(correctionService, times(1)).deleteSubmission(submissionDetail, USER);

    assertThat((NotificationBanner) flashMap.get("flash"))
        .extracting(
            NotificationBanner::getTitle,
            NotificationBanner::getContent)
        .containsExactly(
            "%s deleted".formatted(SUBMISSION_TYPE_NAME),
            "%s for %s has been deleted".formatted(SUBMISSION_TYPE_NAME, RESOURCE_NAME));
  }

  private void mockCreateProspectSubmissionSummaryModelAndView() {
    var modelAndView = new ModelAndView(DeleteSubmissionFromCorrectionController.VIEW_NAME)
        .addObject("generalDetailsSummaryView", generalDetailSummaryView)
        .addObject("sourceAndSealSummaryView", sourceAndSealSummaryView)
        .addObject("chanceOfSuccessSummaryView", chanceOfSuccessSummaryView)
        .addObject("hydrocarbonCaseSummaryView", hydrocarbonCaseSummaryView)
        .addObject("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView)
        .addObject("trapPropertiesSummaryView", trapPropertiesSummaryView)
        .addObject("oilCaseVolumetricsSummaryView", oilCaseVolumetricsTableView)
        .addObject("oilCaseOutputsSummaryView", oilCaseOutputsSummaryView)
        .addObject("gasCaseVolumetricsSummaryView", gasCaseVolumetricsTableView)
        .addObject("gasCaseOutputsSummaryView", gasCaseOutputsSummaryView)
        .addObject("mixedCaseVolumetricsSummaryView", mixedCaseVolumetricsTableView)
        .addObject("mixedCaseOutputsSummaryView", mixedCaseOutputsSummaryView)
        .addObject("opportunityDetailsSummaryView", opportunityDetailsSummaryView)
        .addObject("outlineForMaximumCaseSummaryView", outlineForMaximumCaseSummaryView)
        .addObject("resourceDetailSummaryView", resourceDetailSummaryView)
        .addObject("submittable", true)
        .addObject("gasCase", true)
        .addObject("oilCase", true)
        .addObject("isSubmissionUpdate", false)
        .addObject("mixedCase", true)
        .addObject("accordionId", submissionDetail.getId())
        .addObject("isOffshore", true);
    when(submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        DeleteSubmissionFromCorrectionController.VIEW_NAME
    )).thenReturn(modelAndView);
  }
}
