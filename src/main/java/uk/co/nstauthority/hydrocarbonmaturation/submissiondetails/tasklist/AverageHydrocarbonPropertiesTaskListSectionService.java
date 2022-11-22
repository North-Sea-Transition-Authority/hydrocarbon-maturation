package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertyController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySubmissionService;

@Service
class AverageHydrocarbonPropertiesTaskListSectionService implements TaskListSectionService {

  private final GasFluidPropertySubmissionService gasFluidPropertySubmissionService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @Autowired
  AverageHydrocarbonPropertiesTaskListSectionService(
      GasFluidPropertySubmissionService gasFluidPropertySubmissionService,
      DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService) {
    this.gasFluidPropertySubmissionService = gasFluidPropertySubmissionService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {
    if (submissionDetail.getSubmissionType() == SubmissionType.CREATE_DISCOVERY) {
      var items = new ArrayList<TaskListItem>();

      var hydrocarbonsDiscovered = discoveryWellDetailsPersistenceService
          .getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
          .map(DiscoveryWellDetail::getHydrocarbonsDiscovered).orElse(null);
      if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY
          || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
        items.add(new TaskListItem("Gas properties",
            TaskListLabel.notStartedOrComplete(
                gasFluidPropertySubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(GasFluidPropertyController.class)
                .getGasFluidProperties(submissionDetail.getId()))));
        return Optional.of(new TaskListSection("Average hydrocarbon properties", 60, items));
      }
    }
    return Optional.empty();
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.getSubmissionType() == SubmissionType.CREATE_DISCOVERY) {
      return gasFluidPropertySubmissionService.isSectionSubmittable(submissionDetail);
    } else {
      return true;
    }
  }
}
