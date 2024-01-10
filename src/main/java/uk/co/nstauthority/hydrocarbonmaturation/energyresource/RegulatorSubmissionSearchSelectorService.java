package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
class RegulatorSubmissionSearchSelectorService implements SubmissionSearchSelectorStrategy {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final UserService userService;
  private final ResponsibleTeamService responsibleTeamService;

  RegulatorSubmissionSearchSelectorService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                           UserService userService,
                                           ResponsibleTeamService responsibleTeamService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.userService = userService;
    this.responsibleTeamService = responsibleTeamService;
  }

  @Override
  public Stream<ResourceDetail> getResourceDetailsForSubmissionsWithTeam(Stream<SubmissionDetail> submissionDetailStream,
                                                                         SubmissionType submissionType) {
    return submissionDetailStream
        .filter(this::isUserInResourceResponsibleTeam)
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow);
  }

  private boolean isUserInResourceResponsibleTeam(SubmissionDetail submissionDetail) {
    if (submissionDetail.isProspectSubmission()) {
      return true;
    }

    var user = userService.getUser();
    return responsibleTeamService.isInResponsibleTeam(user, submissionDetail);
  }
}
