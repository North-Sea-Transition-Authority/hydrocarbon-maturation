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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/summary")
public class SubmissionSummaryController {

  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public SubmissionSummaryController(SubmissionDetailService submissionDetailService) {
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView showSubmissionSummary(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    return getSubmissionSummaryModelAndView(submissionDetailId);
  }

  @PostMapping
  public ModelAndView processSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    SubmissionDetail submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    submissionDetailService.completeSubmission(submissionDetail);

    return ReverseRouter.redirect(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId));
  }

  private ModelAndView getSubmissionSummaryModelAndView(UUID submissionDetailId) {
    return new ModelAndView("remi/submissiondetails/submissionSummary")
        .addObject("backLinkUrl", ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)));
  }
}
