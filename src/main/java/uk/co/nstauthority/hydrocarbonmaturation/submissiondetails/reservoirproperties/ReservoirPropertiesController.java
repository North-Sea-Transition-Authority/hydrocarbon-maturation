package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

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

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/reservoir-properties")
public class ReservoirPropertiesController {

  private final ControllerHelperService controllerHelperService;
  private final ReservoirPropertiesFormService reservoirPropertiesFormService;

  static final String PAGE_NAME = "Reservoir details";

  @Autowired
  public ReservoirPropertiesController(ControllerHelperService controllerHelperService,
                                       ReservoirPropertiesFormService reservoirPropertiesFormService) {
    this.controllerHelperService = controllerHelperService;
    this.reservoirPropertiesFormService = reservoirPropertiesFormService;
  }

  @GetMapping
  public ModelAndView getReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    return getReservoirPropertiesModelAndView(new ReservoirPropertiesForm(), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @ModelAttribute("form") ReservoirPropertiesForm form, BindingResult bindingResult) {

    bindingResult = reservoirPropertiesFormService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getReservoirPropertiesModelAndView(form, submissionDetailId),
        () -> ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId))
    );
  }

  private ModelAndView getReservoirPropertiesModelAndView(ReservoirPropertiesForm form, UUID submissionDetailId) {
    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(ProspectTaskListBreadcrumbUtil.getProspectTaskListBreadcrumb(submissionDetailId))
        .build();

    var modelAndView = new ModelAndView("hm/submissiondetails/reservoirProperties")
        .addObject("form", form);

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}
