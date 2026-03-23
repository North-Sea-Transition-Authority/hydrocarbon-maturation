package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportal.starter.configuration.WellKnownOrganisationGroupsConfigurationProperties;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.client.OrganisationGroupProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.OrganisationGroupsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;

@Service
public class OrganisationGroupQueryService {
  public static final OrganisationGroupsProjectionRoot ORGANISATION_GROUPS_PROJECTION_ROOT =
      new OrganisationGroupsProjectionRoot()
          .organisationGroupId()
          .name();
  public static final OrganisationGroupProjectionRoot ORGANISATION_GROUP_PROJECTION_ROOT = new OrganisationGroupProjectionRoot()
      .organisationGroupId()
      .name()
      .emailDomains()
        .domain()
      .root();
  public static final OrganisationGroupsProjectionRoot ORGANISATION_GROUPS_UNITS_PROJECTION_ROOT =
      new OrganisationGroupsProjectionRoot()
          .organisationGroupId()
          .name()
          .organisationUnits()
          .organisationUnitId()
          .name()
          .registeredNumber()
          .isDuplicate()
          .root();
  private final OrganisationApi organisationApi;
  private final WellKnownOrganisationGroupsConfigurationProperties wellKnownOrganisationGroups;

  @Autowired
  public OrganisationGroupQueryService(OrganisationApi organisationApi,
                                       WellKnownOrganisationGroupsConfigurationProperties wellKnownOrganisationGroups
                                       ) {

    this.organisationApi = organisationApi;
    this.wellKnownOrganisationGroups = wellKnownOrganisationGroups;
  }

  public List<OrganisationGroupDto> getOrganisationGroupsByName(String name) {
    return organisationApi.searchOrganisationGroups(
        name,
            ORGANISATION_GROUPS_PROJECTION_ROOT,
            new RequestPurpose("Search organisation groups by name"),
            CorrelationIdUtil.getLogCorrelationId()
        )
        .stream()
        .map(this::fromOrganisationGroup)
        .toList();
  }

  public List<OrganisationGroup> getOrganisationGroupsByIds(List<Integer> organisationGroups) {
    return organisationApi.getAllOrganisationGroupsByIds(
            organisationGroups,
            ORGANISATION_GROUPS_UNITS_PROJECTION_ROOT,
            new RequestPurpose("Get organisation groups by IDs"),
            CorrelationIdUtil.getLogCorrelationId()
        )
        .stream()
        .toList();
  }

  private OrganisationGroupDto fromOrganisationGroup(OrganisationGroup organisationGroup) {
    var dto = new OrganisationGroupDto();
    dto.setOrganisationGroupId(organisationGroup.getOrganisationGroupId());
    dto.setOrganisationGroupName(organisationGroup.getName());
    return dto;
  }

  public Optional<OrganisationGroupDto> getOrganisationGroupById(Integer id) {
    return organisationApi.findOrganisationGroup(
            id,
            ORGANISATION_GROUP_PROJECTION_ROOT,
            new RequestPurpose("getOrganisationGroupById")
        )
        .map(OrganisationGroupDto::from);
  }

  public Optional<OrganisationGroupDto> getRegulatorOrganisationGroup() {
    return getOrganisationGroupById(wellKnownOrganisationGroups.nsta().idAsInteger());
  }
}
