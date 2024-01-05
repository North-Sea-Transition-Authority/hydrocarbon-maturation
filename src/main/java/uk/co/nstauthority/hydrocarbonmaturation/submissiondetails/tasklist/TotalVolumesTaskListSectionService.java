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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals.FieldCombinedCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals.FieldCombinedCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics.FieldGasCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics.FieldGasCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.VolumetricTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
public class TotalVolumesTaskListSectionService implements TaskListSectionService {

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final DiscoveryOilCaseVolumetricTotalsSubmissionService discoveryOilCaseVolumetricTotalsSubmissionService;
  private final DiscoveryGasCaseVolumetricTotalsSubmissionService discoveryGasCaseVolumetricTotalsSubmissionService;
  private final DiscoveryCombinedVolTotalsSubmissionService discoveryCombinedVolTotalsSubmissionService;
  private final FieldOilCaseVolumetricTotalsSubmissionService fieldOilCaseVolumetricTotalsSubmissionService;
  private final FieldGasCaseVolumetricTotalsSubmissionService fieldGasCaseVolumetricTotalsSubmissionService;
  private final FieldCombinedCaseVolumetricTotalsSubmissionService fieldCombinedCaseVolumetricTotalsSubmissionService;
  private final VolumetricTotalsService volumetricTotalsService;

  @Autowired
  public TotalVolumesTaskListSectionService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      DiscoveryOilCaseVolumetricTotalsSubmissionService discoveryOilCaseVolumetricTotalsSubmissionService,
      DiscoveryGasCaseVolumetricTotalsSubmissionService discoveryGasCaseVolumetricTotalsSubmissionService,
      DiscoveryCombinedVolTotalsSubmissionService discoveryCombinedVolTotalsSubmissionService,
      FieldOilCaseVolumetricTotalsSubmissionService fieldOilCaseVolumetricTotalsSubmissionService,
      FieldGasCaseVolumetricTotalsSubmissionService fieldGasCaseVolumetricTotalsSubmissionService,
      FieldCombinedCaseVolumetricTotalsSubmissionService fieldCombinedCaseVolumetricTotalsSubmissionService,
      VolumetricTotalsService volumetricTotalsService) {

    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.discoveryOilCaseVolumetricTotalsSubmissionService = discoveryOilCaseVolumetricTotalsSubmissionService;
    this.discoveryGasCaseVolumetricTotalsSubmissionService = discoveryGasCaseVolumetricTotalsSubmissionService;
    this.discoveryCombinedVolTotalsSubmissionService = discoveryCombinedVolTotalsSubmissionService;
    this.fieldOilCaseVolumetricTotalsSubmissionService = fieldOilCaseVolumetricTotalsSubmissionService;
    this.fieldGasCaseVolumetricTotalsSubmissionService = fieldGasCaseVolumetricTotalsSubmissionService;
    this.fieldCombinedCaseVolumetricTotalsSubmissionService = fieldCombinedCaseVolumetricTotalsSubmissionService;
    this.volumetricTotalsService = volumetricTotalsService;
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
    if (reservoirSubmissionDetails.size() < 2) {
      return Optional.empty();
    }

    if (!volumetricTotalsService.isOilCaseOnly(reservoirSubmissionDetails)
        && !volumetricTotalsService.isGasCaseOnly(reservoirSubmissionDetails)
        && !volumetricTotalsService.isCombinedCase(reservoirSubmissionDetails)) {
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

    var reservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    if (reservoirSubmissionDetails.isEmpty()) {
      return false;
    }
    if (reservoirSubmissionDetails.size() == 1) {
      return true;
    }
    if (submissionDetail.isDiscoverySubmission()) {
      return isDiscoveryHydrocarbonCaseSubmittable(submissionDetail, reservoirSubmissionDetails);
    } else if (submissionDetail.isFieldSubmission()) {
      return isFieldHydrocarbonCaseSectionSubmittable(submissionDetail, reservoirSubmissionDetails);
    }
    return false;
  }

  private boolean isFieldHydrocarbonCaseSectionSubmittable(SubmissionDetail submissionDetail,
                                                           List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    if (volumetricTotalsService.isOilCaseOnly(reservoirSubmissionDetails)) {
      return fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail);
    } else if (volumetricTotalsService.isGasCaseOnly(reservoirSubmissionDetails)) {
      return fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail);
    } else if (volumetricTotalsService.isCombinedCase(reservoirSubmissionDetails)) {
      return fieldCombinedCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail);
    }
    return false;
  }

  private boolean isDiscoveryHydrocarbonCaseSubmittable(SubmissionDetail submissionDetail,
                                                        List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    if (volumetricTotalsService.isOilCaseOnly(reservoirSubmissionDetails)) {
      return discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail);
    } else if (volumetricTotalsService.isGasCaseOnly(reservoirSubmissionDetails)) {
      return discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail);
    } else if (volumetricTotalsService.isCombinedCase(reservoirSubmissionDetails)) {
      return discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail);
    }
    return false;
  }

  private List<TaskListItem> getTaskListItems(SubmissionDetail submissionDetail,
                                              List<ReservoirSubmissionDetail> reservoirSubmissionDetails) {
    var listOfItems = new ArrayList<TaskListItem>();
    if (submissionDetail.isDiscoverySubmission()) {
      if (volumetricTotalsService.isOilCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil case volumetrics",
            TaskListLabel.notStartedOrComplete(
                discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class)
                .getDiscoveryOilCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (volumetricTotalsService.isGasCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(DiscoveryGasCaseVolumetricTotalsController.class)
                .getDiscoveryGasCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (volumetricTotalsService.isCombinedCase(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil and gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(DiscoveryCombinedCaseVolTotalsController.class)
                .getDiscoveryCombinedCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
    } else if (submissionDetail.isFieldSubmission()) {
      if (volumetricTotalsService.isOilCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil case volumetrics",
            TaskListLabel.notStartedOrComplete(
                fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(FieldOilCaseVolumetricTotalsController.class)
                .getFieldOilCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (volumetricTotalsService.isGasCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class)
                .getFieldGasCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (volumetricTotalsService.isCombinedCase(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil and gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                fieldCombinedCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail)
            ),
            ReverseRouter.route(on(FieldCombinedCaseVolumetricTotalsController.class)
                .getFieldCombinedCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
    }

    return listOfItems;
  }
}
