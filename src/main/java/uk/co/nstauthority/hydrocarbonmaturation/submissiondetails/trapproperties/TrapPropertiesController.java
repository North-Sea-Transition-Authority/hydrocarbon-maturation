package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/trap-properties")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class TrapPropertiesController {

  private final TrapPropertiesFormService trapPropertiesFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final QuadrantAndBlockService quadrantAndBlockService;

  public static final String PAGE_NAME = "Trap properties";

  @Autowired
  public TrapPropertiesController(TrapPropertiesFormService trapPropertiesFormService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService,
                                  TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                                  ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                  SubmissionSupportingDocumentService supportingDocumentService,
                                  QuadrantAndBlockService quadrantAndBlockService) {
    this.trapPropertiesFormService = trapPropertiesFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.supportingDocumentService = supportingDocumentService;
    this.quadrantAndBlockService = quadrantAndBlockService;
  }

  @GetMapping
  public ModelAndView getTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var trapPropertiesForm = trapPropertiesFormService.getTrapPropertiesForm(submissionDetail);

    return getTrapPropertiesModelAndView(trapPropertiesForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveTrapProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") TrapPropertiesForm form, BindingResult bindingResult,
                                         ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = trapPropertiesFormService.validate(form, bindingResult, validationType, submissionDetail);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getTrapPropertiesWithDistributionErrors(bindingResult, form, submissionDetail),
        () -> {
          trapPropertiesPersistenceService.saveTrapProperties(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getTrapPropertiesModelAndView(TrapPropertiesForm form, SubmissionDetail submissionDetail) {

    var modelAndView = new ModelAndView("remi/submissiondetails/trapProperties")
        .addObject("form", form)
        .addObject("trapTypes", TrapType.getTrapTypes())
        .addObject("datumTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirDatum.class))
        .addObject("quadrantAndBlocks", quadrantAndBlockService.getQuadrantAndBlocks(submissionDetail))
        .addObject("longitudeHemisphere", Hemisphere.getLongitudeHemisphere())
        .addObject("depthToCrestError", ProbabilityDistributionTypeError.emptyError())
        .addObject("depthToSpillPointError", ProbabilityDistributionTypeError.emptyError())
        .addObject("productiveAreaError", ProbabilityDistributionTypeError.emptyError())
        .addObject("prospectDepthMapTemplate",
            supportingDocumentService.submissionFileUploadComponentAttributes(
                form.getProspectDepthMap(), FileDocumentType.PROSPECT_DEPTH_MAP, submissionDetail));

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      modelAndView
          .addObject("depthToCrestTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT
              ))
          .addObject("depthToSpillPointTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT
              ))
          .addObject("productiveAreaTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT
              ));
    } else if (resourceType == ResourceType.DISCOVERY) {
      modelAndView
          .addObject("depthToCrestTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_DISCOVERY
              ))
          .addObject("depthToSpillPointTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_DISCOVERY
              ))
          .addObject("productiveAreaTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_DISCOVERY
              ));
    }

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  private ModelAndView getTrapPropertiesWithDistributionErrors(
      BindingResult bindingResult,
      TrapPropertiesForm form,
      SubmissionDetail submissionDetail
  ) {
    var modelAndView = getTrapPropertiesModelAndView(form, submissionDetail);
    modelAndView
        .addObject("depthToCrestError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
              bindingResult,
              "depthToCrest",
              form.getDepthToCrest()))
        .addObject("depthToSpillPointError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToSpillPoint",
                form.getDepthToSpillPoint()))
        .addObject("productiveAreaError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "productiveArea",
                form.getProductiveArea()));

    return modelAndView;
  }
}
