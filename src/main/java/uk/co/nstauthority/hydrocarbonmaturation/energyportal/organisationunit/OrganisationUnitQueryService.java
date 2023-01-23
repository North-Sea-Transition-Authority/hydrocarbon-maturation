package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.LogCorrelationId;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.client.OrganisationUnitProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.OrganisationUnitsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@Service
public class OrganisationUnitQueryService {

  public static final OrganisationUnitsProjectionRoot ORGANISATION_UNITS_PROJECTION_ROOT
      = new OrganisationUnitsProjectionRoot().organisationUnitId().name().registeredNumber();
  public static final OrganisationUnitProjectionRoot ORGANISATION_UNIT_PROJECTION_ROOT
      = new OrganisationUnitProjectionRoot().organisationUnitId().name().registeredNumber();
  public static final OrganisationUnitProjectionRoot ORGANISATION_UNIT_GROUPS_PROJECTION_ROOT
      = new OrganisationUnitProjectionRoot().organisationUnitId().organisationGroups().organisationGroupId().root();

  private final OrganisationApi organisationApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;

  @Autowired
  public OrganisationUnitQueryService(OrganisationApi organisationApi,
                                      EnergyPortalApiWrapper energyPortalApiWrapper) {
    this.organisationApi = organisationApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
  }

  public List<OrganisationUnitDto> getOrganisationSearchableResults(String organisationName) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        searchOrganisations(
            organisationName,
            requestPurpose,
            logCorrelationId)
            .stream()
            .map(this::fromOrganisationUnit)
            .toList());
  }

  public Optional<OrganisationUnit> findOrganisationUnit(Integer organisationUnitId) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        organisationApi.findOrganisationUnit(
            organisationUnitId,
            ORGANISATION_UNIT_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId)
    );
  }

  public List<Integer> findOrganisationGroupIdsByUnitId(Integer organisationUnitId) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        organisationApi.findOrganisationUnit(
            organisationUnitId,
            ORGANISATION_UNIT_GROUPS_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId)
            .map(this::organisationGroupIdsFromOrganisationUnit).orElse(List.of()));
  }

  List<OrganisationUnit> searchOrganisations(
      String organisationName,
      RequestPurpose requestPurpose,
      LogCorrelationId logCorrelationId) {
    return organisationApi.searchOrganisationUnits(
        organisationName,
        ORGANISATION_UNITS_PROJECTION_ROOT,
        requestPurpose,
        logCorrelationId);
  }

  private List<Integer> organisationGroupIdsFromOrganisationUnit(OrganisationUnit organisationUnit) {
    if (organisationUnit.getOrganisationGroups() != null) {
      return organisationUnit.getOrganisationGroups()
          .stream()
          .map(OrganisationGroup::getOrganisationGroupId)
          .filter(Objects::nonNull)
          .toList();
    } else {
      return List.of();
    }
  }

  private OrganisationUnitDto fromOrganisationUnit(OrganisationUnit organisationUnit) {
    var organisationUnitDto = new OrganisationUnitDto();
    organisationUnitDto.setOrganisationUnitId(organisationUnit.getOrganisationUnitId());
    organisationUnitDto.setOrganisationUnitName(organisationUnit.getName());
    organisationUnitDto.setCompanyHouseNumber(organisationUnit.getRegisteredNumber());
    return organisationUnitDto;
  }
}
