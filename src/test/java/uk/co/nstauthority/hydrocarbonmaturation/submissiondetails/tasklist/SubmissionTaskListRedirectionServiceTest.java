package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

class SubmissionTaskListRedirectionServiceTest {

  private SubmissionTaskListRedirectionService submissionTaskListRedirectionService;

  @BeforeEach
  void setUp() {
    submissionTaskListRedirectionService = new SubmissionTaskListRedirectionService();
  }

  @Test
  void getTaskListEndpointProspect() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);

    assertThat(submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail))
        .extracting(
            ModelAndView::getViewName
        ).isEqualTo(ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId)).getViewName());
  }

}