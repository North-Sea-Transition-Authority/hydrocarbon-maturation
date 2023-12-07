package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

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
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/field-complexity")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FIELD_SUBMISSIONS)
@IsFromResponsibleTeam
public class FieldComplexityController {

  public static final String FIELD_COMPLEXITY = "Field complexity";

  private final SubmissionDetailService submissionDetailService;
  private final FieldComplexityService fieldComplexityService;
  private final FieldComplexityFormValidator fieldComplexityFormValidator;

  @Autowired
  public FieldComplexityController(FieldComplexityService fieldComplexityService,
                                   SubmissionDetailService submissionDetailService,
                                   FieldComplexityFormValidator fieldComplexityFormValidator) {
    this.fieldComplexityService = fieldComplexityService;
    this.submissionDetailService = submissionDetailService;
    this.fieldComplexityFormValidator = fieldComplexityFormValidator;
  }

  @GetMapping
  public ModelAndView getFieldComplexity(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    return getFieldComplexityModelAndView(
      fieldComplexityService.submissionDetailToForm(submissionDetail),
      submissionDetail
    );
  }

  @PostMapping
  public ModelAndView saveFieldComplexity(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @ModelAttribute("form") FieldComplexityForm form,
                                              BindingResult bindingResult,
                                              ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (fieldComplexityFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      fieldComplexityService.saveFieldComplexity(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    } else {
      return getFieldComplexityModelAndView(form, submissionDetail);
    }
  }

  private ModelAndView getFieldComplexityModelAndView(FieldComplexityForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/fieldComplexity")
        .addObject("form", form)
        .addObject("pageTitle", FIELD_COMPLEXITY)
        .addObject("viscosityRangeMap",
            DisplayableEnumOptionUtil.getDisplayableOptions(ViscosityRange.class))
        .addObject("verticalHeterogeneityRangeMap",
            DisplayableEnumOptionUtil.getDisplayableOptions(VerticalHeterogeneityRange.class))
        .addObject("arealDensityRangeMap",
            DisplayableEnumOptionUtil.getDisplayableOptions(ArealDensityRange.class))
        .addObject("structuralComplexityRangeMap",
            DisplayableEnumOptionUtil.getDisplayableOptions(StructuralComplexityRange.class));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, FIELD_COMPLEXITY, submissionDetail);

    return modelAndView;
  }
}
