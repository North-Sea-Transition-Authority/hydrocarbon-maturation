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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/general-details")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class GeneralDetailsController {

  private final GeneralDetailsFormService generalDetailsFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;

  public static final String PAGE_NAME = "General details";

  @Autowired
  public GeneralDetailsController(GeneralDetailsFormService generalDetailsFormService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService,
                                  GeneralDetailsPersistenceService generalDetailsPersistenceService) {
    this.generalDetailsFormService = generalDetailsFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
  }

  @GetMapping
  public ModelAndView getGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") GeneralDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getGeneralDetailsModelAndView(generalDetailsFormService.getGeneralDetailsForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") GeneralDetailsForm form, BindingResult bindingResult,
                                         ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = generalDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getGeneralDetailsModelAndView(form, submissionDetail),
        () -> {
          generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getGeneralDetailsModelAndView(GeneralDetailsForm form, SubmissionDetail submissionDetail) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/generalDetails")
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("licenceApplicationEndpoint",
            SearchSelectorService.route(on(LicenceApplicationRestController.class)
                .searchLicenceApplicationsWithManualEntry(null)))
        .addObject("discoverySubmission", submissionDetail.isDiscoverySubmission())
        .addObject("prospectSubmission", submissionDetail.isProspectSubmission())
        .addObject("relinquishmentSubmission",
            submissionDetail.isDiscoveryRelinquishment() || submissionDetail.isProspectRelinquishment())
        .addObject("licenceApplication",
            generalDetailsFormService.getPreSelectedLicenceApplication(form.getLicenceApplication()))
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null)))
        .addObject("resourceType",
            submissionDetail.getResourceId().getType().getDisplayName().toLowerCase())
        .addObject("responsibleOrganisation", generalDetailsFormService
            .getPreSelectedOrganisation(form.getResponsibleOrganisationId()))
        .addObject("quadrantBlocksApiEndpoint",
            SearchSelectorService.route(on(ReferenceBlockRestController.class).searchQuadrantBlocks(null)))
        .addObject("alreadyAddedQuadrantBlocks", generalDetailsFormService.getQuadrantAndBlockViews(form))
        .addObject("form", form)
        .addObject("licenceEndpoint",
            SearchSelectorService.route(on(LicenceRestController.class).searchExtantLicences(null)))
        .addObject("alreadyAddedLicences", generalDetailsFormService.getLicenceViews(
            form));

    if (submissionDetail.isProspectSubmission()) {
      modelAndView.addObject("informationSources", InformationSource.getInformationSource());
    } else if (submissionDetail.isDiscoverySubmission()) {
      modelAndView.addObject("informationSources", InformationSource.getDiscoveryInformationSource());
    }

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }
}
