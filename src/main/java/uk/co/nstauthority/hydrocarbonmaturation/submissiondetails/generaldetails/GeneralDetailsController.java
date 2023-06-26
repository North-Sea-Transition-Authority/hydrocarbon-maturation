package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/general-details")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class GeneralDetailsController {

  private final GeneralDetailsFormService generalDetailsFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  public static final String PAGE_NAME = "General details";

  @Autowired
  public GeneralDetailsController(GeneralDetailsFormService generalDetailsFormService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService,
                                  GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                  ResourceDetailsPersistenceService resourceDetailsPersistenceService) {
    this.generalDetailsFormService = generalDetailsFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
  }

  @GetMapping
  public ModelAndView getGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") GeneralDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);
    return getGeneralDetailsModelAndView(generalDetailsFormService.getGeneralDetailsForm(submissionDetail), submissionDetail,
        resourceDetail.getOnshoreOffshore());
  }

  @PostMapping
  public ModelAndView saveGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") GeneralDetailsForm form, BindingResult bindingResult,
                                         ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = generalDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getGeneralDetailsModelAndView(form, submissionDetail, resourceDetail.getOnshoreOffshore()),
        () -> {
          generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, resourceDetail.getOnshoreOffshore());
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getGeneralDetailsModelAndView(GeneralDetailsForm form, SubmissionDetail submissionDetail,
                                                     OnshoreOffshore onshoreOffshore) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/generalDetails")
        .addObject("licenceApplicationEndpoint",
            SearchSelectorService.route(on(LicenceApplicationRestController.class)
                .searchLicenceApplicationsWithManualEntry(null)))
        .addObject("discoverySubmission", submissionDetail.isDiscoverySubmission())
        .addObject("prospectSubmission", submissionDetail.isProspectSubmission())
        .addObject("relinquishmentSubmission",
            submissionDetail.isDiscoveryRelinquishment() || submissionDetail.isProspectRelinquishment())
        .addObject("licenceApplication",
            generalDetailsFormService.getPreSelectedLicenceApplication(form.getLicenceApplication()))
        .addObject("resourceType",
            submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase())
        .addObject("quadrantBlocksApiEndpoint", getQuadrantBlocksApiEndpoint(submissionDetail))
        .addObject("alreadyAddedQuadrantBlocks", generalDetailsFormService.getQuadrantAndBlockViews(form))
        .addObject("form", form)
        .addObject("licenceEndpoint",
            SearchSelectorService.route(on(LicenceRestController.class).searchExtantLicences(null)))
        .addObject("alreadyAddedLicences", generalDetailsFormService.getLicenceViews(
            form))
        .addObject("hasLicencesOnEarlierSubmission", generalDetailsFormService
            .hasLicencesOnEarlierSubmission(submissionDetail))
        .addObject("isOffshore", onshoreOffshore == OnshoreOffshore.OFFSHORE);

    // Relinquishment information source is automatically set to relinquishment
    if (!submissionDetail.isRelinquishment()) {
      if (submissionDetail.isProspectSubmission()) {
        modelAndView.addObject("informationSources", InformationSource.getInformationSource());
      } else if (submissionDetail.isDiscoverySubmission()) {
        modelAndView.addObject("informationSources", InformationSource.getDiscoveryInformationSource());
      }
    }

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  private String getQuadrantBlocksApiEndpoint(SubmissionDetail submissionDetail) {
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);
    return resourceDetail.getOnshoreOffshore() == OnshoreOffshore.ONSHORE
        ? SearchSelectorService.route(on(ReferenceBlockRestController.class).searchOnshoreQuadrantBlocks(null))
        : SearchSelectorService.route(on(ReferenceBlockRestController.class).searchOffshoreQuadrantBlocks(null));
  }
}
