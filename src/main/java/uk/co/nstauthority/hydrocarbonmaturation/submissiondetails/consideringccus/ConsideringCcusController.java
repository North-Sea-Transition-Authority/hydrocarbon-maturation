package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/considering-ccus")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FIELD_SUBMISSIONS)
@IsFromResponsibleTeam
public class ConsideringCcusController {

  public static final String PAGE_NAME = "Considering CCUS";

  private final SubmissionDetailService submissionDetailService;
  private final ConsideringCcusFormService consideringCcusFormService;
  private final ConsideringCcusFormValidator consideringCcusFormValidator;

  @Autowired
  public ConsideringCcusController(SubmissionDetailService submissionDetailService,
                                   ConsideringCcusFormService consideringCcusFormService,
                                   ConsideringCcusFormValidator consideringCcusFormValidator) {
    this.submissionDetailService = submissionDetailService;
    this.consideringCcusFormService = consideringCcusFormService;
    this.consideringCcusFormValidator = consideringCcusFormValidator;
  }

  @GetMapping
  public ModelAndView getConsideringCcus(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    return getConsideringCcusModelAndView(
        consideringCcusFormService.submissionDetailToForm(submissionDetail),
        submissionDetail
    );
  }

  @PostMapping
  public ModelAndView saveConsideringCcus(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @ModelAttribute("form") ConsideringCcusForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (consideringCcusFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      consideringCcusFormService.saveConsideringCcus(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    } else {
      return getConsideringCcusModelAndView(form, submissionDetail);
    }
  }

  private ModelAndView getConsideringCcusModelAndView(ConsideringCcusForm form,
                                                      SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/consideringCcus")
        .addObject("form", form)
        .addObject("pageTitle", PAGE_NAME);

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }
}
