package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/opportunity-details")
public class OpportunityDetailsController {

  public static final String PAGE_NAME = "Opportunity details";
  private final OpportunityDetailsFormService opportunityDetailsFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;

  @Autowired
  public OpportunityDetailsController(OpportunityDetailsFormService opportunityDetailsFormService,
                                      ControllerHelperService controllerHelperService,
                                      SubmissionDetailService submissionDetailService,
                                      OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                      SubmissionTaskListRedirectionService submissionTaskListRedirectionService) {
    this.opportunityDetailsFormService = opportunityDetailsFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
  }

  @GetMapping
  public ModelAndView getOpportunityDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") OpportunityDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getOpportunityDetailsModelAndView(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail),
        submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveOpportunityDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @ModelAttribute("form") OpportunityDetailsForm form,
                                             BindingResult bindingResult, ValidationType validationType) {
    bindingResult = opportunityDetailsFormService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getOpportunityDetailsModelAndView(form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          opportunityDetailsPersistenceService.saveOpportunityDetails(form, submissionDetail);
          return submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail);
        });
  }

  private ModelAndView getOpportunityDetailsModelAndView(OpportunityDetailsForm form, UUID submissionDetailId) {
    ModelAndView modelAndView = new ModelAndView("remi/submissiondetails/opportunityDetails")
        .addObject("opportunityTypes", OpportunityType.getOpportunityType())
        .addObject("form", form)
        .addObject("pageTitle", PAGE_NAME);

    return ProspectTaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
  }
}
