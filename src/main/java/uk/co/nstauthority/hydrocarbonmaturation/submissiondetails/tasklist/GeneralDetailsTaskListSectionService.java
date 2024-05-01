package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus.ConsideringCcusController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus.ConsideringCcusSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.FieldComplexityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.FieldComplexitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsSubmissionService;

@Service
class GeneralDetailsTaskListSectionService implements TaskListSectionService {
  private final GeneralDetailSubmissionService generalDetailSubmissionService;
  private final ResourceDetailsSubmissionService resourceDetailsSubmissionService;
  private final RelatedResourcesSubmissionService relatedResourcesSubmissionService;
  private final ConsideringCcusSubmissionService consideringCcusSubmissionService;
  private final PermissionService permissionService;
  private final UserService userService;
  private final FieldComplexitySubmissionService fieldComplexitySubmissionService;

  @Autowired
  GeneralDetailsTaskListSectionService(GeneralDetailSubmissionService generalDetailSubmissionService,
                                       ResourceDetailsSubmissionService resourceDetailsSubmissionService,
                                       RelatedResourcesSubmissionService relatedResourcesSubmissionService,
                                       ConsideringCcusSubmissionService consideringCcusSubmissionService,
                                       PermissionService permissionService,
                                       UserService userService,
                                       FieldComplexitySubmissionService fieldComplexitySubmissionService) {
    this.generalDetailSubmissionService = generalDetailSubmissionService;
    this.resourceDetailsSubmissionService = resourceDetailsSubmissionService;
    this.relatedResourcesSubmissionService = relatedResourcesSubmissionService;
    this.consideringCcusSubmissionService = consideringCcusSubmissionService;
    this.permissionService = permissionService;
    this.userService = userService;
    this.fieldComplexitySubmissionService = fieldComplexitySubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {
    var items = getTaskListItemsBySubmissionType(submissionDetail);

    return Optional.of(new TaskListSection("General details", 10, items));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var sectionsSubmittable = generalDetailSubmissionService.isSectionSubmittable(submissionDetail)
        && resourceDetailsSubmissionService.isSectionSubmittable(submissionDetail);

    if (isRelatedProspectsRequired(submissionDetail)) {
      return sectionsSubmittable && relatedResourcesSubmissionService.isSectionSubmittable(submissionDetail);
    }

    return sectionsSubmittable;
  }

  private List<TaskListItem> getTaskListItemsBySubmissionType(SubmissionDetail submissionDetail) {
    var listOfItems = new ArrayList<TaskListItem>();

    if (!submissionDetail.isFieldSubmission()) {
      listOfItems.add(
          new TaskListItem("Resource details",
              TaskListLabel.notStartedOrComplete(resourceDetailsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(ResourceDetailsController.class).getResourceDetails(submissionDetail.getId())))
      );
    }

    listOfItems.add(
        new TaskListItem("General details",
            TaskListLabel.notStartedOrComplete(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetail.getId(), null)))
    );

    if (isRelatedProspectsRequired(submissionDetail)) {

      listOfItems.add(
          new TaskListItem("Related %s"
              .formatted(
                  submissionDetail.getSubmission().getResource().getType() == ResourceType.DISCOVERY
                      ? "discoveries"
                      : "prospects"),
              TaskListLabel.notStartedOrComplete(relatedResourcesSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(RelatedResourcesController.class).getRelatedResources(submissionDetail.getId(), null)))
      );
    }

    if (submissionDetail.isFieldSubmission()) {
      listOfItems.add(
          new TaskListItem(ConsideringCcusController.PAGE_NAME,
              TaskListLabel.notStartedOrComplete(consideringCcusSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(ConsideringCcusController.class).getConsideringCcus(submissionDetail.getId())))
      );

      listOfItems.add(
          new TaskListItem(FieldComplexityController.FIELD_COMPLEXITY,
              TaskListLabel.notStartedOrComplete(fieldComplexitySubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(on(FieldComplexityController.class).getFieldComplexity(submissionDetail.getId())))
      );
    }

    return listOfItems;
  }

  private boolean isRelatedProspectsRequired(SubmissionDetail submissionDetail) {
    var submissionType = submissionDetail.getSubmissionType();
    var isInitialResourceSubmission = submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT;
    return isInitialResourceSubmission
        && permissionService.hasPermission(userService.getUser(), Set.of(RolePermission.EXPLORATION))
        && BooleanUtils.isNotTrue(submissionDetail.isCorrection());
  }
}
