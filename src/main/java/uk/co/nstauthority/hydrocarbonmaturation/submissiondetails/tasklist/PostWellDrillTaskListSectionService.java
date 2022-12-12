package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSubmissionService;

@Service
class PostWellDrillTaskListSectionService implements TaskListSectionService {

  private final DryWellDetailsSubmissionService dryWellDetailsSubmissionService;
  private final DiscoveryWellDetailsSubmissionService discoveryWellDetailsSubmissionService;
  private final DrillStemTestSubmissionService drillStemTestSubmissionService;

  @Autowired
  PostWellDrillTaskListSectionService(DryWellDetailsSubmissionService dryWellDetailsSubmissionService,
                                      DiscoveryWellDetailsSubmissionService discoveryWellDetailsSubmissionService,
                                      DrillStemTestSubmissionService drillStemTestSubmissionService) {
    this.dryWellDetailsSubmissionService = dryWellDetailsSubmissionService;
    this.discoveryWellDetailsSubmissionService = discoveryWellDetailsSubmissionService;
    this.drillStemTestSubmissionService = drillStemTestSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {
    var items = new ArrayList<TaskListItem>();

    if (submissionDetail.getSubmissionType() == SubmissionType.POST_DRILL_DRY_HOLE) {
      items.add(
          new TaskListItem("Dry well details",
              TaskListLabel.notStartedOrComplete(
                  dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(
                  on(DryWellDetailsController.class).getDryWellDetails(submissionDetail.getId(), null)))
      );
    }

    if (submissionDetail.getSubmissionType() == SubmissionType.CREATE_DISCOVERY
        || submissionDetail.getSubmissionType() == SubmissionType.UPDATE_DISCOVERY) {
      items.add(
          new TaskListItem("Discovery well details",
              TaskListLabel.notStartedOrComplete(
                  discoveryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(DiscoveryWellDetailsController.class)
                  .getDiscoveryWellDetails(submissionDetail.getId(), null)))
      );
      addDrillStemTestItem(items, submissionDetail);
    }

    if (!items.isEmpty()) {
      return Optional.of(new TaskListSection("Post well drill", 30, items));
    }

    return Optional.empty();
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.getSubmissionType() == SubmissionType.POST_DRILL_DRY_HOLE) {
      return dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail);
    } else if (submissionDetail.getSubmissionType() == SubmissionType.CREATE_DISCOVERY
        || submissionDetail.getSubmissionType() == SubmissionType.UPDATE_DISCOVERY) {
      return discoveryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail)
          && drillStemTestSubmissionService.isSectionSubmittable(submissionDetail);
    } else {
      return true;
    }
  }

  private void addDrillStemTestItem(List<TaskListItem> items, SubmissionDetail submissionDetail) {
    items.add(
        new TaskListItem("Drill stem test results",
            TaskListLabel.notStartedOrComplete(
                drillStemTestSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(
                on(DrillStemTestController.class).getDrillStemTestPage(submissionDetail.getId(), null)))
    );
  }
}
