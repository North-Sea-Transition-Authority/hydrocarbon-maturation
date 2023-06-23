package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
public class CorrectionService {

  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final UserDetailService userDetailService;
  private final ResponsibleTeamService responsibleTeamService;
  private final SubmissionDetailRepository submissionDetailRepository;

  @Autowired
  public CorrectionService(RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                           UserDetailService userDetailService, ResponsibleTeamService responsibleTeamService,
                           SubmissionDetailRepository submissionDetailRepository) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.userDetailService = userDetailService;
    this.responsibleTeamService = responsibleTeamService;
    this.submissionDetailRepository = submissionDetailRepository;
  }


  /** Can start a correction on a resource if there are no existing draft corrections for it.
   * @param resource the resource we want to start a correction on
   * @return true if a correction can be started
   */
  public boolean canStartCorrection(Resource resource) {
    var regulatorManagedInformation = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResource(resource);

    if (regulatorManagedInformation.isEmpty()) {
      return false;
    }

    var responsibleTeam = regulatorManagedInformation.get().getResponsibleTeamType();
    if (Objects.isNull(responsibleTeam)) {
      return false;
    }

    var user = userDetailService.getUserDetail();
    if (!responsibleTeamService.isInResponsibleTeam(user, responsibleTeam)) {
      return false;
    }

    return !submissionDetailRepository.existsSubmissionDetailByCorrectionTrueAndStatusInAndSubmission_Resource(
        Set.of(SubmissionDetailStatus.DRAFT),
        resource
    );
  }
}
