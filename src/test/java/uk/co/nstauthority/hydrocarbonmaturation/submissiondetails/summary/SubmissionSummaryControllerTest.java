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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.VolumetricsTaskListSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

@WithMockUser
@ContextConfiguration(classes = SubmissionSummaryController.class)
class SubmissionSummaryControllerTest extends AbstractControllerTest {

  @MockBean
  SubmissionDetailService submissionDetailService;

  @MockBean
  GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @MockBean
  ProspectSummaryService prospectSummaryService;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  private GeneralDetailSummaryView generalDetailSummaryView;
  private SourceAndSealSummaryView sourceAndSealSummaryView;
  private HydrocarbonCaseSummaryView hydrocarbonCaseSummarView;
  private ChanceOfSuccessSummaryView chanceOfSuccessSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    generalDetailSummaryView =  GeneralDetailSummaryView.empty();
    sourceAndSealSummaryView =  SourceAndSealSummaryView.empty();
    chanceOfSuccessSummaryView = ChanceOfSuccessSummaryView.empty();
    hydrocarbonCaseSummarView = HydrocarbonCaseSummaryView.empty();
  }

  @Test
  void showSubmissionSummary_assertStatusOk() throws Exception {
    var subNavList = List.of(new SubNavigationSectionItem(new VolumetricsTaskListSection()));

    when(prospectSummaryService.getProspectSubNavigation(submissionDetail)).thenReturn(subNavList);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(sourceAndSealSummaryView);
    when(prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(hydrocarbonCaseSummarView);
    when(prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(chanceOfSuccessSummaryView);

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionSummaryController.class).showSubmissionSummary(submissionDetailId))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/submissionSummary"));
  }

  @Test
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionSummaryController.class).processSubmission(submissionDetailId)))
              .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))));

    verify(submissionDetailService, times(1)).completeSubmission(submissionDetail);
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    var subNavList = List.of(new SubNavigationSectionItem(new VolumetricsTaskListSection()));

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(prospectSummaryService.getProspectSubNavigation(submissionDetail)).thenReturn(subNavList);
    when(prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(sourceAndSealSummaryView);
    when(prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(hydrocarbonCaseSummarView);
    when(prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(chanceOfSuccessSummaryView);

    ModelMap modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionSummaryController.class).showSubmissionSummary(submissionDetailId))))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("cancelUrl", ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("hydrocarbonCaseSummaryView", hydrocarbonCaseSummarView),
            entry("chanceOfSuccessSummaryView", chanceOfSuccessSummaryView),
            entry("subNavigation", subNavList)
    );
  }
}