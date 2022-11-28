package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@Service
class ReservoirPropertiesPersistenceService {

  private final ReservoirPropertiesRepository reservoirPropertiesRepository;
  private final ProbabilityDistributionPersistenceService probDistributionService;

  @Autowired
  public ReservoirPropertiesPersistenceService(ReservoirPropertiesRepository reservoirPropertiesRepository,
                                               ProbabilityDistributionPersistenceService probDistributionService) {
    this.reservoirPropertiesRepository = reservoirPropertiesRepository;
    this.probDistributionService = probDistributionService;
  }

  @Transactional
  public void saveReservoirProperty(ReservoirPropertiesForm form, SubmissionDetail submissionDetail) {
    var reservoirProperty = getReservoirProperty(submissionDetail).orElse(new ReservoirProperty());
    var submissionType = submissionDetail.getSubmissionType();

    reservoirProperty.setSubmissionDetail(submissionDetail);
    reservoirProperty.setFormationName(form.getFormationName());
    reservoirProperty.setAgeStage(form.getAgeStageOrSeries());
    reservoirProperty.setLithology(form.getLithology());

    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      reservoirProperty.setReservoirConnectivity(form.getReservoirConnectivity());
      reservoirProperty.setReservoirFacies(form.getReservoirFacies());
    }

    if (form.getAverageNetPayThickness().getProbabilityDistributionType() != null) {
      ProbabilityDistributionFieldInstance averageNetPayInstance;
      if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
        averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY;
      } else {
        averageNetPayInstance = ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT;
      }
      probDistributionService.saveProbabilityDistribution(
          averageNetPayInstance,
          form.getAverageNetPayThickness(),
          submissionDetail
      );
    }

    if (form.getGrossReservoirThickness().getProbabilityDistributionType() != null) {
      var grossReservoirThickness =
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY;
      probDistributionService.saveProbabilityDistribution(
          grossReservoirThickness,
          form.getGrossReservoirThickness(),
          submissionDetail
      );
    }

    if (form.getGrossRockVolume().getProbabilityDistributionType() != null) {
      var grossRockVolume = ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME;
      probDistributionService.saveProbabilityDistribution(
          grossRockVolume,
          form.getGrossRockVolume(),
          submissionDetail
      );
    }

    if ((submissionType == SubmissionType.CREATE_PROSPECT || submissionType == SubmissionType.PROSPECT_UPDATE)
        && (form.getDepthToHydrocarbonContact().getProbabilityDistributionType() != null)) {
      var depthToHydrocarbonContact =
          ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT;
      probDistributionService.saveProbabilityDistribution(
          depthToHydrocarbonContact,
          form.getDepthToHydrocarbonContact(),
          submissionDetail
      );
    }

    if (form.getNetPorosity().getProbabilityDistributionType() != null) {
      var netPorosity = ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY;
      probDistributionService.saveProbabilityDistribution(
          netPorosity,
          form.getNetPorosity(),
          submissionDetail
      );
    }

    if (form.getNetToGross().getProbabilityDistributionType() != null) {
      var netToGross = ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY;
      probDistributionService.saveProbabilityDistribution(
          netToGross,
          form.getNetToGross(),
          submissionDetail
      );
    }

    if (form.getPercentOfGrvOilBearing().getProbabilityDistributionType() != null) {
      var percentOfGrvOilBearing =
          ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING;
      probDistributionService.saveProbabilityDistribution(
          percentOfGrvOilBearing,
          form.getPercentOfGrvOilBearing(),
          submissionDetail
      );
    }

    reservoirPropertiesRepository.save(reservoirProperty);
  }

  Optional<ReservoirProperty> getReservoirProperty(SubmissionDetail submissionDetail) {
    return reservoirPropertiesRepository.findBySubmissionDetail(submissionDetail);
  }
}
