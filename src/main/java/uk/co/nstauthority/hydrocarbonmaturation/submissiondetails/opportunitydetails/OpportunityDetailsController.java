package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/opportunity-details")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_PROSPECT)
@IsFromResponsibleTeam
public class OpportunityDetailsController {

  public static final String PAGE_NAME = "Opportunity details";
  private final OpportunityDetailsFormService opportunityDetailsFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public OpportunityDetailsController(OpportunityDetailsFormService opportunityDetailsFormService,
                                      ControllerHelperService controllerHelperService,
                                      SubmissionDetailService submissionDetailService,
                                      OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                      SubmissionSupportingDocumentService supportingDocumentService) {
    this.opportunityDetailsFormService = opportunityDetailsFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  @GetMapping
  public ModelAndView getOpportunityDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") OpportunityDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var opportunityDetailsForm = opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail);

    return getOpportunityDetailsModelAndView(opportunityDetailsForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveOpportunityDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @ModelAttribute("form") OpportunityDetailsForm form,
                                             BindingResult bindingResult, ValidationType validationType,
                                             User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = opportunityDetailsFormService.validate(form, bindingResult, validationType, submissionDetail, user);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getOpportunityDetailsModelAndView(form, submissionDetail),
        () -> {
          opportunityDetailsPersistenceService.saveOpportunityDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId, null));
        });
  }

  private ModelAndView getOpportunityDetailsModelAndView(OpportunityDetailsForm form, SubmissionDetail submissionDetail) {
    ModelAndView modelAndView = new ModelAndView("remi/submissiondetails/opportunityDetails")
        .addObject("opportunityTypes", OpportunityType.getSelectableOpportunityTypes())
        .addObject("form", form)
        .addObject("pageTitle", PAGE_NAME)
        .addObject("mapOfOpportunityTemplate",
            supportingDocumentService.submissionFileUploadComponentAttributes(
                form.getMapOfOpportunity(), FileDocumentType.MAP_OF_OPPORTUNITY, submissionDetail))
        .addObject("prospectSubmission", submissionDetail.isProspectSubmission())
        .addObject("geologicalCrossSectionTemplate",
            supportingDocumentService.submissionFileUploadComponentAttributes(
                form.getGeologicalCrossSection(), FileDocumentType.GEOLOGICAL_CROSS_SECTION, submissionDetail))
        .addObject("representativeSeismicSectionTemplate",
            supportingDocumentService.submissionFileUploadComponentAttributes(
                form.getRepresentativeSeismicSection(), FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION, submissionDetail))
        .addObject("mapName", submissionDetail.isDiscoverySubmission() ? "Map of discovery" : "Map of opportunity");

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
