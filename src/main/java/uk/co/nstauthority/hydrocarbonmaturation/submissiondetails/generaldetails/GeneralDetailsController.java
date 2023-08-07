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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceRestController;
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
@IsFromResponsibleTeam
public class GeneralDetailsController {

  private final GeneralDetailsFormService generalDetailsFormService;
  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final GeneralDetailsFormValidator generalDetailsFormValidator;
  private final OnshoreOffshoreService onshoreOffshoreService;

  public static final String PAGE_NAME = "General details";

  @Autowired
  public GeneralDetailsController(GeneralDetailsFormService generalDetailsFormService,
                                  SubmissionDetailService submissionDetailService,
                                  GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                  GeneralDetailsFormValidator generalDetailsFormValidator,
                                  OnshoreOffshoreService onshoreOffshoreService) {
    this.generalDetailsFormService = generalDetailsFormService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.generalDetailsFormValidator = generalDetailsFormValidator;
    this.onshoreOffshoreService = onshoreOffshoreService;
  }

  @GetMapping
  public ModelAndView getGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") GeneralDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var onshoreOffshore = onshoreOffshoreService.getOnshoreOffshore(submissionDetail);
    return getGeneralDetailsModelAndView(
        generalDetailsFormService.getGeneralDetailsForm(submissionDetail), submissionDetail, onshoreOffshore);
  }

  @PostMapping
  public ModelAndView saveGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") GeneralDetailsForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var onshoreOffshore = onshoreOffshoreService.getOnshoreOffshore(submissionDetail);
    if (!generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getGeneralDetailsModelAndView(form, submissionDetail, onshoreOffshore);
    } else {
      generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, onshoreOffshore);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    }
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
    var onshoreOffshore = onshoreOffshoreService.getOnshoreOffshore(submissionDetail);
    return onshoreOffshore == OnshoreOffshore.ONSHORE
        ? SearchSelectorService.route(on(ReferenceBlockRestController.class).searchOnshoreQuadrantBlocks(null))
        : SearchSelectorService.route(on(ReferenceBlockRestController.class).searchOffshoreQuadrantBlocks(null));
  }
}
