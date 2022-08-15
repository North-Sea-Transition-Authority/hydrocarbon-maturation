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

  private final SourceAndSealFormService sourceAndSealFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final SourceAndSealPersistenceService sourceAndSealPersistenceService;

  static final String PAGE_NAME = "Source and seal";

  @Autowired
  public SourceAndSealController(SourceAndSealFormService sourceAndSealFormService,
                                 ControllerHelperService controllerHelperService,
                                 SubmissionDetailService submissionDetailService,
                                 SourceAndSealPersistenceService sourceAndSealPersistenceService) {
    this.sourceAndSealFormService = sourceAndSealFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.sourceAndSealPersistenceService = sourceAndSealPersistenceService;
  }

  @GetMapping
  public ModelAndView getSourceAndSealDetails(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getSourceAndSealModelAndView(sourceAndSealFormService.getSourceAndSealForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveSourceAndSealDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @ModelAttribute("form") SourceAndSealForm form, BindingResult bindingResult) {
    bindingResult = sourceAndSealFormService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSourceAndSealModelAndView(form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getSourceAndSealModelAndView(SourceAndSealForm form, UUID submissionDetailId) {
    var modelAndView = new ModelAndView("remi/submissiondetails/sourceAndSeal")
        .addObject("form", form)
        .addObject("actionUrl", ReverseRouter.route(on(SourceAndSealController.class)
            .saveSourceAndSealDetails(submissionDetailId, null, null)));

    return ProspectTaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
  }
}
