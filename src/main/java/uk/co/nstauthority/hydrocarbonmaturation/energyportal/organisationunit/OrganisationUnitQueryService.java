package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.co.fivium.energyportalapi.client.LogCorrelationId;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.generated.client.OrganisationUnitProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.OrganisationUnitsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class OrganisationUnitQueryService {

  public static final OrganisationUnitsProjectionRoot ORGANISATION_UNITS_PROJECTION_ROOT
      = new OrganisationUnitsProjectionRoot()
      .organisationUnitId().name().registeredNumber().isDuplicate()
      .organisationGroups().organisationGroupId().root();
  public static final OrganisationUnitProjectionRoot ORGANISATION_UNIT_PROJECTION_ROOT
      = new OrganisationUnitProjectionRoot().organisationUnitId().name().registeredNumber();
  public static final OrganisationUnitProjectionRoot ORGANISATION_UNIT_GROUPS_PROJECTION_ROOT
      = new OrganisationUnitProjectionRoot().isDuplicate().organisationUnitId()
      .organisationGroups().organisationGroupId().root();

  private final OrganisationApi organisationApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;
  private final UserDetailService userDetailService;
  private final IndustryTeamService industryTeamService;
  private final RegulatorTeamService regulatorTeamService;
  private final OrganisationGroupQueryService organisationGroupQueryService;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public OrganisationUnitQueryService(OrganisationApi organisationApi,
                                      EnergyPortalApiWrapper energyPortalApiWrapper,
                                      UserDetailService userDetailService,
                                      IndustryTeamService industryTeamService,
                                      RegulatorTeamService regulatorTeamService,
                                      OrganisationGroupQueryService organisationGroupQueryService,
                                      SearchSelectorService searchSelectorService) {
    this.organisationApi = organisationApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
    this.userDetailService = userDetailService;
    this.industryTeamService = industryTeamService;
    this.regulatorTeamService = regulatorTeamService;
    this.organisationGroupQueryService = organisationGroupQueryService;
    this.searchSelectorService = searchSelectorService;
  }

  public List<OrganisationUnitDto> getOrganisationSearchableResults(String organisationName) {
    var user = userDetailService.getUserDetail();
    var isRegulatorUser = regulatorTeamService.isRegulatorUser(user);
    if (isRegulatorUser) {
      return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
          searchOrganisations(
              organisationName,
              requestPurpose,
              logCorrelationId)
              .stream()
              .filter(unit -> !BooleanUtils.isTrue(unit.getIsDuplicate()))
              .map(this::fromOrganisationUnit)
              .sorted(Comparator.comparing(OrganisationUnitDto::getOrganisationUnitName))
              .toList());
    } else {
      var userOrganisationGroupIds = industryTeamService.getOrganisationGroupIdsForUser(user);
      return getOrganisationUnitsByOrganisationGroupsAndName(userOrganisationGroupIds, organisationName);
    }
  }

  public Map<String, String> getPreSelectedOrganisationUnit(Integer organisationUnitId) {
    if (organisationUnitId == null) {
      return Map.of();
    }

    var key = getOrganisationUnitNameAndNumberByIdOrEmpty(organisationUnitId);

    return searchSelectorService.buildPrePopulatedSelection(String.valueOf(organisationUnitId), key);
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

  public String getOrganisationUnitNameAndNumberByIdOrEmpty(Integer organisationUnitId) {
    if (organisationUnitId == null) {
      return "";
    }
    return findOrganisationUnit(organisationUnitId)
        .map(organisationUnit -> {
          if (organisationUnit.getRegisteredNumber() != null) {
            return "%s (%s)"
                .formatted(organisationUnit.getName(), organisationUnit.getRegisteredNumber());
          } else {
            return organisationUnit.getName();
          }
        })
        .orElse("");
  }

  public List<Integer> findOrganisationGroupIdsByUnitId(Integer organisationUnitId) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        organisationApi.findOrganisationUnit(
            organisationUnitId,
            ORGANISATION_UNIT_GROUPS_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId)
            .map(this::organisationGroupIdsFromOrganisationUnit)
            .orElse(List.of()));
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

  private List<OrganisationUnitDto> getOrganisationUnitsByOrganisationGroupsAndName(
      List<Integer> organisationGroupIds, String organisationName) {
    if (organisationGroupIds != null && !organisationGroupIds.isEmpty()) {
      return getOrganisationUnitsByGroups(
          organisationGroupQueryService.getOrganisationGroupsByIds(organisationGroupIds))
          .stream()
          .filter(organisationUnit -> !BooleanUtils.isTrue(organisationUnit.getIsDuplicate())
              && organisationUnit.getName().toLowerCase().contains(organisationName.toLowerCase()))
          .map(this::fromOrganisationUnit)
          .sorted(Comparator.comparing(OrganisationUnitDto::getOrganisationUnitName))
          .toList();
    } else {
      return List.of();
    }
  }

  private List<OrganisationUnit> getOrganisationUnitsByGroups(List<OrganisationGroup> organisationGroups) {
    return organisationGroups
        .stream()
        .map(group -> {
          if (CollectionUtils.isEmpty(group.getOrganisationUnits())) {
            return new ArrayList<OrganisationUnit>();
          }

          return group.getOrganisationUnits();
        })
        .flatMap(List::stream)
        .toList();
  }

  private OrganisationUnitDto fromOrganisationUnit(OrganisationUnit organisationUnit) {
    var organisationUnitDto = new OrganisationUnitDto();
    organisationUnitDto.setOrganisationUnitId(organisationUnit.getOrganisationUnitId());
    organisationUnitDto.setOrganisationUnitName(organisationUnit.getName());
    organisationUnitDto.setCompanyHouseNumber(organisationUnit.getRegisteredNumber());
    return organisationUnitDto;
  }
}
