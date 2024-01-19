package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
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
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class ReservoirPropertiesSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {
  public static final String RESERVOIR_PROPERTIES = "Reservoir properties";
  public static final String FORMATION_NAME = "Formation name";
  public static final String SUB_UNIT = "Sub unit";
  public static final String AGE_STAGE_OR_SERIES = "Age, stage, or series";
  public static final String GROUP_NAME = "Group name";
  public static final String LITHOLOGY = "Reservoir lithology";
  public static final String RESERVOIR_FACIES = "Reservoir facies";
  public static final String RESERVOIR_CONNECTIVITY = "Reservoir connectivity";
  public static final String RESERVOIR_FRACTURE_PRESSURE = "Reservoir fracture pressure";
  public static final String DEPOSITIONAL_SETTING = "Depositional setting";
  public static final String DEPOSITIONAL_ENVIRONMENT = "Depositional environment";
  public static final String IS_LATERAL_BARRIERS_BAFFLES = "Are there any lateral barriers baffles?";
  public static final String LATERAL_BARRIERS_BAFFLES = "Lateral barriers baffles";
  public static final String IS_VERTICAL_BARRIERS_BAFFLES = "Are there any vertical barriers baffles?";
  public static final String VERTICAL_BARRIERS_BAFFLES = "Vertical barriers baffles";

  public static final int RESERVOIR_PROPERTIES_DISPLAY_ORDER = 30;

  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final BarrierBaffleDetailRepository barrierBaffleDetailRepository;

  @Autowired
  public ReservoirPropertiesSummaryService(
      ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
      GeneralReservoirInformationService generalReservoirInformationService,
      BarrierBaffleDetailRepository barrierBaffleDetailRepository) {
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.barrierBaffleDetailRepository = barrierBaffleDetailRepository;
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
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    SummaryItem probabilityDistributionSummary;
    SummaryItem reservoirPropertiesSummary;
    if (submissionDetail.isFieldSubmission()) {
      reservoirPropertiesSummary = getFieldReservoirPropertiesSummary(reservoirSubmissionDetail);
      probabilityDistributionSummary = getFieldProbabilityDistributionSummary(reservoirSubmissionDetail);
    } else if (submissionDetail.isDiscoverySubmission()) {
      reservoirPropertiesSummary = getDiscoveryReservoirPropertiesSummary(reservoirSubmissionDetail);
      probabilityDistributionSummary = getDiscoveryProbabilityDistributionSummary(reservoirSubmissionDetail);
    } else {
      return Optional.empty();
    }

    var summaryItems = List.of(
        reservoirPropertiesSummary,
        probabilityDistributionSummary
    );
    return Optional.of(new SummarySection(RESERVOIR_PROPERTIES_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getFieldProbabilityDistributionSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirHydrocarbonsOptional = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail);

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD);

    reservoirHydrocarbonsOptional.ifPresent(
        reservoirHydrocarbons -> {
          if (generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
            listOfInstances.add(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD);
          }
        }
    );

    listOfInstances.add(
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD);

    addFieldFluidContactInstances(reservoirSubmissionDetail, reservoirHydrocarbonsOptional, listOfInstances);

    var summaryViews =
        getSummaryViewsFromInstances(listOfInstances, reservoirSubmissionDetail);
    return new SummaryItem(RESERVOIR_PROPERTIES, List.of(SummaryCard.minMaxMostLikelySummaryCard(summaryViews)));
  }

  private void addFieldFluidContactInstances(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                             Optional<ReservoirHydrocarbons> reservoirHydrocarbonsOptional,
                                             ArrayList<ProbabilityDistributionFieldInstance> listOfInstances) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      return;
    }
    reservoirHydrocarbonsOptional.ifPresent(
        reservoirHydrocarbons -> {
          boolean isOilOnly = generalReservoirInformationService.isOilOnly(reservoirHydrocarbons);
          boolean isGasOnly = generalReservoirInformationService.isGasOnly(reservoirHydrocarbons);
          boolean isCombinedOilAndGas = generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons);

          if (reservoirSubmissionDetail.getSubmissionDetail().isCessationOfProductionSubmission()) {
            addCopFieldFluidContactInstances(listOfInstances, isOilOnly, isCombinedOilAndGas, isGasOnly);
          } else {
            addNonCopFieldFluidContactInstances(listOfInstances, isOilOnly, isCombinedOilAndGas, isGasOnly);
          }
        });
  }

  private static void addNonCopFieldFluidContactInstances(ArrayList<ProbabilityDistributionFieldInstance> listOfInstances,
                                                          boolean isOilOnly,
                                                          boolean isCombinedOilAndGas,
                                                          boolean isGasOnly) {
    if (isOilOnly || isCombinedOilAndGas) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD);
    }
    if (isGasOnly || isCombinedOilAndGas) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD);
    }
    if (isCombinedOilAndGas) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD);
    }
  }

  private static void addCopFieldFluidContactInstances(ArrayList<ProbabilityDistributionFieldInstance> listOfInstances,
                                                       boolean isOilOnly,
                                                       boolean isCombinedOilAndGas,
                                                       boolean isGasOnly) {
    if (isOilOnly || isCombinedOilAndGas) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD);
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD);
    }
    if (isGasOnly || isCombinedOilAndGas) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD);
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD);
    }
    if (isCombinedOilAndGas) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD);
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD);
    }
  }

  private SummaryItem getDiscoveryProbabilityDistributionSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirHydrocarbonsOptional = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail);

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);

    reservoirHydrocarbonsOptional.ifPresent(
        reservoirHydrocarbons -> {
          if (generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
            listOfInstances.add(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY);
          }
        }
    );

    listOfInstances.add(
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);

    addDiscoveryFluidContactInstances(reservoirHydrocarbonsOptional, listOfInstances);

    var summaryViews =
        getSummaryViewsFromInstances(listOfInstances, reservoirSubmissionDetail);
    return SummaryItem.withCard(RESERVOIR_PROPERTIES, SummaryCard.probabilitySummaryCard(summaryViews));
  }

  private void addDiscoveryFluidContactInstances(Optional<ReservoirHydrocarbons> reservoirHydrocarbonsOptional,
                                                 ArrayList<ProbabilityDistributionFieldInstance> listOfInstances) {
    reservoirHydrocarbonsOptional.ifPresent(
        reservoirHydrocarbons -> {
          boolean isOilOnly = generalReservoirInformationService.isOilOnly(reservoirHydrocarbons);
          boolean isGasOnly = generalReservoirInformationService.isGasOnly(reservoirHydrocarbons);
          boolean isCombinedOilAndGas = generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons);

          if (isOilOnly || isCombinedOilAndGas) {
            listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY);
          }
          if (isGasOnly || isCombinedOilAndGas) {
            listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY);
          }
          if (isCombinedOilAndGas) {
            listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY);
          }
        });
  }

  private SummaryItem getDiscoveryReservoirPropertiesSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryCard = SummaryCard.simpleSummaryCard(
        getCommonReservoirPropertiesSummaryDataView(reservoirSubmissionDetail)
    );

    return SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard);
  }

  private SummaryItem getFieldReservoirPropertiesSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryDataView = getCommonReservoirPropertiesSummaryDataView(reservoirSubmissionDetail);

    reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail).ifPresentOrElse(
        reservoirProperty -> {
          summaryDataView.addKeyValue(RESERVOIR_FRACTURE_PRESSURE,
              NumberUtil.getNumberStringWithUnitsOrEmpty(reservoirProperty.getFracturePressure(), "psi"));

          summaryDataView.addKeyValue(DEPOSITIONAL_SETTING,
              DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(reservoirProperty.getDepositionalSetting()));
          summaryDataView.addKeyValue(DEPOSITIONAL_ENVIRONMENT,
              DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(reservoirProperty.getDepositionalEnvironment()));

          summaryDataView.addKeyValue(IS_LATERAL_BARRIERS_BAFFLES,
              BooleanUtil.yesNoFromBoolean(reservoirProperty.getLateralBarriersBaffles()));
          if (BooleanUtils.isTrue(reservoirProperty.getLateralBarriersBaffles())) {
            summaryDataView.addKeyValue(LATERAL_BARRIERS_BAFFLES,
                getStringOfBarrierBafflesSelected(reservoirProperty, VerticalLateralType.LATERAL));
          }

          summaryDataView.addKeyValue(IS_VERTICAL_BARRIERS_BAFFLES,
              BooleanUtil.yesNoFromBoolean(reservoirProperty.getVerticalBarriersBaffles()));
          if (BooleanUtils.isTrue(reservoirProperty.getVerticalBarriersBaffles())) {
            summaryDataView.addKeyValue(VERTICAL_BARRIERS_BAFFLES,
                getStringOfBarrierBafflesSelected(reservoirProperty, VerticalLateralType.VERTICAL));
          }
        },
        () -> {
          summaryDataView.addKeyValue(RESERVOIR_FRACTURE_PRESSURE, "");
          summaryDataView.addKeyValue(DEPOSITIONAL_SETTING, "");
          summaryDataView.addKeyValue(DEPOSITIONAL_ENVIRONMENT, "");
          summaryDataView.addKeyValue(IS_LATERAL_BARRIERS_BAFFLES, "");
          summaryDataView.addKeyValue(IS_VERTICAL_BARRIERS_BAFFLES, "");
        });

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);

    return SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard);
  }

  @NotNull
  private String getStringOfBarrierBafflesSelected(ReservoirProperty reservoirProperty, VerticalLateralType verticalLateralType) {
    StringBuilder barrierBafflesBuilder = new StringBuilder();

    barrierBaffleDetailRepository
        .findAllByReservoirPropertyAndVerticalLateralType(reservoirProperty, verticalLateralType)
        .stream()
        .map(barrierBaffleDetail -> barrierBaffleDetail.getBarrierBaffle().getDisplayName().toLowerCase())
        .sorted()
        .forEach(barrierBaffle -> barrierBafflesBuilder.append("%s, ".formatted(barrierBaffle)));
    // remove last space and comma
    barrierBafflesBuilder.deleteCharAt(barrierBafflesBuilder.length() - 1);
    barrierBafflesBuilder.deleteCharAt(barrierBafflesBuilder.length() - 1);
    //  make first letter upper case
    barrierBafflesBuilder.setCharAt(0, Character.toUpperCase(barrierBafflesBuilder.charAt(0)));

    return barrierBafflesBuilder.toString();
  }

  private SummaryDataView getCommonReservoirPropertiesSummaryDataView(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail).ifPresentOrElse(
        reservoirProperty -> {
          var ageStage = reservoirProperty.getAgeStage();
          summaryDataView.addKeyValue(AGE_STAGE_OR_SERIES, ageStage != null
              ? ageStage.getDisplayName()
              : reservoirProperty.getAgeStageManual());

          var groupName = reservoirProperty.getGroupName();
          summaryDataView.addKeyValue(GROUP_NAME, groupName != null
              ? reservoirProperty.getGroupName().getDisplayName()
              : "");

          var formationName = reservoirProperty.getFormationName();
          summaryDataView.addKeyValue(FORMATION_NAME, formationName != null
              ? formationName.getDisplayName()
              : reservoirProperty.getFormationNameManual());

          var subUnit = reservoirProperty.getSubUnit();
          summaryDataView.addKeyValue(SUB_UNIT, subUnit != null
              ? subUnit.getDisplayName()
              : reservoirProperty.getSubUnitManual());

          var lithology = reservoirProperty.getLithology();
          if (StandardLithology.OTHER.equals(lithology)) {
            summaryDataView.addKeyValue(LITHOLOGY,
                reservoirProperty.getLithologyOther());
          } else if (lithology != null) {
            summaryDataView.addKeyValue(LITHOLOGY,
                lithology.getDisplayName());
          } else {
            summaryDataView.addKeyValue(LITHOLOGY,
                "");
          }

          summaryDataView.addKeyValue(RESERVOIR_FACIES,
              reservoirProperty.getReservoirFacies());
          summaryDataView.addKeyValue(RESERVOIR_CONNECTIVITY,
              reservoirProperty.getReservoirConnectivity());
        }, () -> {
          summaryDataView.addKeyValue(AGE_STAGE_OR_SERIES, "");
          summaryDataView.addKeyValue(GROUP_NAME, "");
          summaryDataView.addKeyValue(FORMATION_NAME, "");
          summaryDataView.addKeyValue(SUB_UNIT, "");
          summaryDataView.addKeyValue(LITHOLOGY, "");
          summaryDataView.addKeyValue(RESERVOIR_FACIES, "");
          summaryDataView.addKeyValue(RESERVOIR_CONNECTIVITY, "");
        });

    return summaryDataView;
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
    return reservoirProperty.getLithology() == StandardLithology.OTHER ? lithologyOther : lithology;
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

  private List<ProbabilityDistributionSummaryView> getSummaryViewsFromInstances(
      List<ProbabilityDistributionFieldInstance> listOfInstances,
      ReservoirSubmissionDetail reservoirSubmissionDetail) {

    return probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, reservoirSubmissionDetail.getSubmissionDetail(), reservoirSubmissionDetail);
  }
}
