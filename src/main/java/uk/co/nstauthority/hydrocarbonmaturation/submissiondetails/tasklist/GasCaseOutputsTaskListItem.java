package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
public class GasCaseOutputsTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = GasCaseOutputsController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 40;

  private final GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  public GasCaseOutputsTaskListItem(GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService,
                                    HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.gasCaseOutputsSubmissionService = gasCaseOutputsSubmissionService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
  }

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(GasCaseOutputsController.class)
        .getGasCaseOutputs(target.submissionDetail().getId(), null));
  }

  @Override
  public int getDisplayOrder() {
    return ITEM_DISPLAY_ORDER;
  }

  @Override
  public Class<? extends TaskListSection<ProspectTaskListItemType>> getTaskListSection() {
    return VolumetricsTaskListSection.class;
  }

  @Override
  public boolean isVisible(ProspectTaskListItemType target) {
    return ProspectTaskListItem.super.isVisible(target)
        && hydrocarbonCasePersistenceService.isGasOnlyPresent(target.submissionDetail());
  }

  @Override
  public boolean isValid(ProspectTaskListItemType target) {
    return gasCaseOutputsSubmissionService.isSectionSubmittable(target.submissionDetail());
  }
}
