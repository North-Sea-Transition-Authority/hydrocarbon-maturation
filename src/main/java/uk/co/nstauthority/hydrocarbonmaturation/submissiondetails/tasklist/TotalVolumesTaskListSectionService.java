package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedVolTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilvolumetrics.FieldOilCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilvolumetrics.FieldOilCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
public class TotalVolumesTaskListSectionService implements TaskListSectionService {

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final DiscoveryOilCaseVolumetricTotalsSubmissionService discoveryOilCaseVolumetricTotalsSubmissionService;
  private final DiscoveryGasCaseVolumetricTotalsSubmissionService discoveryGasCaseVolumetricTotalsSubmissionService;
  private final DiscoveryCombinedVolTotalsSubmissionService discoveryCombinedVolTotalsSubmissionService;
  private final FieldOilCaseVolumetricTotalsSubmissionService fieldOilCaseVolumetricTotalsSubmissionService;

  @Autowired
  public TotalVolumesTaskListSectionService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      GeneralReservoirInformationService generalReservoirInformationService,
      DiscoveryOilCaseVolumetricTotalsSubmissionService discoveryOilCaseVolumetricTotalsSubmissionService,
      DiscoveryGasCaseVolumetricTotalsSubmissionService discoveryGasCaseVolumetricTotalsSubmissionService,
      DiscoveryCombinedVolTotalsSubmissionService discoveryCombinedVolTotalsSubmissionService,
      FieldOilCaseVolumetricTotalsSubmissionService fieldOilCaseVolumetricTotalsSubmissionService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.discoveryOilCaseVolumetricTotalsSubmissionService = discoveryOilCaseVolumetricTotalsSubmissionService;
    this.discoveryGasCaseVolumetricTotalsSubmissionService = discoveryGasCaseVolumetricTotalsSubmissionService;
    this.discoveryCombinedVolTotalsSubmissionService = discoveryCombinedVolTotalsSubmissionService;
    this.fieldOilCaseVolumetricTotalsSubmissionService = fieldOilCaseVolumetricTotalsSubmissionService;
  }

  public boolean sectionIsPresent(SubmissionDetail submissionDetail) {
    return getSection(submissionDetail).isPresent();
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {
    if (submissionDetail.isProspectSubmission()) {
      return Optional.empty();
    }

    var reservoirSubmissionDetails =
        reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    if (reservoirSubmissionDetails.isEmpty() || reservoirSubmissionDetails.size() == 1) {
      return Optional.empty();
    }

    if (!isOilCaseOnly(reservoirSubmissionDetails)
        && !isGasCaseOnly(reservoirSubmissionDetails)
        && !isCombinedCase(reservoirSubmissionDetails)) {
      return Optional.empty();
    }

    var items = getTaskListItems(submissionDetail, reservoirSubmissionDetails);
    return Optional.of(
        new TaskListSection(
            String.format("Total volumes for %s",
                submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase()),
            50,
            items
        )
    );
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.isProspectSubmission()) {
      return true;
    }

    var reservoirSubmissionDetails =
        reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    if (reservoirSubmissionDetails.isEmpty()) {
      return false;
    }
    if (reservoirSubmissionDetails.size() == 1) {
      return true;
    }
    if (submissionDetail.isDiscoverySubmission()) {
      if (isOilCaseOnly(reservoirSubmissionDetails)) {
        return discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail);
      }
      if (isGasCaseOnly(reservoirSubmissionDetails)) {
        return discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail);
      }
      if (isCombinedCase(reservoirSubmissionDetails)) {
        return discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail);
      }
    } else if (submissionDetail.isFieldSubmission()) {
      if (isOilCaseOnly(reservoirSubmissionDetails)) {
        return fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail);
      }
    }
    return false;
  }

  private List<TaskListItem> getTaskListItems(SubmissionDetail submissionDetail,
                                              List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    var listOfItems = new ArrayList<TaskListItem>();
    if (submissionDetail.isDiscoverySubmission()) {
      if (isOilCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil case volumetrics",
            TaskListLabel.notStartedOrComplete(
                discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class)
                .getDiscoveryOilCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (isGasCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(DiscoveryGasCaseVolumetricTotalsController.class)
                .getDiscoveryGasCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (isCombinedCase(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil and gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(DiscoveryCombinedCaseVolTotalsController.class)
                .getDiscoveryCombinedCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
    } else if (submissionDetail.isFieldSubmission()) {
      if (isOilCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil case volumetrics",
            TaskListLabel.notStartedOrComplete(
                fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(FieldOilCaseVolumetricTotalsController.class)
                .getFieldOilCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
    }

    return listOfItems;
  }

  public boolean isOilCaseOnly(List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    return reservoirSubmissionDetails.stream().allMatch(generalReservoirInformationService::isOilOnly);
  }

  public boolean isGasCaseOnly(List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    return reservoirSubmissionDetails.stream().allMatch(generalReservoirInformationService::isGasOnly);
  }

  /** Considered a combined case is any of the reservoirs have combined oil and gas selected.
   * Or a both oil and gas cases have been selected separately.
   * @param reservoirSubmissionDetails the list of reservoirs on the submission
   * @return true if it's a combined case, else false
   */
  public boolean isCombinedCase(List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    var anyCombined = reservoirSubmissionDetails.stream().anyMatch(generalReservoirInformationService::isCombinedOilAndGas);
    var anyOil = reservoirSubmissionDetails.stream().anyMatch(generalReservoirInformationService::isOilOnly);
    var anyGas = reservoirSubmissionDetails.stream().anyMatch(generalReservoirInformationService::isGasOnly);
    return anyCombined || (anyOil && anyGas);
  }
}
