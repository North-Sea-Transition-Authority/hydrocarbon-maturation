package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class ReservoirPropertiesSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {
  public static final String RESERVOIR_PROPERTIES = "Reservoir properties";
  public static final String FORMATION_NAME = "Formation name";
  public static final String SUB_UNIT = "Sub unit";
  public static final String AGE_STAGE_OR_SERIES = "Age, stage, or series";
  public static final String GROUP_NAME = "Group name";
  public static final String LITHOLOGY = "Reservoir lithology";
  public static final String LITHOLOGY_OTHER = "Other lithology";
  public static final String AVERAGE_NET_PAY_THICKNESS = "Average net pay thickness";
  public static final String GROSS_RESERVOIR_THICKNESS = "Gross reservoir thickness";
  public static final String GROSS_ROCK_VOLUME = "Gross rock volume";
  public static final String PERCENT_OF_GRV_OIL_BEARING = "Percent of GRV oil-bearing";
  public static final String NET_TO_GROSS = "Net to gross";
  public static final String NET_POROSITY = "Net porosity";
  public static final String DEPTH_TO_HYDROCARBON_CONTACT = "Depth to hydrocarbon contact";
  public static final String DEPTH_TO_OIL_WATER_CONTACT = "Depth to oil-water contact";
  public static final String DEPTH_TO_GAS_WATER_CONTACT = "Depth to gas-water contact";
  public static final String DEPTH_TO_GAS_OIL_CONTACT = "Depth to gas-oil contact";
  public static final String ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT = "Original depth to oil-water contact";
  public static final String ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT = "Original depth to gas-water contact";
  public static final String ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT = "Original depth to gas-oil contact";
  public static final String DEPTH_TO_MOVED_OIL_WATER_CONTACT = "Depth to moved oil-water contact";
  public static final String DEPTH_TO_MOVED_GAS_WATER_CONTACT = "Depth to moved gas-water contact";
  public static final String DEPTH_TO_MOVED_GAS_OIL_CONTACT = "Depth to moved gas-oil contact";
  public static final String RESERVOIR_FACIES = "Reservoir facies";
  public static final String RESERVOIR_CONNECTIVITY = "Reservoir connectivity";


  public static final int RESERVOIR_PROPERTIES_DISPLAY_ORDER = 30;

  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public ReservoirPropertiesSummaryService(
      ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
      GeneralReservoirInformationService generalReservoirInformationService) {
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  public ReservoirPropertiesSummaryView getReservoirPropertiesSummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirPropertyOptional = reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail);
    return reservoirPropertyOptional.map(
            reservoirProperty -> createSummaryViewFromReservoirProperty(reservoirSubmissionDetail, reservoirProperty))
        .orElse(createSummaryViewFromReservoirProperty(reservoirSubmissionDetail, new ReservoirProperty()));
  }

  private ReservoirPropertiesSummaryView createSummaryViewFromReservoirProperty(
      ReservoirSubmissionDetail reservoirSubmissionDetail,
      ReservoirProperty reservoirProperty) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

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
    var listOfDepthToContactDistributions  = getDiscoveryProbabilityTableSummaryViews(reservoirSubmissionDetail, listOfInstances);

    addNetToGross(submissionDetail, listOfInstances);
    addNetPorosity(submissionDetail, listOfInstances);

    if (submissionDetail.isProspectSubmission()) {
      addDepthToHydrocarbonContact(listOfInstances);
    }

    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfInstances.forEach(instance -> listOfSummaryViews.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail, reservoirSubmissionDetail)));

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

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryItems = List.of(
        getReservoirPropertiesSummary(reservoirSubmissionDetail),
        getProbabilityDistributionsSummary(reservoirSubmissionDetail)
    );
    return Optional.of(new SummarySection(RESERVOIR_PROPERTIES_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getReservoirPropertiesSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail).ifPresentOrElse(
        reservoirProperty -> {
          summaryDataView.addKeyValue(FORMATION_NAME,
              reservoirProperty.getFormationName().getDisplayName());
          summaryDataView.addKeyValue(SUB_UNIT,
              reservoirProperty.getSubUnit().getDisplayName());
          summaryDataView.addKeyValue(AGE_STAGE_OR_SERIES,
              reservoirProperty.getAgeStage().getDisplayName());
          summaryDataView.addKeyValue(GROUP_NAME,
              reservoirProperty.getGroupName().getDisplayName());

          var lithology = reservoirProperty.getLithology();
          if (ReservoirLithology.OTHER.equals(lithology)) {
            summaryDataView.addKeyValue(LITHOLOGY,
                reservoirProperty.getLithologyOther());
          } else {
            summaryDataView.addKeyValue(LITHOLOGY,
                reservoirProperty.getLithology().getDisplayName());
          }

          summaryDataView.addKeyValue(RESERVOIR_FACIES,
              reservoirProperty.getReservoirFacies());
          summaryDataView.addKeyValue(RESERVOIR_CONNECTIVITY,
              reservoirProperty.getReservoirConnectivity());
        }, () -> {
          summaryDataView.addKeyValue(FORMATION_NAME, "");
          summaryDataView.addKeyValue(SUB_UNIT, "");
          summaryDataView.addKeyValue(AGE_STAGE_OR_SERIES, "");
          summaryDataView.addKeyValue(GROUP_NAME, "");
          summaryDataView.addKeyValue(LITHOLOGY, "");
          summaryDataView.addKeyValue(RESERVOIR_FACIES, "");
          summaryDataView.addKeyValue(RESERVOIR_CONNECTIVITY, "");
        });

    var summaryCard1 = SummaryCard.simpleSummaryCard(
        summaryDataView
    );

    return SummaryItem.withCards(RESERVOIR_PROPERTIES, List.of(summaryCard1));
  }

  private SummaryItem getProbabilityDistributionsSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);
    if (generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY);
    }
    if (generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY);
    }
    if (generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY);
    }

    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, reservoirSubmissionDetail
        );

    return SummaryItem.withCard(RESERVOIR_PROPERTIES, SummaryCard.probabilitySummaryCard(listOfSummaryViews));
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
      ReservoirSubmissionDetail reservoirSubmissionDetail,
      List<ProbabilityDistributionFieldInstance> listOfInstances) {
    var listOfDepthToContactInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    if (submissionDetail.isDiscoverySubmission()) {
      generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
          .map(ReservoirHydrocarbons::getHydrocarbonsDiscovered)
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
            instance, submissionDetail, reservoirSubmissionDetail)));
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
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT);
  }

  private void addDepthToOilWaterContact(List<ProbabilityDistributionFieldInstance> listOfInstances) {
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY);
  }

  private void addDepthToGasWaterContact(List<ProbabilityDistributionFieldInstance> listOfInstances) {
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY);
  }

  private void addDepthToGasOilContact(List<ProbabilityDistributionFieldInstance> listOfInstances) {
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY);
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
