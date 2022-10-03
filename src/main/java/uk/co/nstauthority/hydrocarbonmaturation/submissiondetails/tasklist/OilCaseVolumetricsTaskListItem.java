package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSection;

@Component
class OilCaseVolumetricsTaskListItem implements ProspectTaskListItem {

  static final String ITEM_NAME = OilCaseVolumetricsController.PAGE_NAME;
  static final int ITEM_DISPLAY_ORDER = 10;

  private final OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  OilCaseVolumetricsTaskListItem(OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService,
                                 HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.oilCaseVolumetricsSubmissionService = oilCaseVolumetricsSubmissionService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
  }

  @Override
  public String getItemDisplayText() {
    return ITEM_NAME;
  }

  @Override
  public String getActionUrl(ProspectTaskListItemType target) {
    return ReverseRouter.route(on(OilCaseVolumetricsController.class)
        .getOilCaseVolumetrics(target.submissionDetail().getId(), null));
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
        && hydrocarbonCasePersistenceService.isOilOnlyPresent(target.submissionDetail());
  }

  @Override
  public boolean isValid(ProspectTaskListItemType target) {
    return oilCaseVolumetricsSubmissionService.isSectionSubmittable(target.submissionDetail());
  }
}
