package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

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
@RequestMapping("/submission-details/{submissionDetailId}/additional-information")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.NONE)
@IsFromResponsibleTeam
public class AdditionalInformationController {

  private final AdditionalInformationFormService additionalInformationFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final AdditionalInformationPersistenceService additionalInformationPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  public static final String PAGE_NAME = "Additional information";

  @Autowired
  public AdditionalInformationController(AdditionalInformationFormService additionalInformationFormService,
                                         ControllerHelperService controllerHelperService,
                                         SubmissionDetailService submissionDetailService,
                                         AdditionalInformationPersistenceService additionalInformationPersistenceService,
                                         SubmissionSupportingDocumentService supportingDocumentService) {
    this.additionalInformationFormService = additionalInformationFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.additionalInformationPersistenceService = additionalInformationPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  @GetMapping
  public ModelAndView getAdditionalInformation(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @ModelAttribute("form") AdditionalInformationForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var additionalInformationForm = additionalInformationFormService.getAdditionalInformationForm(submissionDetail);
    return getAdditionalInformationModelAndView(additionalInformationForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveAdditionalInformation(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                @ModelAttribute("form") AdditionalInformationForm form,
                                                BindingResult bindingResult,
                                                ValidationType validationType,
                                                User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = additionalInformationFormService.validate(form, bindingResult, validationType, submissionDetail, user);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAdditionalInformationModelAndView(form, submissionDetail),
        () -> {
          additionalInformationPersistenceService.saveAdditionalInformation(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId, null));
        }
    );
  }

  private ModelAndView getAdditionalInformationModelAndView(AdditionalInformationForm form,
                                                            SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/additionalInformation")
        .addObject("form", form)
        .addObject("pageTitle", PAGE_NAME)
        .addObject("additionalDocumentTemplate",
            supportingDocumentService.submissionFileUploadComponentAttributes(
                form.getAdditionalDocuments(),
                FileDocumentType.ADDITIONAL_DOCUMENT,
                submissionDetail
            ));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }
}
