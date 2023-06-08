package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.EnumUtils;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;

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
  public void saveReservoirProperty(ReservoirPropertiesForm form, SubmissionDetail submissionDetail) {
    var reservoirProperty = getReservoirProperty(submissionDetail).orElse(new ReservoirProperty());
    reservoirProperty.setSubmissionDetail(submissionDetail);

    setFormation(form, reservoirProperty);
    setSubUnit(form, reservoirProperty);
    reservoirProperty.setAgeStage(form.getAgeStageOrSeries());
    reservoirProperty.setGroupName(form.getGroupName());
    reservoirProperty.setLithology(form.getLithology());
    reservoirProperty.setLithologyOther(
        form.getLithology() == ReservoirLithology.OTHER ? form.getLithologyOther() : null
    );

    if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      reservoirProperty.setReservoirConnectivity(form.getReservoirConnectivity());
      reservoirProperty.setReservoirFacies(form.getReservoirFacies());
    }

    if (form.getAverageNetPayThickness().getProbabilityDistributionType() != null) {
      ProbabilityDistributionFieldInstance averageNetPayInstance;
      if (submissionDetail.isDryWellSubmission()) {
        averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL;
      } else if (submissionDetail.isDiscoverySubmission()) {
        averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY;
      } else {
        averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT;
      }
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          averageNetPayInstance,
          form.getAverageNetPayThickness(),
          submissionDetail
      );
    }

    if (form.getGrossReservoirThickness().getProbabilityDistributionType() != null) {
      var grossReservoirThickness =
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          grossReservoirThickness,
          form.getGrossReservoirThickness(),
          submissionDetail
      );
    }

    if (form.getGrossRockVolume().getProbabilityDistributionType() != null) {
      var grossRockVolume = ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          grossRockVolume,
          form.getGrossRockVolume(),
          submissionDetail
      );
    }

    if (submissionDetail.isProspectSubmission()
        && form.getDepthToHydrocarbonContact().getProbabilityDistributionType() != null) {
      var depthToHydrocarbonContact =
          ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          depthToHydrocarbonContact,
          form.getDepthToHydrocarbonContact(),
          submissionDetail
      );
    }

    if (form.getNetPorosity().getProbabilityDistributionType() != null) {
      var netPorosity = ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          netPorosity,
          form.getNetPorosity(),
          submissionDetail
      );
    }

    if (form.getNetToGross().getProbabilityDistributionType() != null) {
      var netToGross = ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          netToGross,
          form.getNetToGross(),
          submissionDetail
      );
    }

    if (form.getPercentOfGrvOilBearing().getProbabilityDistributionType() != null) {
      var percentOfGrvOilBearing =
          ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING;
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          percentOfGrvOilBearing,
          form.getPercentOfGrvOilBearing(),
          submissionDetail
      );
    }

    if (submissionDetail.isDiscoverySubmission()) {
      saveDiscoveryProbabilityDistributions(submissionDetail, form);
    }

    reservoirPropertiesRepository.save(reservoirProperty);
  }

  Optional<ReservoirProperty> getReservoirProperty(SubmissionDetail submissionDetail) {
    return reservoirPropertiesRepository.findBySubmissionDetail(submissionDetail);
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

  private void saveDiscoveryProbabilityDistributions(SubmissionDetail submissionDetail,
                                                     ReservoirPropertiesForm form) {
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
        submissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
        submissionDetail);
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
        submissionDetail);

    if (form.getDepthToOilWaterContact().getProbabilityDistributionType() != null) {
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT,
          form.getDepthToOilWaterContact(),
          submissionDetail);
    }
    if (form.getDepthToGasWaterContact().getProbabilityDistributionType() != null) {
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
          form.getDepthToGasWaterContact(),
          submissionDetail);
    }
    if (form.getDepthToGasOilContact().getProbabilityDistributionType() != null) {
      probabilityDistributionPersistenceService.saveProbabilityDistribution(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
          form.getDepthToGasOilContact(),
          submissionDetail);
    }
  }
}
