package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/trap-properties")
public class TrapPropertiesController {

  private final TrapPropertiesFormService trapPropertiesFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;
  private final ProbabilityDistributionService probabilityDistributionService;

  public static final String PAGE_NAME = "Trap properties";

  @Autowired
  public TrapPropertiesController(TrapPropertiesFormService trapPropertiesFormService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService,
                                  TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                                  SubmissionTaskListRedirectionService submissionTaskListRedirectionService,
                                  ProbabilityDistributionService probabilityDistributionService) {
    this.trapPropertiesFormService = trapPropertiesFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
    this.probabilityDistributionService = probabilityDistributionService;
  }

  @GetMapping
  public ModelAndView getTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getTrapPropertiesModelAndView(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") TrapPropertiesForm form, BindingResult bindingResult,
                                         ValidationType validationType) {

    bindingResult = trapPropertiesFormService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
      bindingResult,
      getTrapPropertiesModelAndView(form, submissionDetailId),
      () -> {
        var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
        trapPropertiesPersistenceService.saveTrapProperties(form, submissionDetail);
        return submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail);
      }
    );
  }

  private ModelAndView getTrapPropertiesModelAndView(TrapPropertiesForm form, UUID submissionDetailId) {

    var modelAndView = new ModelAndView("remi/submissiondetails/trapProperties")
        .addObject("form", form)
        .addObject("trapTypes", TrapType.getTrapTypes())
        .addObject("longitudeHemisphere", Hemisphere.getLongitudeHemisphere())
        .addObject("depthToCrestTemplate",
            probabilityDistributionService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT_DISCOVERY
            ))
        .addObject("depthToSpillPointTemplate",
            probabilityDistributionService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY
            ))
        .addObject("productiveAreaTemplate",
            probabilityDistributionService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA
            ));

    ProspectTaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
    return modelAndView;
  }
}
