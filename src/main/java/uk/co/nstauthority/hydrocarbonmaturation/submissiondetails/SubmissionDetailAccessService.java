package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;

@Service
public class SubmissionDetailAccessService {
  private final IndustryTeamMemberService industryTeamMemberService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final InternalTeamService internalTeamService;

  @Autowired
  public SubmissionDetailAccessService(IndustryTeamMemberService industryTeamMemberService,
                                       ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                       InternalTeamService internalTeamService) {
    this.industryTeamMemberService = industryTeamMemberService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.internalTeamService = internalTeamService;
  }

  public boolean canViewSubmissionDetail(SubmissionDetail submissionDetail, User user) {
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      return true;
    }

    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);

    return industryTeamMemberService.isMemberOfOrganisationUnit(user, resourceDetail.getOrganisationId());
  }
}
