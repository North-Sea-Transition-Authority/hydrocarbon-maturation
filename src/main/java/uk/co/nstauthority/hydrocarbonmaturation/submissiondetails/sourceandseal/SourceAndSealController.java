package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

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
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/source-and-seal")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class SourceAndSealController {

  private final SourceAndSealFormService sourceAndSealFormService;
  private final FormationSearchService formationSearchService;
  private final SubmissionDetailService submissionDetailService;
  private final SourceAndSealPersistenceService sourceAndSealPersistenceService;
  private final AgeSeriesSearchService ageSeriesSearchService;
  private final SourceAndSealFormValidator sourceAndSealFormValidator;

  public static final String PAGE_NAME = "Source and seal";

  @Autowired
  public SourceAndSealController(SourceAndSealFormService sourceAndSealFormService,
                                 FormationSearchService formationSearchService,
                                 SubmissionDetailService submissionDetailService,
                                 SourceAndSealPersistenceService sourceAndSealPersistenceService,
                                 AgeSeriesSearchService ageSeriesSearchService,
                                 SourceAndSealFormValidator sourceAndSealFormValidator) {
    this.sourceAndSealFormService = sourceAndSealFormService;
    this.formationSearchService = formationSearchService;
    this.submissionDetailService = submissionDetailService;
    this.sourceAndSealPersistenceService = sourceAndSealPersistenceService;
    this.ageSeriesSearchService = ageSeriesSearchService;
    this.sourceAndSealFormValidator = sourceAndSealFormValidator;
  }

  @GetMapping
  public ModelAndView getSourceAndSealDetails(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getSourceAndSealModelAndView(sourceAndSealFormService.getSourceAndSealForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveSourceAndSealDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @ModelAttribute("form") SourceAndSealForm form,
                                               BindingResult bindingResult,
                                               ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (!sourceAndSealFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getSourceAndSealModelAndView(form, submissionDetail);
    } else {
      sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    }
  }

  private ModelAndView getSourceAndSealModelAndView(SourceAndSealForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/sourceAndSeal")
        .addObject("form", form)
        .addObject("fieldSubmission", submissionDetail.isFieldSubmission())
        .addObject("groupNames", GroupName.getGroupNames())
        .addObject("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .addObject("preSelectedSourceFormation", formationSearchService.getPreSelectedFormation(form.getSourceFormationName()))
        .addObject("preSelectedTopSealFormation", formationSearchService.getPreSelectedFormation(form.getTopSealFormationName()))
        .addObject("ageSeriesRestEndpoint",
            SearchSelectorService.route(on(AgeSeriesRestController.class).getAgeSeriesList(null))
        )
        .addObject("preSelectedSourceAgeStage", ageSeriesSearchService.getPreSelectedAgeSeries(form.getSourceAgeStage()))
        .addObject("preSelectedTopSealAgeStage", ageSeriesSearchService.getPreSelectedAgeSeries(form.getTopSealAgeStage()))
        .addObject("topSealLithologyOptions", StandardLithology.getStandardLithologyOptions())
        .addObject("sourceLithologyOptions", StandardLithology.getStandardLithologyOptions());


    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
