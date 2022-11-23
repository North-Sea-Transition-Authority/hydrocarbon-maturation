package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class ReservoirPropertiesDuplicationService implements SubmissionSectionDuplicationService {
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  ReservoirPropertiesDuplicationService(ReservoirPropertiesPersistenceService chanceOfSuccessPersistenceService,
                                        ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                        SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.reservoirPropertiesPersistenceService = chanceOfSuccessPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    reservoirPropertiesPersistenceService.getReservoirProperty(oldSubmissionDetail)
        .ifPresent(reservoirProperty ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, reservoirProperty,
                ReservoirProperty.class)
        );

    var applicableInstances = new HashSet<>(Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT
    ));

    if (!Objects.equals(newSubmissionDetail.getSubmissionType(), SubmissionType.PROSPECT_UPDATE)) {
      applicableInstances.remove(ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT);
    }

    probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, applicableInstances)
        .forEach(probabilityDistribution ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution,
                ProbabilityDistribution.class));
  }
}
