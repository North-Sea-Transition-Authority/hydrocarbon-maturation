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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/summary")
public class SubmissionSummaryController {

  private final ProspectSummaryService prospectSummaryService;
  private final SubmissionDetailService submissionDetailService;

  public static final String PAGE_NAME = "Check your answers before submitting";

  @Autowired
  public SubmissionSummaryController(ProspectSummaryService prospectSummaryService,
                                     SubmissionDetailService submissionDetailService) {
    this.prospectSummaryService = prospectSummaryService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView showSubmissionSummary(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    return getSubmissionSummaryModelAndView(submissionDetailId);
  }

  @PostMapping
  public ModelAndView processSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    submissionDetailService.completeSubmission(submissionDetail);
    return ReverseRouter.redirect(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId));
  }

  private ModelAndView getSubmissionSummaryModelAndView(UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var modelAndView = new ModelAndView("remi/submissiondetails/submissionSummary")
        .addObject("generalDetailsSummaryView",
            prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("sourceAndSealSummaryView",
            prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("chanceOfSuccessSummaryView",
            prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("hydrocarbonCaseSummaryView",
            prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("reservoirPropertiesSummaryView",
            prospectSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("cancelUrl", ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)))
        .addObject("subNavigation", prospectSummaryService.getProspectSubNavigation(submissionDetail));

    ProspectTaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);

    return modelAndView;
  }
}
