package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedVolTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes.FdpaIncreasedVolumesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes.FdpaIncreasedVolumesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes.FdpaPromotedVolumesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes.FdpaVolumesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals.FieldCombinedCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals.FieldCombinedCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics.FieldGasCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics.FieldGasCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
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
  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;
  private final FdpaPromotedVolumesSubmissionService fdpaPromotedVolumesSubmissionService;
  private final FdpaIncreasedVolumesSubmissionService fdpaIncreasedVolumesSubmissionService;

  @Autowired
  public TotalVolumesTaskListSectionService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      DiscoveryOilCaseVolumetricTotalsSubmissionService discoveryOilCaseVolumetricTotalsSubmissionService,
      DiscoveryGasCaseVolumetricTotalsSubmissionService discoveryGasCaseVolumetricTotalsSubmissionService,
      DiscoveryCombinedVolTotalsSubmissionService discoveryCombinedVolTotalsSubmissionService,
      FieldOilCaseVolumetricTotalsSubmissionService fieldOilCaseVolumetricTotalsSubmissionService,
      FieldGasCaseVolumetricTotalsSubmissionService fieldGasCaseVolumetricTotalsSubmissionService,
      FieldCombinedCaseVolumetricTotalsSubmissionService fieldCombinedCaseVolumetricTotalsSubmissionService,
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService,
      FdpaPromotedVolumesSubmissionService fdpaPromotedVolumesSubmissionService,
      FdpaIncreasedVolumesSubmissionService fdpaIncreasedVolumesSubmissionService) {

    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.discoveryOilCaseVolumetricTotalsSubmissionService = discoveryOilCaseVolumetricTotalsSubmissionService;
    this.discoveryGasCaseVolumetricTotalsSubmissionService = discoveryGasCaseVolumetricTotalsSubmissionService;
    this.discoveryCombinedVolTotalsSubmissionService = discoveryCombinedVolTotalsSubmissionService;
    this.fieldOilCaseVolumetricTotalsSubmissionService = fieldOilCaseVolumetricTotalsSubmissionService;
    this.fieldGasCaseVolumetricTotalsSubmissionService = fieldGasCaseVolumetricTotalsSubmissionService;
    this.fieldCombinedCaseVolumetricTotalsSubmissionService = fieldCombinedCaseVolumetricTotalsSubmissionService;
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
    this.fdpaPromotedVolumesSubmissionService = fdpaPromotedVolumesSubmissionService;
    this.fdpaIncreasedVolumesSubmissionService = fdpaIncreasedVolumesSubmissionService;
  }

  public boolean sectionIsPresent(SubmissionDetail submissionDetail, User user) {
    return getSection(submissionDetail, user).isPresent();
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail, User user) {
    if (submissionDetail.isProspectSubmission() || submissionDetail.isDryWellSubmission()) {
      return Optional.empty();
    }

    var reservoirSubmissionDetails =
        reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
            submissionDetail);

    if (reservoirSubmissionDetails.isEmpty()) {
      return Optional.empty();
    }

    if (submissionDetail.isDiscoverySubmission() && reservoirSubmissionDetails.size() == 1) {
      return Optional.empty();
    }

    if (!reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)
        && !reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails)
        && !reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails)) {
      return Optional.empty();
    }

    var items = getTaskListItems(submissionDetail, reservoirSubmissionDetails, user);
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
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    if (submissionDetail.isProspectSubmission() || submissionDetail.isDryWellSubmission()) {
      return true;
    }

    var reservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    if (reservoirSubmissionDetails.isEmpty()) {
      return false;
    }
    if (submissionDetail.isDiscoverySubmission() && reservoirSubmissionDetails.size() == 1) {
      return true;
    }
    if (submissionDetail.isDiscoverySubmission()) {
      return isDiscoveryHydrocarbonCaseSubmittable(submissionDetail, reservoirSubmissionDetails, user);
    } else if (submissionDetail.isFieldDevelopmentPlanAddendumSubmission()) {
      return isFieldHydrocarbonCaseSectionSubmittable(submissionDetail, reservoirSubmissionDetails, user)
          && isFdpaPromotedVolumesSectionSubmittable(submissionDetail, user)
          && isFdpaIncreasedVolumesSectionSubmittable(submissionDetail, user);
    } else if (submissionDetail.isFieldSubmission()) {
      return isFieldHydrocarbonCaseSectionSubmittable(submissionDetail, reservoirSubmissionDetails, user);
    }
    return false;
  }

  private boolean isFieldHydrocarbonCaseSectionSubmittable(SubmissionDetail submissionDetail,
                                                           List<ReservoirSubmissionDetail> reservoirSubmissionDetails,
                                                           User user) {
    if (reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)) {
      return fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user);
    } else if (reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails)) {
      return fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user);
    } else if (reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails)) {
      return fieldCombinedCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user);
    }
    return false;
  }

  private boolean isDiscoveryHydrocarbonCaseSubmittable(SubmissionDetail submissionDetail,
                                                        List<ReservoirSubmissionDetail> reservoirSubmissionDetails,
                                                        User user) {
    if (reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)) {
      return discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user);
    } else if (reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails)) {
      return discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user);
    } else if (reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails)) {
      return discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail, user);
    }
    return false;
  }

  private boolean isFdpaPromotedVolumesSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    return fdpaPromotedVolumesSubmissionService.isSectionSubmittable(submissionDetail, user);
  }

  private boolean isFdpaIncreasedVolumesSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    return fdpaIncreasedVolumesSubmissionService.isSectionSubmittable(submissionDetail, user);
  }

  private List<TaskListItem> getTaskListItems(SubmissionDetail submissionDetail,
                                              List<ReservoirSubmissionDetail> reservoirSubmissionDetails,
                                              User user) {
    var listOfItems = new ArrayList<TaskListItem>();
    if (submissionDetail.isDiscoverySubmission()) {
      if (reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil case volumetrics",
            TaskListLabel.notStartedOrComplete(
                discoveryOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user)
            ),
            ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class)
                .getDiscoveryOilCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                discoveryGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user)
            ),
            ReverseRouter.route(on(DiscoveryGasCaseVolumetricTotalsController.class)
                .getDiscoveryGasCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil and gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                discoveryCombinedVolTotalsSubmissionService.isSectionSubmittable(submissionDetail, user)
            ),
            ReverseRouter.route(on(DiscoveryCombinedCaseVolTotalsController.class)
                .getDiscoveryCombinedCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
    } else if (submissionDetail.isFieldSubmission()) {
      if (reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil case volumetrics",
            TaskListLabel.notStartedOrComplete(
                fieldOilCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user)
            ),
            ReverseRouter.route(on(FieldOilCaseVolumetricTotalsController.class)
                .getFieldOilCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (reservoirHydrocarbonCaseService.isGasCaseOnly(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user)
            ),
            ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class)
                .getFieldGasCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
      if (reservoirHydrocarbonCaseService.isCombinedCase(reservoirSubmissionDetails)) {
        listOfItems.add(new TaskListItem("Total oil and gas case volumetrics",
            TaskListLabel.notStartedOrComplete(
                fieldCombinedCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, user)
            ),
            ReverseRouter.route(on(FieldCombinedCaseVolumetricTotalsController.class)
                .getFieldCombinedCaseVolumetricTotals(submissionDetail.getId())))
        );
      }
    }

    if (submissionDetail.isFieldDevelopmentPlanAddendumSubmission()) {
      listOfItems.add(new TaskListItem(
          "FDPA promoted volumes",
          TaskListLabel.notStartedOrComplete(
              fdpaPromotedVolumesSubmissionService.isSectionSubmittable(submissionDetail, user)),
          ReverseRouter.route(on(FdpaVolumesController.class)
              .getFdpaPromotedVolumes(submissionDetail.getId()))
      ));

      listOfItems.add(new TaskListItem(
          "FDPA increased volumes",
          TaskListLabel.notStartedOrComplete(
              fdpaIncreasedVolumesSubmissionService.isSectionSubmittable(submissionDetail, user)),
          ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
              .getFdpaIncreasedVolumes(submissionDetail.getId()))
      ));
    }

    return listOfItems;
  }
}
