package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSubmissionService;

@Service
class PostWellDrillTaskListSectionService implements TaskListSectionService {

  private final DryWellDetailsSubmissionService dryWellDetailsSubmissionService;
  private final DiscoveryWellDetailsSubmissionService discoveryWellDetailsSubmissionService;

  @Autowired
  PostWellDrillTaskListSectionService(DryWellDetailsSubmissionService dryWellDetailsSubmissionService,
                                      DiscoveryWellDetailsSubmissionService discoveryWellDetailsSubmissionService) {
    this.dryWellDetailsSubmissionService = dryWellDetailsSubmissionService;
    this.discoveryWellDetailsSubmissionService = discoveryWellDetailsSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {
    var items = new ArrayList<TaskListItem>();

    if (submissionDetail.isDryWellSubmission()) {
      items.add(
          new TaskListItem("Dry well details",
              TaskListLabel.notStartedOrComplete(
                  dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(
                  on(DryWellDetailsController.class).getDryWellDetails(submissionDetail.getId(), null)))
      );
    }

    if (submissionDetail.isDiscoverySubmission()) {
      items.add(
          new TaskListItem("Discovery well details",
              TaskListLabel.notStartedOrComplete(
                  discoveryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(DiscoveryWellDetailsController.class)
                  .getDiscoveryWellDetails(submissionDetail.getId(), null)))
      );
    }

    if (!items.isEmpty()) {
      return Optional.of(new TaskListSection("Post well drill", 30, items));
    }

    return Optional.empty();
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.isDryWellSubmission()) {
      return dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail);
    } else if (submissionDetail.isDiscoverySubmission()) {
      return discoveryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail);
    } else {
      return true;
    }
  }
}
