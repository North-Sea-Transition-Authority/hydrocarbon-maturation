package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWellsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWellsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells.PlannedUnsanctionedWellsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells.PlannedUnsanctionedWellsSubmissionService;

@Service
public class WellInformationTaskListSectionService implements TaskListSectionService {
  public static final String SECTION_NAME = "Well information";

  private final PlannedSanctionedWellsSubmissionService plannedSanctionedWellsSubmissionService;
  private final PlannedUnsanctionedWellsSubmissionService plannedUnsanctionedWellsSubmissionService;

  @Autowired
  public WellInformationTaskListSectionService(
      PlannedSanctionedWellsSubmissionService plannedSanctionedWellsSubmissionService,
      PlannedUnsanctionedWellsSubmissionService plannedUnsanctionedWellsSubmissionService) {
    this.plannedSanctionedWellsSubmissionService = plannedSanctionedWellsSubmissionService;
    this.plannedUnsanctionedWellsSubmissionService = plannedUnsanctionedWellsSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {
    var isNotFdpOrFdpa = !(submissionDetail.isFieldDevelopmentPlanSubmission()
        || submissionDetail.isFieldDevelopmentPlanAddendumSubmission());

    if (isNotFdpOrFdpa) {
      return Optional.empty();
    }

    var items = getTaskListItemsBySubmissionType(submissionDetail);

    return Optional.of(new TaskListSection(SECTION_NAME, 15, items));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    return plannedSanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail)
        && plannedUnsanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail);
  }

  private List<TaskListItem> getTaskListItemsBySubmissionType(SubmissionDetail submissionDetail) {
    var listOfItems = new ArrayList<TaskListItem>();

    listOfItems.add(
        new TaskListItem(
            PlannedSanctionedWellsController.PAGE_NAME,
            TaskListLabel.notStartedOrComplete(
                plannedSanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(PlannedSanctionedWellsController.class)
                .getPlannedSanctionedWells(submissionDetail.getId())))
    );

    listOfItems.add(
        new TaskListItem(
            PlannedUnsanctionedWellsController.PAGE_NAME,
            TaskListLabel.notStartedOrComplete(
                plannedUnsanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(PlannedUnsanctionedWellsController.class)
                .getPlannedUnsanctionedWells(submissionDetail.getId())))
    );

    return  listOfItems;
  }
}
