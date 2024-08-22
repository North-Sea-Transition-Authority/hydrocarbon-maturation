package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
class RegulatorSubmissionSearchSelectorService implements SubmissionSearchSelectorStrategy {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final ResponsibleTeamService responsibleTeamService;

  @Autowired
  RegulatorSubmissionSearchSelectorService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                           ResponsibleTeamService responsibleTeamService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.responsibleTeamService = responsibleTeamService;
  }

  @Override
  public Stream<ResourceDetail> getResourceDetailsForSubmissionsWithTeam(Stream<SubmissionDetail> submissionDetailStream,
                                                                         SubmissionType submissionType,
                                                                         User user) {
    return submissionDetailStream
        .filter(submissionDetail -> isUserInResourceResponsibleTeam(submissionDetail, user))
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow);
  }

  private boolean isUserInResourceResponsibleTeam(SubmissionDetail submissionDetail, User user) {
    if (submissionDetail.isProspectSubmission()) {
      return true;
    }

    return responsibleTeamService.isInResponsibleTeam(user, submissionDetail);
  }
}
