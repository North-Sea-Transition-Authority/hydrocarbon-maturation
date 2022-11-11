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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/summary")
public class SubmissionSummaryController {

  private final ProspectSummaryService prospectSummaryService;
  private final SubmissionDetailService submissionDetailService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;

  public static final String PAGE_NAME = "Check your answers before submitting";

  @Autowired
  public SubmissionSummaryController(ProspectSummaryService prospectSummaryService,
                                     SubmissionDetailService submissionDetailService,
                                     HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                     SubmissionTaskListRedirectionService submissionTaskListRedirectionService) {
    this.prospectSummaryService = prospectSummaryService;
    this.submissionDetailService = submissionDetailService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
  }

  @GetMapping
  public ModelAndView showSubmissionSummary(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    return getSubmissionSummaryModelAndView(submissionDetail);
  }

  @PostMapping
  public ModelAndView processSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    submissionDetailService.completeSubmission(submissionDetail);
    return ReverseRouter.redirect(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId));
  }

  private ModelAndView getSubmissionSummaryModelAndView(SubmissionDetail submissionDetail) {
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
        .addObject("trapPropertiesSummaryView",
            prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilCaseVolumetricsSummaryView",
            prospectSummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilCaseOutputsSummaryView",
            prospectSummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("gasCaseVolumetricsSummaryView",
            prospectSummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("gasCaseOutputsSummaryView",
            prospectSummaryService.getGasCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("mixedCaseVolumetricsSummaryView",
            prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("mixedCaseOutputsSummaryView",
            prospectSummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("opportunityDetailsSummaryView",
            prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("additionalInformationSummaryView",
            prospectSummaryService.getAdditionalInformationSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("gasCase", hydrocarbonCasePersistenceService.isGasOnlyPresent(submissionDetail))
        .addObject("oilCase", hydrocarbonCasePersistenceService.isOilOnlyPresent(submissionDetail))
        .addObject("mixedCase", hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(submissionDetail))
        .addObject("cancelUrl", submissionTaskListRedirectionService.getTaskListUrl(submissionDetail))
        .addObject("subNavigation", prospectSummaryService.getProspectSubNavigation(submissionDetail));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }
}
