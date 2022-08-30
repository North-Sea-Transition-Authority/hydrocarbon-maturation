package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
 class ChanceOfSuccessTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = ChanceOfSuccessController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 50;

  private final ChanceOfSuccessSubmissionService chanceOfSuccessSubmissionService;

  @Autowired
  public ChanceOfSuccessTaskListItem(ChanceOfSuccessSubmissionService chanceOfSuccessSubmissionService) {
    this.chanceOfSuccessSubmissionService = chanceOfSuccessSubmissionService;
  }

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(target.submissionDetail().getId()));
  }

  @Override
  public int getDisplayOrder() {
    return ITEM_DISPLAY_ORDER;
  }

  @Override
  public boolean isValid(ProspectTaskListItemType target) {
    return chanceOfSuccessSubmissionService.isSectionSubmittable(target.submissionDetail());
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return OpportunityTaskListSection.class;
  }
}
