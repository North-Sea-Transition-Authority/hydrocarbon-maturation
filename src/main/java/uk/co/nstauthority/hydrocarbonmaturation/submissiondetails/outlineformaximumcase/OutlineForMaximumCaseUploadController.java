package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/outline-for-maximum-case/upload")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class OutlineForMaximumCaseUploadController {

  public static final String PAGE_NAME = "Upload outline for maximum case shapefile";
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final OutlineForMaximumCaseUploadFormService outlineForMaximumCaseFormService;
  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public OutlineForMaximumCaseUploadController(ControllerHelperService controllerHelperService,
                                               SubmissionDetailService submissionDetailService,
                                               OutlineForMaximumCaseUploadFormService outlineForMaximumCaseFormService,
                                               OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService,
                                               SupportingDocumentService supportingDocumentService) {
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.outlineForMaximumCaseFormService = outlineForMaximumCaseFormService;
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  @GetMapping
  public ModelAndView getOutlineForMaximumCaseUpload(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                     @ModelAttribute("form") OutlineForMaximumCaseUploadForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (outlineForMaximumCasePersistenceService.hasVerifiedShapefile(submissionDetail)) {
      return ReverseRouter.redirect(on(OutlineForMaximumCaseVerifyController.class)
          .getOutlineForMaximumCaseVerify(submissionDetailId));
    }

    var outlineForMaximumCaseForm = outlineForMaximumCaseFormService.getOutlineForMaximumCaseForm(submissionDetail);
    return getOutlineForMaximumCaseModelAndView(outlineForMaximumCaseForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveOutlineForMaximumCaseUpload(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                      @ModelAttribute("form") OutlineForMaximumCaseUploadForm form,
                                                      BindingResult bindingResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    bindingResult = outlineForMaximumCaseFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getOutlineForMaximumCaseModelAndView(form, submissionDetail),
        () -> {
          outlineForMaximumCasePersistenceService.saveOutlineForMaximumCaseUploadDescriptions(form);
          return ReverseRouter.redirect(on(OutlineForMaximumCaseVerifyController.class)
              .getOutlineForMaximumCaseVerify(submissionDetailId));
        });
  }

  private ModelAndView getOutlineForMaximumCaseModelAndView(OutlineForMaximumCaseUploadForm form,
                                                            SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/outlineForMaximumCase/upload")
        .addObject("pageTitle", PAGE_NAME)
        .addObject("form", form)
        .addObject("cancelUrl",
            ReverseRouter.route(on(TaskListController.class)
                .getTaskList(submissionDetail.getId())))
        .addObject("outlineForMaximumCaseTemplate", supportingDocumentService.buildFileUploadTemplate(
            submissionDetail.getId(),
            SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE,
            outlineForMaximumCaseFormService.getAllowedExtensions())
        )
        .addObject("outlineForMaximumCaseUploads", outlineForMaximumCaseFormService.getOutlineForMaximumCaseUploads(form));

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
