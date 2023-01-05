package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
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
@RequestMapping("/submission-details/{submissionDetailId}/drill-stem-test")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class DrillStemTestController {

  private final DrillStemTestFormService drillStemTestFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  public static final String PAGE_NAME = "Drill stem test results";

  @Autowired
  public DrillStemTestController(DrillStemTestFormService drillStemTestFormService,
                                 ControllerHelperService controllerHelperService,
                                 SubmissionDetailService submissionDetailService,
                                 DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService) {
    this.drillStemTestFormService = drillStemTestFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
  }

  @GetMapping
  public ModelAndView getDrillStemTestPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                           @ModelAttribute("form") DrillStemTestInitialForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getDrillStemTestModelAndView(
        drillStemTestFormService.getDrillStemTestInitialForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveDrillStemTestDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @ModelAttribute("form") DrillStemTestInitialForm form,
                                               BindingResult bindingResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = drillStemTestFormService.validate(form, bindingResult, ValidationType.FULL);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDrillStemTestModelAndView(form, submissionDetail),
        () -> {
          drillStemTestInitialPersistenceService.saveInitialDrillStemTest(form, submissionDetail);
          if (BooleanUtils.isTrue(form.getHasDrillStemTestResult())) {
            return ReverseRouter.redirect(on(DrillStemTestResultController.class)
                .getCreateNewDrillStemTestResultPage(submissionDetailId));
          }
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getDrillStemTestModelAndView(DrillStemTestInitialForm form, SubmissionDetail submissionDetail) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/drillstemtest/drillStemTestInitial")
        .addObject("form", form)
        .addObject("actionUrl",
            ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetail.getId(), null, null)));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(
        modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }
}
