package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.ArrayList;
import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;

@Service
public class ReservoirPropertiesSummaryService {

  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  public ReservoirPropertiesSummaryService(
      ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService,
      HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
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

    addGrossReservoirThickness(submissionDetail, listOfSummaryViews);
    addGrossRockVolume(submissionDetail, listOfSummaryViews);
    if (submissionDetail.isDryWellSubmission()) {
      addPercentOfGrvOilBearing(submissionDetail, listOfSummaryViews);
    } else if (submissionDetail.isProspectSubmission()) {
      hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail)
          .ifPresent(hydrocarbonCaseDetail -> {
            if (hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail)) {
              addPercentOfGrvOilBearing(submissionDetail, listOfSummaryViews);
            }
          });
    }
    var listOfDepthToContactDistributions  = getDiscoveryProbabilityTableSummaryViews(submissionDetail, listOfSummaryViews);

    addNetToGross(submissionDetail, listOfSummaryViews);
    addNetPorosity(submissionDetail, listOfSummaryViews);

    if (submissionDetail.isProspectSubmission()) {
      addDepthToHydrocarbonContact(submissionDetail, listOfSummaryViews);
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

  private void addNetPorosity(SubmissionDetail submissionDetail,
                              List<ProbabilityDistributionSummaryView> listOfSummaryViews) {
    var netPorosity =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(netPorosity);
  }

  private void addNetToGross(SubmissionDetail submissionDetail,
                             List<ProbabilityDistributionSummaryView> listOfSummaryViews) {
    var netToGross =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(netToGross);
  }

  private List<ProbabilityDistributionSummaryView> getDiscoveryProbabilityTableSummaryViews(
      SubmissionDetail submissionDetail,
      List<ProbabilityDistributionSummaryView> listOfSummaryViews) {
    var depthToContactDistributions = new ArrayList<ProbabilityDistributionSummaryView>();
    if (submissionDetail.isDiscoverySubmission()) {
      discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
          .map(DiscoveryWellDetail::getHydrocarbonsDiscovered)
          .ifPresent(hydrocarbonsDiscovered -> {
            if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
                || hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
              addDepthToOilWaterContact(submissionDetail, depthToContactDistributions);
            }
            if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
                || hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
              addDepthToGasWaterContact(submissionDetail, depthToContactDistributions);
            }
            if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
              addPercentOfGrvOilBearing(submissionDetail, listOfSummaryViews);
              addDepthToGasOilContact(submissionDetail, depthToContactDistributions);
            }
          });
    }
    return depthToContactDistributions;
  }

  private void addGrossReservoirThickness(SubmissionDetail submissionDetail,
                                          List<ProbabilityDistributionSummaryView> listOfSummaryViews) {
    var grossReservoirThickness =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(grossReservoirThickness);
  }

  private void addGrossRockVolume(SubmissionDetail submissionDetail,
                                  List<ProbabilityDistributionSummaryView> listOfSummaryViews) {
    var grossRockVolume =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME,
            submissionDetail);
    listOfSummaryViews.add(grossRockVolume);
  }

  private void addDepthToHydrocarbonContact(SubmissionDetail submissionDetail,
                                            List<ProbabilityDistributionSummaryView> listOfSummaryViews) {
    var depthToHydrocarbonContact =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT,
            submissionDetail);
    listOfSummaryViews.add(depthToHydrocarbonContact);
  }

  private void addDepthToOilWaterContact(SubmissionDetail submissionDetail,
                                         List<ProbabilityDistributionSummaryView> summaryViewList) {
    var depthToOilWaterContact =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
            submissionDetail
        );
    summaryViewList.add(depthToOilWaterContact);
  }

  private void addDepthToGasWaterContact(SubmissionDetail submissionDetail,
                                         List<ProbabilityDistributionSummaryView> listOfSummaryViews) {
    var depthToGasWaterContact =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
            submissionDetail
        );
    listOfSummaryViews.add(depthToGasWaterContact);
  }

  private void addDepthToGasOilContact(SubmissionDetail submissionDetail,
                                       List<ProbabilityDistributionSummaryView> listOfSummaryViews) {
    var depthToGasOilContact =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
            submissionDetail
        );
    listOfSummaryViews.add(depthToGasOilContact);
  }

  private void addPercentOfGrvOilBearing(SubmissionDetail submissionDetail,
                                         List<ProbabilityDistributionSummaryView> listOfSummaryViews) {
    var percentOfGrvOilBearing =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING,
            submissionDetail);
    listOfSummaryViews.add(percentOfGrvOilBearing);
  }
}
