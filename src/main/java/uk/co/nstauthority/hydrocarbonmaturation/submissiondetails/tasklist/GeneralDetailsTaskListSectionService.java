package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus.ConsideringCcusController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus.ConsideringCcusSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.FieldComplexityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.FieldComplexitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Service
class GeneralDetailsTaskListSectionService implements TaskListSectionService {
  private final GeneralDetailSubmissionService generalDetailSubmissionService;
  private final ResourceDetailsSubmissionService resourceDetailsSubmissionService;
  private final RelatedResourcesSubmissionService relatedResourcesSubmissionService;
  private final ConsideringCcusSubmissionService consideringCcusSubmissionService;
  private final FieldComplexitySubmissionService fieldComplexitySubmissionService;
  private final TeamQueryService teamQueryService;

  @Autowired
  GeneralDetailsTaskListSectionService(GeneralDetailSubmissionService generalDetailSubmissionService,
                                       ResourceDetailsSubmissionService resourceDetailsSubmissionService,
                                       RelatedResourcesSubmissionService relatedResourcesSubmissionService,
                                       ConsideringCcusSubmissionService consideringCcusSubmissionService,
                                       FieldComplexitySubmissionService fieldComplexitySubmissionService,
                                       TeamQueryService teamQueryService) {
    this.generalDetailSubmissionService = generalDetailSubmissionService;
    this.resourceDetailsSubmissionService = resourceDetailsSubmissionService;
    this.relatedResourcesSubmissionService = relatedResourcesSubmissionService;
    this.consideringCcusSubmissionService = consideringCcusSubmissionService;
    this.teamQueryService = teamQueryService;
    this.fieldComplexitySubmissionService = fieldComplexitySubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail, User user) {
    var items = getTaskListItemsBySubmissionType(submissionDetail, user);

    return Optional.of(new TaskListSection("General details", 10, items));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    var sectionsSubmittable = generalDetailSubmissionService.isSectionSubmittable(submissionDetail, user)
        && resourceDetailsSubmissionService.isSectionSubmittable(submissionDetail, user);

    /*
        if (areRelatedResourcesRequired(submissionDetail, user)) {
          return sectionsSubmittable && relatedResourcesSubmissionService.isSectionSubmittable(submissionDetail, user);
        }
    */

    return sectionsSubmittable;
  }

  private List<TaskListItem> getTaskListItemsBySubmissionType(SubmissionDetail submissionDetail, User user) {
    var listOfItems = new ArrayList<TaskListItem>();

    if (!submissionDetail.isFieldSubmission()) {
      listOfItems.add(
          new TaskListItem("Resource details",
              TaskListLabel.notStartedOrComplete(resourceDetailsSubmissionService.isSectionSubmittable(submissionDetail, user)),
              ReverseRouter.route(on(ResourceDetailsController.class).getResourceDetails(submissionDetail.getId(), null)))
      );
    }

    listOfItems.add(
        new TaskListItem("General details",
            TaskListLabel.notStartedOrComplete(generalDetailSubmissionService.isSectionSubmittable(submissionDetail, user)),
            ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetail.getId(), null)))
    );

    /*
    TODO determine behaviour for related resources on new resource creation
    if (areRelatedResourcesRequired(submissionDetail, user)) {

      listOfItems.add(
          new TaskListItem("Related %s"
              .formatted(
                  submissionDetail.getSubmission().getResource().getType() == ResourceType.DISCOVERY
                      ? "discoveries"
                      : "prospects"),
              TaskListLabel.notStartedOrComplete(relatedResourcesSubmissionService.isSectionSubmittable(
              submissionDetail, user)),
              ReverseRouter.route(on(RelatedResourcesController.class).getRelatedResources(submissionDetail.getId(), null)))
      );
    }
*/

    if (submissionDetail.isFieldSubmission()) {
      listOfItems.add(
          new TaskListItem(ConsideringCcusController.PAGE_NAME,
              TaskListLabel.notStartedOrComplete(consideringCcusSubmissionService.isSectionSubmittable(submissionDetail, user)),
              ReverseRouter.route(on(ConsideringCcusController.class).getConsideringCcus(submissionDetail.getId())))
      );

      listOfItems.add(
          new TaskListItem(FieldComplexityController.FIELD_COMPLEXITY,
              TaskListLabel.notStartedOrComplete(fieldComplexitySubmissionService.isSectionSubmittable(submissionDetail, user)),
              ReverseRouter.route(on(FieldComplexityController.class).getFieldComplexity(submissionDetail.getId())))
      );
    }

    return listOfItems;
  }

  private boolean areRelatedResourcesRequired(SubmissionDetail submissionDetail, User user) {
    var submissionType = submissionDetail.getSubmissionType();
    var isInitialResourceSubmission = submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT;
    return isInitialResourceSubmission
        && teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)
        && BooleanUtils.isNotTrue(submissionDetail.isCorrection());
  }
}
