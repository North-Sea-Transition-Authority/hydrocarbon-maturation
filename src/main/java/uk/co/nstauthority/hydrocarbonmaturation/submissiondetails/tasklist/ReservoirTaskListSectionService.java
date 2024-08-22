package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.ReservoirAdditionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirAdditionsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirAdditionsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class ReservoirTaskListSectionService implements TaskListSectionService {

  private final ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;
  private final PermeabilitySubmissionService permeabilitySubmissionService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ReservoirAdditionsFormValidator reservoirAdditionsFormValidator;
  private final ReservoirAdditionsFormService reservoirAdditionsFormService;

  @Autowired
  public ReservoirTaskListSectionService(
      ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService,
      PermeabilitySubmissionService permeabilitySubmissionService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      ReservoirAdditionsFormValidator reservoirAdditionsFormValidator,
      ReservoirAdditionsFormService reservoirAdditionsFormService) {
    this.reservoirPropertiesSubmissionService = reservoirPropertiesSubmissionService;
    this.permeabilitySubmissionService = permeabilitySubmissionService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.reservoirAdditionsFormValidator = reservoirAdditionsFormValidator;
    this.reservoirAdditionsFormService = reservoirAdditionsFormService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail, User user) {

    var items = new ArrayList<TaskListItem>();

    if (submissionDetail.isDiscoverySubmission() || submissionDetail.isFieldSubmission()) {
      items.add(
          new TaskListItem("%s reservoirs".formatted(
              submissionDetail.getSubmission().getResource().getType().getDisplayName()),
              isSectionComplete(submissionDetail)
                  ? TaskListLabel.COMPLETE
                  : TaskListLabel.NOT_COMPLETE,
              reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)
                  ? ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(
                      submissionDetail.getId(), null))
                  : ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetail.getId(), null)))
      );
    }

    if (submissionDetail.isProspectSubmission() || submissionDetail.isDryWellSubmission()) {
      var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
          .getExtantReservoirSubmissionDetail(submissionDetail);

      items.add(
          new TaskListItem("Reservoir properties",
              TaskListLabel.notStartedOrComplete(
                  reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
              ReverseRouter.route(on(ReservoirPropertiesController.class)
                  .getReservoirProperties(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
      );

      if (submissionDetail.isDryWellSubmission()) {
        items.add(new TaskListItem("Permeability",
            TaskListLabel.notStartedOrComplete(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)),
            ReverseRouter.route(on(PermeabilityController.class)
                .getPermeabilityPage(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
        );
      }
    }

    return Optional.of(new TaskListSection(
        "Reservoirs",
        40,
        items));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {

    if (submissionDetail.isProspectSubmission() || submissionDetail.isDryWellSubmission()) {
      var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
          .getExtantReservoirSubmissionDetail(submissionDetail);

      if (submissionDetail.isDryWellSubmission()) {
        return reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)
            && permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
      } else if (submissionDetail.isProspectSubmission()) {
        return reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail);
      }
    }

    return true;
  }

  private Boolean isSectionComplete(SubmissionDetail submissionDetail) {
    var form = reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail);

    // check no more reservoirs to be added AND all reservoirs are valid
    return ReservoirAdditionStatus.NO.equals(form.getReservoirAdditionStatus())
        && reservoirAdditionsFormValidator
        .isValid(form, ReverseRouter.emptyBindingResult(), submissionDetail, ValidationType.FULL);
  }
}
