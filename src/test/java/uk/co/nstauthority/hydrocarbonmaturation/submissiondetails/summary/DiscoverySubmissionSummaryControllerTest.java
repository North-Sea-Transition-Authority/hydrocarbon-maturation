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
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@ContextConfiguration(classes = DiscoverySubmissionSummaryController.class)
class DiscoverySubmissionSummaryControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail NSTA_USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private SubmissionSummaryFormService submissionSummaryFormService;

  @MockBean
  private DiscoverySummaryService discoverySummaryService;

  @MockBean
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  private GasFluidPropertySummaryView gasFluidPropertySummaryView;
  private OilFluidPropertySummaryView oilFluidPropertySummaryView;
  private GeneralFluidPropertySummaryView generalFluidPropertySummaryView;
  private PermeabilityDetailSummaryView permeabilityDetailSummaryView;
  private GeneralDetailSummaryView generalDetailSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    gasFluidPropertySummaryView =  GasFluidPropertySummaryView.empty();
    oilFluidPropertySummaryView =  OilFluidPropertySummaryView.empty();
    generalFluidPropertySummaryView = GeneralFluidPropertySummaryView.empty();
    permeabilityDetailSummaryView = PermeabilityDetailSummaryView.empty();
    generalDetailSummaryView = GeneralDetailSummaryView.empty("Prospect");
  }

  @Test
  void showSubmissionSummary_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(discoverySummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(permeabilityDetailSummaryView);
    when(discoverySummaryService.getOilFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilFluidPropertySummaryView);
    when(discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(gasFluidPropertySummaryView);
    when(discoverySummaryService.getGeneralFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalFluidPropertySummaryView);
    when(discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);

    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(NSTA_USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/discoverySubmissionSummary"));
  }

  @Test
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionSummaryFormService.validate(any(), any(), any())).thenReturn(ReverseRouter.emptyBindingResult());
    when(discoverySummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(permeabilityDetailSummaryView);
    when(discoverySummaryService.getOilFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilFluidPropertySummaryView);
    when(discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(gasFluidPropertySummaryView);
    when(discoverySummaryService.getGeneralFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalFluidPropertySummaryView);
    when(discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).processSubmission(submissionDetailId)))
                .with(user(NSTA_USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))));

    verify(submissionDetailService, times(1)).completeSubmission(submissionDetail);
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(discoverySummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(permeabilityDetailSummaryView);
    when(discoverySummaryService.getOilFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilFluidPropertySummaryView);
    when(discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(gasFluidPropertySummaryView);
    when(discoverySummaryService.getGeneralFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalFluidPropertySummaryView);
    when(discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);

    ModelMap modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(DiscoverySubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(NSTA_USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))),
            entry("gasFluidPropertiesSummaryView", gasFluidPropertySummaryView),
            entry("generalFluidPropertiesSummaryView", generalFluidPropertySummaryView),
            entry("oilFluidPropertiesSummaryView", oilFluidPropertySummaryView),
            entry("permeabiltySummaryView", permeabilityDetailSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("gasOnlyDiscovery", false),
            entry("oilOnlyDiscovery", false),
            entry("mixedDiscovery", false)
        );
  }
}