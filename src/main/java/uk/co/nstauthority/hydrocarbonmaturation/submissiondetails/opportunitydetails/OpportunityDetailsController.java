package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/general-details")
public class OpportunityDetailsController {

  private final OpportunityDetailsService opportunityDetailsService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public OpportunityDetailsController(OpportunityDetailsService opportunityDetailsService,
                                      ControllerHelperService controllerHelperService,
                                      SubmissionDetailService submissionDetailService) {
    this.opportunityDetailsService = opportunityDetailsService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getOpportunityDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") OpportunityDetailsForm form) {
    return getOpportunityDetailsModelAndView(new OpportunityDetailsForm(), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveOpportunityDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @ModelAttribute("form") OpportunityDetailsForm form,
                                             BindingResult bindingResult) {
    bindingResult = opportunityDetailsService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getOpportunityDetailsModelAndView(form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          opportunityDetailsService.saveOpportunityDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId));
        });
  }

  private ModelAndView getOpportunityDetailsModelAndView(OpportunityDetailsForm form, UUID submissionDetailId) {
    return new ModelAndView("hm/submissiondetails/opportunityDetails")
        .addObject("opportunityTypes", OpportunityType.getOpportunityType())
        .addObject("form", form)
        .addObject("actionUrl", "#")
        .addObject("backLinkUrl", ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)));
  }
}
