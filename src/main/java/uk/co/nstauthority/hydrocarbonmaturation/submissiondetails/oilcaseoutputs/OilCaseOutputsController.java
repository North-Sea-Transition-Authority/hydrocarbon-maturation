package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.UUID;
import javax.validation.Valid;
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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/oil-case-outputs")
public class OilCaseOutputsController {

  private final SubmissionDetailService submissionDetailService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final OilCaseOutputsFormService oilCaseOutputsFormService;
  private final OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  public static final String PAGE_NAME = "Oil case outputs (resources)";

  @Autowired
  public OilCaseOutputsController(SubmissionDetailService submissionDetailService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionTaskListRedirectionService submissionTaskListRedirectionService,
                                  ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                  OilCaseOutputsFormService oilCaseOutputsFormService,
                                  OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.controllerHelperService = controllerHelperService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.oilCaseOutputsFormService = oilCaseOutputsFormService;
    this.oilCaseOutputsPersistenceService = oilCaseOutputsPersistenceService;
  }

  @GetMapping
  public ModelAndView getOilCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") OilCaseOutputsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getOilCaseOutputsModelAndView(
        oilCaseOutputsFormService.getOilCaseOutputsForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveOilCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @Valid @ModelAttribute("form") OilCaseOutputsForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType) {
    bindingResult = oilCaseOutputsFormService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getOilOutputsModelAndViewWithErrors(bindingResult, form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, submissionDetail);
          return submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail);
        }
    );
  }

  private ModelAndView getOilCaseOutputsModelAndView(OilCaseOutputsForm form, UUID submissionDetailId) {
    var modelAndView = new ModelAndView("remi/submissiondetails/oilCaseOutputs")
        .addObject("form", form)
        .addObject("oilInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_IN_PLACE))
        .addObject("oilRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES))
        .addObject("associatedGasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_RECOVERABLE_RESOURCES))
        .addObject("oilEquivalentRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES))
        .addObject("oilInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError());
    ProspectTaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
    return modelAndView;
  }

  private ModelAndView getOilOutputsModelAndViewWithErrors(BindingResult bindingResult,
                                                           OilCaseOutputsForm form,
                                                           UUID submissionDetailId) {
    var modelAndView = getOilCaseOutputsModelAndView(form, submissionDetailId);

    return modelAndView
        .addObject("oilInPlaceError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilInPlace",
                form.getOilInPlace()))
        .addObject("oilRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilRecoverableResources",
                form.getOilRecoverableResources()))
        .addObject("associatedGasRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "associatedGasRecoverableResources",
                form.getAssociatedGasRecoverableResources()))
        .addObject("oilEquivalentRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilEquivalentRecoverableResources",
                form.getOilEquivalentRecoverableResources()));
  }
}
