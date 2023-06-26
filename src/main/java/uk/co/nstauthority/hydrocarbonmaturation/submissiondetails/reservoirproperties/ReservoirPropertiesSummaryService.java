package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
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
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();

    if (submissionDetail.isDryWellSubmission()) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL);
    } else if (submissionDetail.isDiscoverySubmission()) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    } else {
      listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT);
    }

    addGrossReservoirThickness(submissionDetail, listOfInstances);
    addGrossRockVolume(submissionDetail, listOfInstances);
    if (submissionDetail.isDryWellSubmission()) {
      addPercentOfGrvOilBearing(submissionDetail, listOfInstances);
    } else if (submissionDetail.isProspectSubmission()) {
      hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail)
          .ifPresent(hydrocarbonCaseDetail -> {
            if (hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail)) {
              addPercentOfGrvOilBearing(submissionDetail, listOfInstances);
            }
          });
    }
    var listOfDepthToContactDistributions  = getDiscoveryProbabilityTableSummaryViews(submissionDetail, listOfInstances);

    addNetToGross(submissionDetail, listOfInstances);
    addNetPorosity(submissionDetail, listOfInstances);

    if (submissionDetail.isProspectSubmission()) {
      addDepthToHydrocarbonContact(listOfInstances);
    }

    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfInstances.forEach(instance -> listOfSummaryViews.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail)));

    return new ReservoirPropertiesSummaryView(
        submissionDetail.getSubmissionType(),
        getFormationDisplay(reservoirProperty),
        getSubUnitDisplay(reservoirProperty),
        getAgeStage(reservoirProperty),
        reservoirProperty.getGroupName() != null ? reservoirProperty.getGroupName().getDisplayName() : "",
        getLithologyDisplay(reservoirProperty),
        Objects.requireNonNullElse(reservoirProperty.getReservoirFacies(), ""),
        Objects.requireNonNullElse(reservoirProperty.getReservoirConnectivity(), ""),
        listOfSummaryViews,
        listOfDepthToContactDistributions
    );
  }

  private String getFormationDisplay(ReservoirProperty reservoirProperty) {
    String formationName = "";
    if (reservoirProperty.getFormationName() != null) {
      formationName = reservoirProperty.getFormationName().getDisplayName();
    } else if (reservoirProperty.getFormationNameManual() != null) {
      formationName = reservoirProperty.getFormationNameManual();
    }
    return formationName;
  }

  private String getSubUnitDisplay(ReservoirProperty reservoirProperty) {
    String subUnit = "";
    if (reservoirProperty.getSubUnit() != null) {
      subUnit = reservoirProperty.getSubUnit().getDisplayName();
    } else if (reservoirProperty.getSubUnitManual() != null) {
      subUnit = reservoirProperty.getSubUnitManual();
    }
    return subUnit;
  }

  private String getAgeStage(ReservoirProperty reservoirProperty) {
    if (reservoirProperty.getAgeStage() != null) {
      return reservoirProperty.getAgeStage().getDisplayName();
    } else if (reservoirProperty.getAgeStageManual() != null) {
      return reservoirProperty.getAgeStageManual();
    }
    return "";
  }

  private String getLithologyDisplay(ReservoirProperty reservoirProperty) {
    var lithology = reservoirProperty.getLithology() != null ? reservoirProperty.getLithology().getDisplayName() : "";
    var lithologyOther = ObjectUtils.defaultIfNull(reservoirProperty.getLithologyOther(), "");
    return reservoirProperty.getLithology() == ReservoirLithology.OTHER ? lithologyOther : lithology;
  }

  private void addNetPorosity(SubmissionDetail submissionDetail,
                              List<ProbabilityDistributionFieldInstance> listOfInstances) {
    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);
    }
  }

  private void addNetToGross(SubmissionDetail submissionDetail,
                             List<ProbabilityDistributionFieldInstance> listOfInstances) {
    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    }
  }

  private List<ProbabilityDistributionSummaryView> getDiscoveryProbabilityTableSummaryViews(
      SubmissionDetail submissionDetail,
      List<ProbabilityDistributionFieldInstance> listOfInstances) {
    var listOfDepthToContactInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    if (submissionDetail.isDiscoverySubmission()) {
      discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
          .map(DiscoveryWellDetail::getHydrocarbonsDiscovered)
          .ifPresent(hydrocarbonsDiscovered -> {
            if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
                || hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
              addDepthToOilWaterContact(listOfDepthToContactInstances);
            }
            if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
                || hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
              addDepthToGasWaterContact(listOfDepthToContactInstances);
            }
            if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
              addPercentOfGrvOilBearing(submissionDetail, listOfInstances);
              addDepthToGasOilContact(listOfDepthToContactInstances);
            }
          });
    }

    var depthToContactDistributions = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfDepthToContactInstances.forEach(instance -> depthToContactDistributions.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail)));
    return depthToContactDistributions;
  }

  private void addGrossReservoirThickness(SubmissionDetail submissionDetail,
                                          List<ProbabilityDistributionFieldInstance> listOfInstances) {
    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    }
  }

  private void addGrossRockVolume(SubmissionDetail submissionDetail,
                                  List<ProbabilityDistributionFieldInstance> listOfInstances) {
    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);
    }
  }

  private void addDepthToHydrocarbonContact(List<ProbabilityDistributionFieldInstance> listOfInstances) {
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT);
  }

  private void addDepthToOilWaterContact(List<ProbabilityDistributionFieldInstance> listOfInstances) {
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT);
  }

  private void addDepthToGasWaterContact(List<ProbabilityDistributionFieldInstance> listOfInstances) {
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT);
  }

  private void addDepthToGasOilContact(List<ProbabilityDistributionFieldInstance> listOfInstances) {
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT);
  }

  private void addPercentOfGrvOilBearing(SubmissionDetail submissionDetail,
                                         List<ProbabilityDistributionFieldInstance> listOfInstances) {
    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY);
    }
  }
}
