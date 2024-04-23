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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.OpportunityTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/outline-for-maximum-case/upload")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = {
    SecuritySubmissionTypes.DISCOVERY_AND_PROSPECT,
    SecuritySubmissionTypes.POST_DRILL_DRY_HOLE
})
@IsFromResponsibleTeam
public class OutlineForMaximumCaseUploadController {

  public static final String PAGE_NAME = "Upload outline for maximum case shapefile";
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final OutlineForMaximumCaseUploadFormService outlineForMaximumCaseFormService;
  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;

  @Autowired
  public OutlineForMaximumCaseUploadController(
      ControllerHelperService controllerHelperService,
      SubmissionDetailService submissionDetailService,
      OutlineForMaximumCaseUploadFormService outlineForMaximumCaseFormService,
      OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService,
      SubmissionSupportingDocumentService supportingDocumentService,
      ResourceDetailsPersistenceService resourceDetailsPersistenceService,
      OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService,
      UserService userService
  ) {
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.outlineForMaximumCaseFormService = outlineForMaximumCaseFormService;
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
    this.supportingDocumentService = supportingDocumentService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.outlineForMaximumCaseDocumentTypeService = outlineForMaximumCaseDocumentTypeService;
  }

  @GetMapping
  public ModelAndView getOutlineForMaximumCaseUpload(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                     @ModelAttribute("form") OutlineForMaximumCaseUploadForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (outlineForMaximumCasePersistenceService.hasVerifiedShapefile(submissionDetail)) {
      return ReverseRouter.redirect(on(OutlineForMaximumCaseVerifyController.class)
          .getOutlineForMaximumCaseVerify(submissionDetailId));
    }

    var outlineForMaximumCaseForm = outlineForMaximumCaseFormService.getOutlineForMaximumCaseUploadForm(submissionDetail);
    return getOutlineForMaximumCaseModelAndView(outlineForMaximumCaseForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveOutlineForMaximumCaseUpload(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                      @ModelAttribute("form") OutlineForMaximumCaseUploadForm form,
                                                      BindingResult bindingResult,
                                                      User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    bindingResult = outlineForMaximumCaseFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getOutlineForMaximumCaseModelAndView(form, submissionDetail),
        () -> {
          outlineForMaximumCasePersistenceService
              .saveOutlineForMaximumCaseUploads(submissionDetail, form.getOutlineForMaximumCase(), user);
          return ReverseRouter.redirect(on(OutlineForMaximumCaseVerifyController.class)
              .getOutlineForMaximumCaseVerify(submissionDetailId));
        });
  }

  private ModelAndView getOutlineForMaximumCaseModelAndView(OutlineForMaximumCaseUploadForm form,
                                                            SubmissionDetail submissionDetail) {
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);
    var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(resourceDetail);

    var modelAndView = new ModelAndView("remi/submissiondetails/outlineForMaximumCase/upload")
        .addObject("pageTitle", PAGE_NAME)
        .addObject("form", form)
        .addObject("cancelUrl",
            ReverseRouter.route(on(TaskListController.class)
                .getTaskList(submissionDetail.getId())))
        .addObject("outlineForMaximumCaseTemplate",
            supportingDocumentService.submissionFileUploadComponentAttributes(
                form.getOutlineForMaximumCase(),
                documentType,
                submissionDetail))
        .addObject("isOffshore", resourceDetail.getOnshoreOffshore() == OnshoreOffshore.OFFSHORE);

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(
            modelAndView,
            OpportunityTaskListSectionService.OUTLINE_FOR_MAXIMUM_CASE,
            submissionDetail
        );
  }
}
