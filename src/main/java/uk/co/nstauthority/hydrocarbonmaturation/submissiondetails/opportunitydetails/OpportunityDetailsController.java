package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/prospect")
public class OpportunityDetailsController {

  private final OpportunityDetailsService opportunityDetailsService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public OpportunityDetailsController(OpportunityDetailsService opportunityDetailsService,
                                      ControllerHelperService controllerHelperService) {
    this.opportunityDetailsService = opportunityDetailsService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping("/opportunity-details")
  public ModelAndView getOpportunityDetailsPage() {
    return opportunityDetailsService.getOpportunityDetailsModelAndView(new OpportunityDetailsForm());
  }

  @PostMapping("/opportunity-details")
  public ModelAndView saveOpportunityDetailsPage(@ModelAttribute("form") OpportunityDetailsForm form,
                                                 BindingResult bindingResult) {
    bindingResult = opportunityDetailsService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        opportunityDetailsService.getOpportunityDetailsModelAndView(form),
        () -> {
          opportunityDetailsService.saveOpportunityDetails(form);
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea()); //TODO redirect to task list
          // TODO add flash message
        });
  }
}
