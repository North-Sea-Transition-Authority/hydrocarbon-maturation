package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Service
public class SubmissionDetailAccessService {
  private final IndustryTeamService industryTeamService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final InternalTeamService internalTeamService;

  @Autowired
  public SubmissionDetailAccessService(IndustryTeamService industryTeamService,
                                       ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                       InternalTeamService internalTeamService) {
    this.industryTeamService = industryTeamService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.internalTeamService = internalTeamService;
  }

  public boolean canViewSubmissionDetail(SubmissionDetail submissionDetail, User user) {
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      return true;
    }

    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);

    if (Objects.isNull(resourceDetail.getOrganisationId())) {
      return false;
    }

    return industryTeamService.getOrganisationUnitIdsForUser(user).contains(resourceDetail.getOrganisationId());
  }
}
