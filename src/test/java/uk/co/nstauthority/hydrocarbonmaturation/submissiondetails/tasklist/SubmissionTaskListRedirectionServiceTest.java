package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.discovery.DiscoveryTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.PostDrillDryHoleTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

class SubmissionTaskListRedirectionServiceTest {

  private SubmissionTaskListRedirectionService submissionTaskListRedirectionService;

  @BeforeEach
  void setUp() {
    submissionTaskListRedirectionService = new SubmissionTaskListRedirectionService();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_PROSPECT", "PROSPECT_UPDATE"})
  void getTaskListEndpointProspect(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(submissionType);

    assertThat(submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail))
        .extracting(ModelAndView::getViewName)
        .isEqualTo(ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId)).getViewName());
  }

  @Test
  void getTaskListEndpointProspect_PostDrillDryHole() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    assertThat(submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail))
        .extracting(ModelAndView::getViewName)
        .isEqualTo(ReverseRouter.redirect(on(PostDrillDryHoleTaskListController.class).getTaskList(submissionDetailId)).getViewName());
  }

  @Test
  void getTaskListEndpointDiscovery_PostDrillDiscovery() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    assertThat(submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail))
        .extracting(ModelAndView::getViewName)
        .isEqualTo(ReverseRouter.redirect(on(DiscoveryTaskListController.class).getTaskList(submissionDetailId)).getViewName());
  }
}