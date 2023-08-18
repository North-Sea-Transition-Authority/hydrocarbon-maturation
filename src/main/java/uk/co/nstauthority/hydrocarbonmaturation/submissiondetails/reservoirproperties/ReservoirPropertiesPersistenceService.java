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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class ReservoirPropertiesPersistenceService {

  private final ReservoirPropertiesRepository reservoirPropertiesRepository;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  ReservoirPropertiesPersistenceService(ReservoirPropertiesRepository reservoirPropertiesRepository,
                                        ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.reservoirPropertiesRepository = reservoirPropertiesRepository;
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
        form.getLithology() == ReservoirLithology.OTHER ? form.getLithologyOther() : null
    );

    if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      reservoirProperty.setReservoirConnectivity(form.getReservoirConnectivity());
      reservoirProperty.setReservoirFacies(form.getReservoirFacies());
    }

    ProbabilityDistributionFieldInstance averageNetPayInstance;
    if (submissionDetail.isDryWellSubmission()) {
      averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL;
    } else if (submissionDetail.isDiscoverySubmission()) {
      averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY;
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
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        grossRockVolume,
        form.getGrossRockVolume(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    if (submissionDetail.isProspectSubmission()) {
      var depthToHydrocarbonContact =
          ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT;
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          depthToHydrocarbonContact,
          form.getDepthToHydrocarbonContact(),
          submissionDetail,
          reservoirSubmissionDetail
      );
    }

    var netPorosity = ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT;
    if (resourceType == ResourceType.DISCOVERY) {
      netPorosity = ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY;
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
    }
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        percentOfGrvOilBearing,
        form.getPercentOfGrvOilBearing(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    if (submissionDetail.isDiscoverySubmission()) {
      saveDiscoveryProbabilityDistributions(reservoirSubmissionDetail, form);
    }

    reservoirPropertiesRepository.save(reservoirProperty);
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

  private void saveDiscoveryProbabilityDistributions(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                     ReservoirPropertiesForm form) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
        reservoirSubmissionDetail);

    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
        form.getDepthToOilWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
        form.getDepthToGasWaterContact(),
        submissionDetail,
        reservoirSubmissionDetail);
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
        form.getDepthToGasOilContact(),
        submissionDetail,
        reservoirSubmissionDetail);
  }
}
