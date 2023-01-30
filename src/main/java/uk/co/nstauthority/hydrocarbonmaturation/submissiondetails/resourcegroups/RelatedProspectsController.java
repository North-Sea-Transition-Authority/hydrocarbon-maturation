package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsRegulatorInResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/related-prospects")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsRegulatorInResponsibleTeam
public class RelatedProspectsController {

  public static final String PAGE_NAME = "Related prospects";
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final RelatedProspectsFormService relatedProspectsFormService;
  private final RelatedProspectsPersistenceService relatedProspectsPersistenceService;

  @Autowired
  public RelatedProspectsController(ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService,
                                  RelatedProspectsFormService relatedProspectsFormService,
                                  RelatedProspectsPersistenceService relatedProspectsPersistenceService) {
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.relatedProspectsFormService = relatedProspectsFormService;
    this.relatedProspectsPersistenceService = relatedProspectsPersistenceService;
  }

  @GetMapping
  public ModelAndView getRelatedProspects(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") RelatedProspectsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var relatedProspectsForm = relatedProspectsFormService.getRelatedProspectsForm(submissionDetail);
    return getRelatedProspectsModelAndView(relatedProspectsForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveRelatedProspects(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") RelatedProspectsForm form,
                                         BindingResult bindingResult, ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    bindingResult = relatedProspectsFormService.validate(form, bindingResult, validationType, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getRelatedProspectsModelAndView(form, submissionDetail),
        () -> {
          relatedProspectsPersistenceService.saveRelatedProspects(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        });
  }

  private ModelAndView getRelatedProspectsModelAndView(RelatedProspectsForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/relatedProspects")
        .addObject("pageTitle", PAGE_NAME)
        .addObject("form", form)
        .addObject("prospectGroupsEndpoint",
            SearchSelectorService.route(on(ResourceGroupRestController.class).searchProspects(null)))
        .addObject("relatedSubmissionDetail", relatedProspectsFormService.getPreSelectedSubmissionDetail(form));

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
