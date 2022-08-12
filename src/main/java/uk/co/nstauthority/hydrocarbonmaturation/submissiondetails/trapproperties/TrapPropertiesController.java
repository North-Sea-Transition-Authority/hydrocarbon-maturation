package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/trap-properties")
public class TrapPropertiesController {

  private final TrapPropertiesFormService trapPropertiesFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  static final String PAGE_NAME = "Trap properties";

  @Autowired
  public TrapPropertiesController(TrapPropertiesFormService trapPropertiesFormService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService,
                                  TrapPropertiesPersistenceService trapPropertiesPersistenceService) {
    this.trapPropertiesFormService = trapPropertiesFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
  }

  @GetMapping
  public ModelAndView getTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getTrapPropertiesModelAndView(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") TrapPropertiesForm form, BindingResult bindingResult) {

    bindingResult = trapPropertiesFormService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
      bindingResult,
      getTrapPropertiesModelAndView(form, submissionDetailId),
      () -> {
        var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
        trapPropertiesPersistenceService.saveTrapProperties(form, submissionDetail);
        return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId));
      }
    );
  }

  private ModelAndView getTrapPropertiesModelAndView(TrapPropertiesForm form, UUID submissionDetailId) {

    var modelAndView = new ModelAndView("hm/submissiondetails/trapProperties")
        .addObject("form", form)
        .addObject("trapTypes", TrapType.getTrapTypes())
        .addObject("longitudeHemisphere", Hemisphere.getLongitudeHemisphere())
        .addObject("actionUrl", ReverseRouter.route(on(TrapPropertiesController.class)
            .saveTrapProperties(submissionDetailId, null, null)));

    ProspectTaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
    return modelAndView;
  }
}
