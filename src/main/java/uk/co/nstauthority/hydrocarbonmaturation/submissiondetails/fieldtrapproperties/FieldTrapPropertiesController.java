package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/field-trap-properties")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FIELD_SUBMISSIONS)
@IsFromResponsibleTeam
public class FieldTrapPropertiesController {

  public static final String TRAP_PROPERTIES = "Trap properties";

  private final FieldTrapPropertiesFormService fieldTrapPropertiesFormService;
  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final FieldTrapPropertiesFormValidator fieldTrapPropertiesFormValidator;

  @Autowired
  public FieldTrapPropertiesController(FieldTrapPropertiesFormService fieldTrapPropertiesFormService,
                                       SubmissionDetailService submissionDetailService,
                                       ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                       FieldTrapPropertiesFormValidator fieldTrapPropertiesFormValidator) {
    this.fieldTrapPropertiesFormService = fieldTrapPropertiesFormService;
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.fieldTrapPropertiesFormValidator = fieldTrapPropertiesFormValidator;
  }

  @GetMapping
  public ModelAndView getFieldTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    return getFieldTrapPropertiesModelAndView(
        fieldTrapPropertiesFormService.submissionDetailToForm(submissionDetail),
        submissionDetail
    );
  }

  @PostMapping
  public ModelAndView saveFieldTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @ModelAttribute("form") FieldTrapPropertiesForm form,
                                              BindingResult bindingResult,
                                              ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (!fieldTrapPropertiesFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getFieldTrapPropertiesModelAndViewWithErrors(bindingResult, form, submissionDetail);
    } else {
      fieldTrapPropertiesFormService.saveFieldTrapProperties(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    }
  }

  private ModelAndView getFieldTrapPropertiesModelAndView(FieldTrapPropertiesForm form,
                                                          SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/fieldTrapProperties")
        .addObject("form", form)
        .addObject("pageTitle", TRAP_PROPERTIES)
        .addObject("trapTypes", TrapType.getTrapTypes())

        .addObject("depthToCrestTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD))
        .addObject("depthToCrestError", ProbabilityDistributionTypeError.emptyError())

        .addObject("depthToSpillPointTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD))
        .addObject("depthToSpillPointError", ProbabilityDistributionTypeError.emptyError())

        .addObject("productiveAreaTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD))
        .addObject("productiveAreaError", ProbabilityDistributionTypeError.emptyError());

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, TRAP_PROPERTIES, submissionDetail);

    return modelAndView;
  }

  private ModelAndView getFieldTrapPropertiesModelAndViewWithErrors(BindingResult bindingResult,
                                                                    FieldTrapPropertiesForm form,
                                                                    SubmissionDetail submissionDetail) {
    return getFieldTrapPropertiesModelAndView(form, submissionDetail)
        .addObject("depthToCrestError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToCrest",
                form.getDepthToCrest()
            ))
        .addObject("depthToSpillPointError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToSpillPointError",
                form.getDepthToSpillPoint()
            ))
        .addObject("productiveAreaError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "productiveArea",
                form.getProductiveArea()
            ));
  }
}
