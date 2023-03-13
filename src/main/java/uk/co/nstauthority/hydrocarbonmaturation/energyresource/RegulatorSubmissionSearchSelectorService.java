package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.Set;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Service
class RegulatorSubmissionSearchSelectorService implements SubmissionSearchSelectorStrategy {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final UserDetailService userDetailService;
  private final PermissionService permissionService;

  RegulatorSubmissionSearchSelectorService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                           UserDetailService userDetailService,
                                           PermissionService permissionService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.userDetailService = userDetailService;
    this.permissionService = permissionService;
  }

  @Override
  public Stream<ResourceDetail> getResourceDetailsForSubmissions(Stream<SubmissionDetail> submissionDetailStream,
                                                                 SubmissionType submissionType) {
    var user = userDetailService.getUserDetail();
    var isExplorationTeam = permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION));

    //Gets the list of resource views for prospects to be displayed in a search selector
    return submissionDetailStream
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        .filter(resourceDetail -> isExplorationTeam);
  }
}
