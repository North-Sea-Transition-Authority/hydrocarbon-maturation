package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.EnumUtils;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class ReservoirPropertiesPersistenceService {

  private final ReservoirPropertiesRepository reservoirPropertiesRepository;
  private final BarrierBaffleDetailRepository barrierBaffleDetailRepository;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  ReservoirPropertiesPersistenceService(ReservoirPropertiesRepository reservoirPropertiesRepository,
                                        BarrierBaffleDetailRepository barrierBaffleDetailRepository,
                                        ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.reservoirPropertiesRepository = reservoirPropertiesRepository;
    this.barrierBaffleDetailRepository = barrierBaffleDetailRepository;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  @Transactional
  public void saveReservoirProperty(ReservoirPropertiesForm form, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var reservoirProperty = getReservoirProperty(reservoirSubmissionDetail).orElse(new ReservoirProperty());
    reservoirProperty.setReservoirSubmissionDetail(reservoirSubmissionDetail);

    setFormation(form, reservoirProperty);
    setSubUnit(form, reservoirProperty);
    setAgeStage(form, reservoirProperty);

    reservoirProperty.setGroupName(form.getGroupName());
    reservoirProperty.setLithology(form.getLithology());
    reservoirProperty.setLithologyOther(
        form.getLithology() == StandardLithology.OTHER ? form.getLithologyOther() : null
    );

    if (submissionDetail.isDryWellOrDiscoverySubmission() || submissionDetail.isFieldSubmission()) {
      reservoirProperty.setReservoirConnectivity(form.getReservoirConnectivity());
      reservoirProperty.setReservoirFacies(form.getReservoirFacies());
    }

    ProbabilityDistributionFieldInstance averageNetPayInstance;
    if (submissionDetail.isDryWellSubmission()) {
      averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL;
    } else if (submissionDetail.isDiscoverySubmission()) {
      averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY;
    } else if (submissionDetail.isFieldSubmission()) {
      averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD;
    } else {
      averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        averageNetPayInstance,
        form.getAverageNetPayThickness(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var resourceType = submissionDetail.getSubmission().getResource().getType();

    var grossReservoirThickness =
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      grossReservoirThickness = ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY;
    } else if (resourceType == ResourceType.FIELD) {
      grossReservoirThickness = ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        grossReservoirThickness,
        form.getGrossReservoirThickness(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var grossRockVolume = ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      grossRockVolume = ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY;
    } else if (resourceType == ResourceType.FIELD) {
      grossRockVolume = ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        grossRockVolume,
        form.getGrossRockVolume(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    if (submissionDetail.isProspectSubmission()) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT,
          form.getDepthToHydrocarbonContact(),
          submissionDetail,
          reservoirSubmissionDetail
      );
    }

    var netPorosity = ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      netPorosity = ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY;
    } else if (resourceType == ResourceType.FIELD) {
      netPorosity = ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        netPorosity,
        form.getNetPorosity(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var netToGross = ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      netToGross = ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY;
    } else if (submissionDetail.isFieldSubmission()) {
      netToGross = ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        netToGross,
        form.getNetToGross(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var percentOfGrvOilBearing =
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      percentOfGrvOilBearing = ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY;
    } else if (resourceType == ResourceType.FIELD) {
      percentOfGrvOilBearing = ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD;
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        percentOfGrvOilBearing,
        form.getPercentOfGrvOilBearing(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    if (submissionDetail.isDiscoverySubmission()) {
      saveDiscoveryProbabilityDistributions(reservoirSubmissionDetail, form);
    } else if (submissionDetail.isFieldSubmission()) {
      saveFieldProbabilityDistributions(reservoirSubmissionDetail, form);

      saveFracturePressure(form, reservoirProperty);
      saveDepositionalSettingAndEnvironment(form, reservoirProperty);

      reservoirProperty.setLateralBarriersBaffles(form.getIsLateralBarriersBaffles());
      reservoirProperty.setVerticalBarriersBaffles(form.getIsVerticalBarriersBaffles());
    }

    var savedReservoirProperty = reservoirPropertiesRepository.save(reservoirProperty);

    if (submissionDetail.isFieldSubmission()) {
      // save reservoir property first to assign reservoir_property_id used in barrier_baffles table.
      saveBarrierBafflesDetail(form, savedReservoirProperty);
    }
  }

  Optional<ReservoirProperty> getReservoirProperty(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirPropertiesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  private void setFormation(ReservoirPropertiesForm form, ReservoirProperty reservoirProperty) {
    if (!Objects.isNull(form.getFormationName())) {
      if (SearchSelectorService.isManualEntry(form.getFormationName())) {
        reservoirProperty.setFormationNameManual(SearchSelectorService.removePrefix(form.getFormationName()));
        reservoirProperty.setFormationName(null);
      } else {
        reservoirProperty.setFormationName(EnumUtils.getEnumValue(Formation.class, form.getFormationName()));
        reservoirProperty.setFormationNameManual(null);
      }
    } else {
      reservoirProperty.setFormationName(null);
      reservoirProperty.setFormationNameManual(null);
    }
  }

  private void setSubUnit(ReservoirPropertiesForm form, ReservoirProperty reservoirProperty) {
    if (!Objects.isNull(form.getSubUnit())) {
      if (SearchSelectorService.isManualEntry(form.getSubUnit())) {
        reservoirProperty.setSubUnitManual(SearchSelectorService.removePrefix(form.getSubUnit()));
        reservoirProperty.setSubUnit(null);
      } else {
        reservoirProperty.setSubUnit(EnumUtils.getEnumValue(ReservoirSubUnit.class, form.getSubUnit()));
        reservoirProperty.setSubUnitManual(null);
      }
    } else {
      reservoirProperty.setSubUnit(null);
      reservoirProperty.setSubUnitManual(null);
    }
  }

  private void setAgeStage(ReservoirPropertiesForm form, ReservoirProperty reservoirProperty) {
    reservoirProperty.setAgeStage(form.getAgeStageOrSeries());
    reservoirProperty.setAgeStageManual(null);
  }

  private void saveDiscoveryDepthToProbabilityDistributions(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                        ReservoirPropertiesForm form) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY,
        reservoirSubmissionDetail);

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY,
        form.getDepthToOilWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY,
        form.getDepthToGasWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY,
        form.getDepthToGasOilContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD,
        form.getDepthToGasOilContact(),
        submissionDetail,
        reservoirSubmissionDetail);
  }

  private void saveFieldDepthToProbabilityDistributions(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                        ReservoirPropertiesForm form) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD,
        reservoirSubmissionDetail);

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD,
        form.getDepthToOilWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD,
        form.getDepthToGasWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD,
        form.getDepthToGasOilContact(),
        submissionDetail,
        reservoirSubmissionDetail);
  }

  private void saveFieldOriginalDepthProbabilityDistributions(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                              ReservoirPropertiesForm form) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD,
        reservoirSubmissionDetail);

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD,
        form.getOriginalDepthToOilWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD,
        form.getOriginalDepthToGasWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD,
        form.getOriginalDepthToGasOilContact(),
        submissionDetail,
        reservoirSubmissionDetail);
  }

  private void saveFieldDepthToMovedProbabilityDistributions(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                              ReservoirPropertiesForm form) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD,
        reservoirSubmissionDetail);

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD,
        form.getDepthToMovedOilWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD,
        form.getDepthToMovedGasWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD,
        form.getDepthToMovedGasOilContact(),
        submissionDetail,
        reservoirSubmissionDetail);
  }

  private void saveDiscoveryProbabilityDistributions(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                     ReservoirPropertiesForm form) {
    saveDiscoveryDepthToProbabilityDistributions(reservoirSubmissionDetail, form);
  }

  private void saveFieldProbabilityDistributions(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                 ReservoirPropertiesForm form) {
    if (reservoirSubmissionDetail.getSubmissionDetail().isCessationOfProductionSubmission()) {
      saveFieldOriginalDepthProbabilityDistributions(reservoirSubmissionDetail, form);
      saveFieldDepthToMovedProbabilityDistributions(reservoirSubmissionDetail, form);
    } else {
      saveFieldDepthToProbabilityDistributions(reservoirSubmissionDetail, form);
    }
  }

  private void saveFracturePressure(ReservoirPropertiesForm form, ReservoirProperty reservoirProperty) {
    reservoirProperty.setFracturePressure(form.getFracturePressure().createBigDecimalOrNull());
  }

  private void saveDepositionalSettingAndEnvironment(ReservoirPropertiesForm form, ReservoirProperty reservoirProperty) {
    if (form.getDepositionalSetting() != null) {
      var depositionalSetting = EnumUtils.getEnumValue(DepositionalSetting.class, form.getDepositionalSetting());
      reservoirProperty.setDepositionalSetting(depositionalSetting);

      var depositionalEnvironmentFromSetting = form.getDepositionalEnvironmentMap().get(depositionalSetting);
      if (depositionalEnvironmentFromSetting != null) {
        var depositionalEnvironment = EnumUtils.getEnumValue(DepositionalEnvironment.class, depositionalEnvironmentFromSetting);
        reservoirProperty.setDepositionalEnvironment(depositionalEnvironment);
      }
    }
  }

  private void saveBarrierBafflesDetail(ReservoirPropertiesForm form, ReservoirProperty reservoirProperty) {
    clearLateralBarrierBaffles(reservoirProperty);
    clearVerticalBarrierBaffles(reservoirProperty);

    // save new details
    if (form.getIsLateralBarriersBaffles() != null && form.getIsLateralBarriersBaffles()) {
      for (var barrierBaffleName : form.getLateralBarriersBafflesDetail()) {
        var barrierBaffle = BarrierBaffle.valueOf(barrierBaffleName);
        barrierBaffleDetailRepository.save(
            new BarrierBaffleDetail(reservoirProperty, barrierBaffle, VerticalLateralType.LATERAL));
      }
    }
    if (form.getIsVerticalBarriersBaffles() != null && form.getIsVerticalBarriersBaffles()) {
      for (var barrierBaffleName : form.getVerticalBarriersBafflesDetail()) {
        var barrierBaffle = BarrierBaffle.valueOf(barrierBaffleName);
        barrierBaffleDetailRepository.save(
            new BarrierBaffleDetail(reservoirProperty, barrierBaffle, VerticalLateralType.VERTICAL));
      }
    }
  }

  private void clearLateralBarrierBaffles(ReservoirProperty reservoirProperty) {
    barrierBaffleDetailRepository
        .deleteAllByReservoirPropertyAndVerticalLateralType(reservoirProperty, VerticalLateralType.LATERAL);
  }

  private void clearVerticalBarrierBaffles(ReservoirProperty reservoirProperty) {
    barrierBaffleDetailRepository
        .deleteAllByReservoirPropertyAndVerticalLateralType(reservoirProperty, VerticalLateralType.VERTICAL);
  }
}
