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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.OpportunityTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/outline-for-maximum-case/verify")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = {
    SecuritySubmissionTypes.DISCOVERY_AND_PROSPECT,
    SecuritySubmissionTypes.POST_DRILL_DRY_HOLE
})
@IsFromResponsibleTeam
public class OutlineForMaximumCaseVerifyController {

  public static final String PAGE_NAME = "Verify shapefile of outline for maximum case";
  public static final String BREADCRUMB = "Verify outline for maximum case";

  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final OutlineForMaximumCaseVerifyFormService outlineForMaximumCaseVerifyFormService;
  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  private final OutlineForMaximumCaseMapService outlineForMaximumCaseMapService;

  @Autowired
  public OutlineForMaximumCaseVerifyController(ControllerHelperService controllerHelperService,
                                               SubmissionDetailService submissionDetailService,
                                               OutlineForMaximumCaseVerifyFormService outlineForMaximumCaseVerifyFormService,
                                               OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService,
                                               OutlineForMaximumCaseMapService outlineForMaximumCaseMapService) {
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.outlineForMaximumCaseVerifyFormService = outlineForMaximumCaseVerifyFormService;
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
    this.outlineForMaximumCaseMapService = outlineForMaximumCaseMapService;
  }

  @GetMapping
  public ModelAndView getOutlineForMaximumCaseVerify(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var form = outlineForMaximumCaseVerifyFormService.getOutlineForMaximumCaseVerifyForm(submissionDetail);
    return getOutlineForMaximumCaseModelAndView(form, submissionDetail);
  }



  @PostMapping
  public ModelAndView saveOutlineForMaximumCaseUpload(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                      @ModelAttribute("form") OutlineForMaximumCaseVerifyForm form,
                                                      BindingResult bindingResult,
                                                      User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    bindingResult = outlineForMaximumCaseVerifyFormService.validate(
        form, bindingResult, ValidationType.FULL, submissionDetail, user);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getOutlineForMaximumCaseModelAndView(form, submissionDetail),
        () -> {
          outlineForMaximumCasePersistenceService.saveOutlineForMaximumCaseVerifyForm(submissionDetail, form);

          if (outlineForMaximumCaseVerifyFormService.getShapeIsCorrect(form)) {
            return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId, null));
          }

          outlineForMaximumCasePersistenceService.deleteOutlineForMaximumCase(submissionDetail);

          return ReverseRouter.redirect(on(OutlineForMaximumCaseUploadController.class)
              .getOutlineForMaximumCaseUpload(submissionDetailId, null));
        });
  }

  private ModelAndView getOutlineForMaximumCaseModelAndView(OutlineForMaximumCaseVerifyForm form,
                                                            SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/outlineForMaximumCase/verify")
        .addObject("pageTitle", PAGE_NAME)
        .addObject("form", form)
        .addObject("cancelUrl",
            ReverseRouter.route(on(TaskListController.class)
                .getTaskList(submissionDetail.getId(), null)))
        .addObject("geoJsonDataUrl", outlineForMaximumCaseMapService.getMapGeoJsonDataRoute(submissionDetail))
        .addObject("dataMapAttributions", outlineForMaximumCaseMapService.getMapAttributions());

    var outlineFormMaximumCaseUploadBreadcrumb = new BreadcrumbItem(
        OpportunityTaskListSectionService.OUTLINE_FOR_MAXIMUM_CASE,
        ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class)
            .getOutlineForMaximumCaseUpload(submissionDetail.getId(), null)
        )
    );

    var breadcrumbs = Breadcrumbs.builder(BREADCRUMB)
        .addWorkAreaBreadcrumb()
        .addTaskListBreadcrumb(submissionDetail)
        .addBreadcrumb(outlineFormMaximumCaseUploadBreadcrumb)
        .build();
    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}
