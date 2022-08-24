package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class SourceAndSealTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = SourceAndSealController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 30;

  private final SourceAndSealSubmissionService sourceAndSealSubmissionService;

  @Autowired
  SourceAndSealTaskListItem(SourceAndSealSubmissionService sourceAndSealSubmissionService) {
    this.sourceAndSealSubmissionService = sourceAndSealSubmissionService;
  }

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(SourceAndSealController.class)
        .getSourceAndSealDetails(target.submissionDetail().getId()));
  }

  @Override
  public int getDisplayOrder() {
    return ITEM_DISPLAY_ORDER;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return OpportunityTaskListSection.class;
  }

  @Override
  public boolean isValid(ProspectTaskListItemType target) {
    return sourceAndSealSubmissionService.isSectionSubmittable(target.submissionDetail());
  }
}
