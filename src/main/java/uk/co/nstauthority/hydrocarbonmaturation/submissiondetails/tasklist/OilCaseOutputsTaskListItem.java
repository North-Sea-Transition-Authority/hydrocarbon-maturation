package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class OilCaseOutputsTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = OilCaseOutputsController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 20;

  private final OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  OilCaseOutputsTaskListItem(OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService,
                             HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.oilCaseOutputsSubmissionService = oilCaseOutputsSubmissionService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
  }

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(OilCaseOutputsController.class)
        .getOilCaseOutputs(target.submissionDetail().getId(), null));
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
        && hydrocarbonCasePersistenceService.isOilPresent(target.submissionDetail());
  }

  @Override
  public boolean isValid(ProspectTaskListItemType target) {
    return oilCaseOutputsSubmissionService.isSectionSubmittable(target.submissionDetail());
  }
}
