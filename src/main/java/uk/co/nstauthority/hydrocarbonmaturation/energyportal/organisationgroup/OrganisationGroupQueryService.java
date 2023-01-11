package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.client.OrganisationGroupProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.OrganisationGroupsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;

@Service
public class OrganisationGroupQueryService {
  public static final OrganisationGroupsProjectionRoot ORGANISATION_GROUPS_PROJECTION_ROOT =
      new OrganisationGroupsProjectionRoot()
          .organisationGroupId()
          .name();
  public static final OrganisationGroupProjectionRoot ORGANISATION_GROUP_PROJECTION_ROOT = new OrganisationGroupProjectionRoot()
      .organisationGroupId()
      .name();
  private final OrganisationApi organisationApi;

  @Autowired
  public OrganisationGroupQueryService(OrganisationApi organisationApi) {
    this.organisationApi = organisationApi;
  }

  public List<OrganisationGroup> getOrganisationGroupsByName(String name, String purpose) {
    return organisationApi.searchOrganisationGroups(name, ORGANISATION_GROUPS_PROJECTION_ROOT, new RequestPurpose(purpose));
  }

  public RestSearchResult organisationGroupsToSearchResult(List<OrganisationGroup> queryResults) {
    return new RestSearchResult(queryResults.stream()
        .map(organisationGroup -> new RestSearchItem(
            organisationGroup.getOrganisationGroupId().toString(),
            organisationGroup.getName()))
        .toList());
  }

  public Optional<OrganisationGroup> getOrganisationGroupById(Integer id, String purpose) {
    return organisationApi.findOrganisationGroup(id, ORGANISATION_GROUP_PROJECTION_ROOT, new RequestPurpose(purpose));
  }

}
