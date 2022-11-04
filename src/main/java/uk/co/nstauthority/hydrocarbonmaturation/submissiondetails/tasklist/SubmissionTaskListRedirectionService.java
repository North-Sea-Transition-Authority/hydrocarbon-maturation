package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.PostDrillDryHoleTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

@Service
public class SubmissionTaskListRedirectionService {

  public ModelAndView getTaskListEndpoint(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getSubmissionType()) {
      case CREATE_PROSPECT, PROSPECT_UPDATE ->
          ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetail.getId()));
      case POST_DRILL_DRY_HOLE ->
          ReverseRouter.redirect(on(PostDrillDryHoleTaskListController.class).getTaskList(submissionDetail.getId()));
    };
  }
}
