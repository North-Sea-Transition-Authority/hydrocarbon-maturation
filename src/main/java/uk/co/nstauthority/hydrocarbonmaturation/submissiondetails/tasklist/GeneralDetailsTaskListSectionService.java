package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.RelatedProspectsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.RelatedProspectsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Service
class GeneralDetailsTaskListSectionService implements TaskListSectionService {

  private final GeneralDetailSubmissionService generalDetailSubmissionService;
  private final RelatedProspectsSubmissionService relatedProspectsSubmissionService;
  private final PermissionService permissionService;
  private final UserDetailService userDetailService;

  @Autowired
  GeneralDetailsTaskListSectionService(GeneralDetailSubmissionService generalDetailSubmissionService,
                                       RelatedProspectsSubmissionService relatedProspectsSubmissionService,
                                       PermissionService permissionService, UserDetailService userDetailService) {
    this.generalDetailSubmissionService = generalDetailSubmissionService;
    this.relatedProspectsSubmissionService = relatedProspectsSubmissionService;
    this.permissionService = permissionService;
    this.userDetailService = userDetailService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {
    var items = getTaskListItemsBySubmissionType(submissionDetail);

    return Optional.of(new TaskListSection("General details", 10, items));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var generalDetailSectionSubmittable = generalDetailSubmissionService.isSectionSubmittable(submissionDetail);

    if (isRelatedProspectsRequired(submissionDetail)) {
      return generalDetailSectionSubmittable && relatedProspectsSubmissionService.isSectionSubmittable(submissionDetail);
    }

    return generalDetailSectionSubmittable;
  }

  private List<TaskListItem> getTaskListItemsBySubmissionType(SubmissionDetail submissionDetail) {
    var listOfItems = new ArrayList<TaskListItem>();

    listOfItems.add(
        new TaskListItem("General details",
            TaskListLabel.notStartedOrComplete(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetail.getId(), null)))
    );

    if (isRelatedProspectsRequired(submissionDetail)) {

      listOfItems.add(
          new TaskListItem("Related prospects",
              TaskListLabel.notStartedOrComplete(relatedProspectsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(RelatedProspectsController.class).getRelatedProspects(submissionDetail.getId(), null)))
      );
    }

    return listOfItems;
  }

  private boolean isRelatedProspectsRequired(SubmissionDetail submissionDetail) {
    var hasExplorationRole =
        permissionService.hasPermission(userDetailService.getUserDetail(), Set.of(RolePermission.EXPLORATION));
    var isCreateProspect = submissionDetail.getSubmissionType() == SubmissionType.CREATE_PROSPECT;
    return hasExplorationRole && isCreateProspect;
  }
}
