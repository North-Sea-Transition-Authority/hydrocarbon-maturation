package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionWorkflowService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/submit")
public class SubmissionSummaryController {

  private final ControllerHelperService controllerHelperService;
  private final SubmissionWorkflowService submissionWorkflowService;

  @Autowired
  public SubmissionSummaryController(ControllerHelperService controllerHelperService,
                                     SubmissionWorkflowService submissionWorkflowService) {
    this.controllerHelperService = controllerHelperService;
    this.submissionWorkflowService = submissionWorkflowService;
  }

  @GetMapping
  public ModelAndView showSubmissionSummary(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    return getSubmissionSummaryModelAndView(submissionDetailId);
  }

  @PostMapping
  public ModelAndView processSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId) {

    submissionWorkflowService.completeSubmission(submissionDetailId);
    return ReverseRouter.redirect(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId));
  }

  ModelAndView getSubmissionSummaryModelAndView(UUID submissionDetailId) {
    return new ModelAndView("remi/submissiondetails/submissionSummary")
        .addObject("backLinkUrl", ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)));
  }
}
