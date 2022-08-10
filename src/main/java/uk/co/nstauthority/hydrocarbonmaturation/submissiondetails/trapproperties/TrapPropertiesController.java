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
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/trap-properties")
public class TrapPropertiesController {

  private final TrapPropertiesService trapPropertiesService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;

  static final String PAGE_NAME = "Trap properties";

  @Autowired
  public TrapPropertiesController(TrapPropertiesService trapPropertiesService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService) {
    this.trapPropertiesService = trapPropertiesService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    return getTrapPropertiesModelAndView(new TrapPropertiesForm(), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") TrapPropertiesForm form, BindingResult bindingResult) {

    bindingResult = trapPropertiesService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
      bindingResult,
      getTrapPropertiesModelAndView(form, submissionDetailId),
      () -> {
        var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
        trapPropertiesService.saveTrapProperties(form, submissionDetail);
        return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId));
      }
    );
  }

  private ModelAndView getTrapPropertiesModelAndView(TrapPropertiesForm form, UUID submissionDetailId) {
    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(ProspectTaskListBreadcrumbUtil.getProspectTaskListBreadcrumb(submissionDetailId))
        .build();

    var modelAndView = new ModelAndView("hm/submissiondetails/trapProperties")
        .addObject("form", form)
        .addObject("trapTypes", TrapType.getTrapTypes())
        .addObject("longitudeHemisphere", Hemisphere.getLongitudeHemisphere())
        .addObject("actionUrl", ReverseRouter.route(on(TrapPropertiesController.class)
            .saveTrapProperties(submissionDetailId, null, null)));

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}
