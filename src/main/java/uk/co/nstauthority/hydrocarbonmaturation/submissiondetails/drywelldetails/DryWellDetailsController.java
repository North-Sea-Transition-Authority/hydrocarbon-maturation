package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;


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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AllowedSubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/dry-well-details")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.POST_DRILL_DRY_HOLE)
@IsFromResponsibleTeam
public class DryWellDetailsController {

  private final DryWellDetailsFormService dryWellDetailsFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final DryWellDetailsPersistenceService dryWellDetailsPersistenceService;

  public static final String PAGE_NAME = "Dry well details";

  @Autowired
  public DryWellDetailsController(DryWellDetailsFormService dryWellDetailsFormService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService,
                                  DryWellDetailsPersistenceService dryWellDetailsPersistenceService) {
    this.dryWellDetailsFormService = dryWellDetailsFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.dryWellDetailsPersistenceService = dryWellDetailsPersistenceService;
  }

  @GetMapping
  public ModelAndView getDryWellDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") DryWellDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getDryWellDetailsModelAndView(dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveDryWellDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") DryWellDetailsForm form, BindingResult bindingResult,
                                         ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = dryWellDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDryWellDetailsModelAndView(form, submissionDetail),
        () -> {
          dryWellDetailsPersistenceService.saveDryWellDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getDryWellDetailsModelAndView(DryWellDetailsForm form, SubmissionDetail submissionDetail) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/wellDrillDetails/dryWellDetails")
        .addObject("form", form)
        .addObject("datumTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirDatum.class))
        .addObject("dryHoleTypes", DisplayableEnumOptionUtil.getDisplayableOptions(DryHoleType.class))
        .addObject("longitudeHemisphere", Hemisphere.getLongitudeHemisphere())
        .addObject("wellEndpoint", SearchSelectorService.route(on(WellRestController.class).searchWells(null)))
        .addObject("well", dryWellDetailsFormService.getPreSelectedWell(form.getDrillDetailsForm().getWell()))
        .addObject("sourceFailureCategories",
            DisplayableEnumOptionUtil.getDisplayableOptions(SourceFailureCategory.class))
        .addObject("reservoirFailureCategories",
            DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirFailureCategory.class))
        .addObject("sealFailureCategories",
            DisplayableEnumOptionUtil.getDisplayableOptions(SealFailureCategory.class))
        .addObject("trapFailureCategories",
            DisplayableEnumOptionUtil.getDisplayableOptions(TrapFailureCategory.class))
        .addObject("migrationFailureCategories",
            DisplayableEnumOptionUtil.getDisplayableOptions(MigrationFailureCategory.class))
        ;

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(
        modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }
}
