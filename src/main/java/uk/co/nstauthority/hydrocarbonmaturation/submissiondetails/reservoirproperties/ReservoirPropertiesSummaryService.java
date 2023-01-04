package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.ArrayList;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;

@Service
public class ReservoirPropertiesSummaryService {

  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @Autowired
  public ReservoirPropertiesSummaryService(
      ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService) {
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
  }

  public ReservoirPropertiesSummaryView getReservoirPropertiesSummaryFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    var reservoirPropertyOptional = reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail);
    return reservoirPropertyOptional.map(
            reservoirProperty -> createSummaryViewFromReservoirProperty(submissionDetail, reservoirProperty))
        .orElse(createSummaryViewFromReservoirProperty(submissionDetail, new ReservoirProperty()));
  }

  private ReservoirPropertiesSummaryView createSummaryViewFromReservoirProperty(SubmissionDetail submissionDetail,
                                                                                ReservoirProperty reservoirProperty) {
    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    ProbabilityDistributionSummaryView averageNetPayThickness;
    if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      averageNetPayThickness = probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY,
              submissionDetail
      );
    } else {
      averageNetPayThickness = probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
              submissionDetail
      );
    }
    listOfSummaryViews.add(averageNetPayThickness);

    var grossReservoirThickness =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(grossReservoirThickness);

    var grossRockVolume =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME,
            submissionDetail);
    listOfSummaryViews.add(grossRockVolume);

    var percentOfGrvOilBearing =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING,
            submissionDetail);
    listOfSummaryViews.add(percentOfGrvOilBearing);

    var netToGross =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(netToGross);

    var netPorosity =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(netPorosity);

    if (submissionDetail.isProspectSubmission()) {
      var depthToHydrocarbonContact =
          probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT,
              submissionDetail);
      listOfSummaryViews.add(depthToHydrocarbonContact);
    }

    var listOfDepthToContactDistributions  = new ArrayList<ProbabilityDistributionSummaryView>();

    if (submissionDetail.isDiscoverySubmission()) {
      discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
          .map(DiscoveryWellDetail::getHydrocarbonsDiscovered)
          .ifPresent(hydrocarbonsDiscovered -> {
            if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
                || hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
              var depthToOilWaterContact =
                  probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
                      submissionDetail
                  );
              listOfDepthToContactDistributions.add(depthToOilWaterContact);
            }
            if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
                || hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
              var depthToGasWaterContact =
                  probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
                      submissionDetail
                  );
              listOfDepthToContactDistributions.add(depthToGasWaterContact);
            }
            if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
              var depthToGasOilContact =
                  probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
                      submissionDetail
                  );
              listOfDepthToContactDistributions.add(depthToGasOilContact);
            }
          });
    }

    return new ReservoirPropertiesSummaryView(
        submissionDetail.getSubmissionType(),
        Objects.requireNonNullElse(reservoirProperty.getFormationName(), ""),
        Objects.requireNonNullElse(reservoirProperty.getAgeStage(), ""),
        Objects.requireNonNullElse(reservoirProperty.getLithology(), ""),
        Objects.requireNonNullElse(reservoirProperty.getReservoirFacies(), ""),
        Objects.requireNonNullElse(reservoirProperty.getReservoirConnectivity(), ""),
        listOfSummaryViews,
        listOfDepthToContactDistributions
    );
  }
}
