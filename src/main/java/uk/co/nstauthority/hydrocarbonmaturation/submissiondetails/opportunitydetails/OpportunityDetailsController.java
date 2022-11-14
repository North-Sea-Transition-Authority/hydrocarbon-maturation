package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/opportunity-details")
public class OpportunityDetailsController {

  public static final String PAGE_NAME = "Opportunity details";
  private final OpportunityDetailsFormService opportunityDetailsFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public OpportunityDetailsController(OpportunityDetailsFormService opportunityDetailsFormService,
                                      ControllerHelperService controllerHelperService,
                                      SubmissionDetailService submissionDetailService,
                                      OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                      SupportingDocumentService supportingDocumentService) {
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
    var mapOfOpportunityUploads = opportunityDetailsFormService.getMapOfOpportunityUploads(opportunityDetailsForm);
    return getOpportunityDetailsModelAndView(opportunityDetailsForm, submissionDetail, mapOfOpportunityUploads);
  }

  @PostMapping
  public ModelAndView saveOpportunityDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @ModelAttribute("form") OpportunityDetailsForm form,
                                             BindingResult bindingResult, ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var mapOfOpportunityUploads = opportunityDetailsFormService.getMapOfOpportunityUploads(form);

    bindingResult = opportunityDetailsFormService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getOpportunityDetailsModelAndView(form, submissionDetail, mapOfOpportunityUploads),
        () -> {
          opportunityDetailsPersistenceService.saveOpportunityDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        });
  }

  private ModelAndView getOpportunityDetailsModelAndView(OpportunityDetailsForm form, SubmissionDetail submissionDetail,
                                                         List<UploadedFileView> mapOfOpportunityUploads) {
    ModelAndView modelAndView = new ModelAndView("remi/submissiondetails/opportunityDetails")
        .addObject("opportunityTypes", OpportunityType.getOpportunityType())
        .addObject("form", form)
        .addObject("pageTitle", PAGE_NAME)
        .addObject("mapOfOpportunityTemplate", supportingDocumentService.buildFileUploadTemplate(
            submissionDetail.getId(),
            SupportingDocumentType.MAP_OF_OPPORTUNITY))
        .addObject("mapOfOpportunityUploads", mapOfOpportunityUploads);

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
