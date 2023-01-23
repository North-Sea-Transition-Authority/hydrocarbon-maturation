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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/source-and-seal")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class SourceAndSealController {

  private final SourceAndSealFormService sourceAndSealFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final SourceAndSealPersistenceService sourceAndSealPersistenceService;

  public static final String PAGE_NAME = "Source and seal";

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
    return getSourceAndSealModelAndView(sourceAndSealFormService.getSourceAndSealForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveSourceAndSealDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @ModelAttribute("form") SourceAndSealForm form, BindingResult bindingResult,
                                               ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = sourceAndSealFormService.validate(form, bindingResult, validationType, submissionDetail);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSourceAndSealModelAndView(form, submissionDetail),
        () -> {
          sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getSourceAndSealModelAndView(SourceAndSealForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/sourceAndSeal")
        .addObject("form", form);

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
