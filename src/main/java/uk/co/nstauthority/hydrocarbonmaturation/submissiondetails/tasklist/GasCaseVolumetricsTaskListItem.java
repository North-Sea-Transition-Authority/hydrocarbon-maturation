package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class GasCaseVolumetricsTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = GasCaseVolumetricsController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 10;

  private final GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService;

  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  GasCaseVolumetricsTaskListItem(GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService,
                                 HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.gasCaseVolumetricsSubmissionService = gasCaseVolumetricsSubmissionService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
  }

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(GasCaseVolumetricsController.class).getGasCaseVolumetrics(target.submissionDetail().getId(), null));
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
        && hydrocarbonCasePersistenceService.isGasPresent(target.submissionDetail());
  }

  @Override
  public boolean isValid(ProspectTaskListItemType target) {
    return gasCaseVolumetricsSubmissionService.isSectionSubmittable(target.submissionDetail());
  }
}
