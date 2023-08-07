package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/permeability")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_POST_DRILL)
@IsFromResponsibleTeam
public class PermeabilityController {

  private final SubmissionDetailService submissionDetailService;
  private final PermeabilityFormService permeabilityFormService;
  private final PermeabilityPersistenceService permeabilityPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final PermeabilityFormValidator permeabilityFormValidator;

  public static final String PAGE_NAME = "Permeability";

  @Autowired
  public PermeabilityController(SubmissionDetailService submissionDetailService,
                                PermeabilityFormService permeabilityFormService,
                                PermeabilityPersistenceService permeabilityPersistenceService,
                                ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                PermeabilityFormValidator permeabilityFormValidator) {
    this.submissionDetailService = submissionDetailService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.permeabilityFormValidator = permeabilityFormValidator;
    this.permeabilityFormService = permeabilityFormService;
    this.permeabilityPersistenceService = permeabilityPersistenceService;
  }

  @GetMapping
  public ModelAndView getPermeabilityPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getPermeabilityModelAndView(
        permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail), reservoirSubmissionDetail
    );
  }

  @PostMapping
  public ModelAndView savePermeabilityDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                              @ModelAttribute("form") PermeabilityForm form,
                                              BindingResult bindingResult,
                                              ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    if (!permeabilityFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getPermeabilityModelAndView(form, reservoirSubmissionDetail);
    } else {
      permeabilityPersistenceService.savePermeabilityDetail(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class)
          .getReservoirTaskList(submissionDetailId, null));
    }
  }

  private ModelAndView getPermeabilityModelAndView(PermeabilityForm form, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var viewName = "";
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    if (submissionDetail.isDiscoverySubmission()) {
      viewName = "remi/submissiondetails/permeability/permeabilityDiscovery";
    } else {
      viewName = "remi/submissiondetails/permeability/permeabilityDryWell";
    }

    var modelAndView = new ModelAndView(viewName)
        .addObject("form", form)
        .addObject("methodUsed", DisplayableEnumOptionUtil.getDisplayableOptions(AverageMethod.class))
        .addObject("source", DisplayableEnumOptionUtil.getDisplayableOptions(PermeabilitySource.class));

    return TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(
        modelAndView, PAGE_NAME, reservoirSubmissionDetail
    );
  }
}
