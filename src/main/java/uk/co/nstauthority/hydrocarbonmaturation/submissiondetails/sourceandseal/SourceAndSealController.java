package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

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
@RequestMapping("/submission-details/{submissionDetailId}/source-and-seal")
public class SourceAndSealController {

  private final SourceAndSealService sourceAndSealService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;

  static final String PAGE_NAME = "Source and seal";

  @Autowired
  public SourceAndSealController(SourceAndSealService sourceAndSealService,
                                 ControllerHelperService controllerHelperService,
                                 SubmissionDetailService submissionDetailService) {
    this.sourceAndSealService = sourceAndSealService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getSourceAndSealDetails(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    return getSourceAndSealModelAndView(new SourceAndSealForm(), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveSourceAndSealDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @ModelAttribute("form") SourceAndSealForm form, BindingResult bindingResult) {
    bindingResult = sourceAndSealService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSourceAndSealModelAndView(form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          sourceAndSealService.saveSourceAndSealDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getSourceAndSealModelAndView(SourceAndSealForm form, UUID submissionDetailId) {
    var modelAndView = new ModelAndView("hm/submissiondetails/sourceAndSeal")
        .addObject("form", form)
        .addObject("actionUrl", ReverseRouter.route(on(SourceAndSealController.class)
            .saveSourceAndSealDetails(submissionDetailId, null, null)));

    return ProspectTaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
  }
}
