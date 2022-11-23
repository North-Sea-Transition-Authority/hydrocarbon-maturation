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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/discovery-summary")
public class DiscoverySubmissionSummaryController {

  private final DiscoverySummaryService discoverySummaryService;
  private final SubmissionDetailService submissionDetailService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  public static final String PAGE_NAME = "Check your answers before submitting";

  @Autowired
  public DiscoverySubmissionSummaryController(DiscoverySummaryService discoverySummaryService,
                                              SubmissionDetailService submissionDetailService,
                                              DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService) {
    this.discoverySummaryService = discoverySummaryService;
    this.submissionDetailService = submissionDetailService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
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
    var modelAndView = new ModelAndView("remi/submissiondetails/discoverySubmissionSummary")
        .addObject("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)))
        .addObject("gasFluidPropertiesSummaryView",
            discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("generalFluidPropertiesSummaryView",
            discoverySummaryService.getGeneralFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilFluidPropertiesSummaryView",
            discoverySummaryService.getOilFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("permeabiltySummaryView",
            discoverySummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail));

    modelAndView = addHydrocarbonsDiscoveredToModelAndView(submissionDetail, modelAndView);
    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }

  private ModelAndView addHydrocarbonsDiscoveredToModelAndView(SubmissionDetail submissionDetail,
                                                               ModelAndView modelAndView) {
    return discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
        .map(discoveryWellDetail -> {
          modelAndView.addObject("gasOnlyDiscovery",
              discoveryWellDetailsPersistenceService.isDiscoveryGasOnly(discoveryWellDetail));
          modelAndView.addObject("oilOnlyDiscovery",
              discoveryWellDetailsPersistenceService.isDiscoveryOilOnly(discoveryWellDetail));
          modelAndView.addObject("mixedDiscovery",
              discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWellDetail));
          return modelAndView;
        }).orElseGet(() -> {
          modelAndView.addObject("gasOnlyDiscovery", false);
          modelAndView.addObject("oilOnlyDiscovery", false);
          modelAndView.addObject("mixedDiscovery", false);
          return modelAndView;
        });
  }
}
