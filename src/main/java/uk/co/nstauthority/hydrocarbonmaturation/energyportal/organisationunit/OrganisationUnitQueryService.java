package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class OrganisationUnitQueryService {

  public static final OrganisationUnitsProjectionRoot ORGANISATION_UNITS_PROJECTION_ROOT
      = new OrganisationUnitsProjectionRoot()
      .organisationUnitId().name().registeredNumber().isDuplicate()
      .organisationGroups().organisationGroupId().root()
      .organisationNameHistory().name().startDate().endDate().root();
  public static final OrganisationUnitProjectionRoot ORGANISATION_UNIT_PROJECTION_ROOT
      = new OrganisationUnitProjectionRoot()
      .organisationUnitId().name().registeredNumber()
      .organisationNameHistory().name().startDate().endDate().root();
  public static final OrganisationUnitProjectionRoot ORGANISATION_UNIT_GROUPS_PROJECTION_ROOT
      = new OrganisationUnitProjectionRoot().isDuplicate().organisationUnitId()
      .organisationGroups().organisationGroupId().root();

  private final OrganisationApi organisationApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;
  private final InternalTeamService internalTeamService;
  private final OrganisationGroupQueryService organisationGroupQueryService;
  private final SearchSelectorService searchSelectorService;
  private final IndustryTeamService industryTeamService;

  @Autowired
  public OrganisationUnitQueryService(OrganisationApi organisationApi,
                                      EnergyPortalApiWrapper energyPortalApiWrapper,
                                      OrganisationGroupQueryService organisationGroupQueryService,
                                      SearchSelectorService searchSelectorService,
                                      InternalTeamService internalTeamService,
                                      IndustryTeamService industryTeamService) {
    this.organisationApi = organisationApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
    this.internalTeamService = internalTeamService;
    this.organisationGroupQueryService = organisationGroupQueryService;
    this.searchSelectorService = searchSelectorService;
    this.industryTeamService = industryTeamService;
  }

  public List<OrganisationUnitDto> getOrganisationSearchableResults(String organisationName,
                                                                    User user) {
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      return searchOrganisationUnitsWithName(organisationName);
    } else {
      var userOrganisationGroupIds = industryTeamService.getOrganisationGroupIdsForUser(user);
      return getOrganisationUnitsByOrganisationGroupsAndName(userOrganisationGroupIds, organisationName);
    }
  }

  public List<OrganisationUnitWithDateDto> getOrganisationSearchableResults(String organisationName,
                                                                            User user,
                                                                            SubmissionDetail submissionDetail) {
    List<OrganisationUnitDto> organisationUnits;
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      organisationUnits = searchOrganisationUnitsWithName(organisationName);
    } else {
      var userOrganisationGroupIds = industryTeamService.getOrganisationGroupIdsForUser(user);
      organisationUnits = getOrganisationUnitsByOrganisationGroupsAndName(userOrganisationGroupIds, organisationName);
    }
    var organisationNameDate = submissionDetail.getAcceptedTimestamp() != null
        ? DateUtil.createLocalDate(submissionDetail.getAcceptedTimestamp())
        : LocalDate.now();
    return organisationUnits.stream()
        .map(ou -> OrganisationUnitWithDateDto.fromOrganisationUnitAndDate(ou, organisationNameDate))
        .toList();
  }

  public Map<String, String> getPreSelectedOrganisationUnit(Integer organisationUnitId) {
    if (organisationUnitId == null) {
      return Map.of();
    }

    var key = getOrganisationUnitNameAndNumberByIdOrEmpty(organisationUnitId);

    return searchSelectorService.buildPrePopulatedSelection(String.valueOf(organisationUnitId), key);
  }

  public Map<String, String> getPreSelectedOrganisationUnitForDate(ResourceDetail resourceDetail, LocalDate acceptedDate) {
    var organisationUnitId = resourceDetail.getOrganisationId();
    var organisationRegisteredNumber = resourceDetail.getOrganisationRegisteredNumber();

    if (!Objects.isNull(organisationUnitId)) {

      return findOrganisationUnit(organisationUnitId).map(organisationUnit -> {
        var organisationNameHistory = OrganisationUnitDateUtil.getOrganisationNameHistoryForDate(organisationUnit, acceptedDate);
        var orgDisplayName = "";
        if (organisationNameHistory.isPresent()) {
          var nameFromHistory = organisationNameHistory.get().name();
          orgDisplayName = getOrganisationDisplayName(nameFromHistory, organisationRegisteredNumber);
        }
        return searchSelectorService.buildPrePopulatedSelection(String.valueOf(organisationUnitId), orgDisplayName);
      }).orElse(Map.of());

    }

    return Map.of();
  }

  private String getOrganisationDisplayName(String organisationName, String organisationRegisteredNumber) {
    if (organisationRegisteredNumber != null) {
      return "%s (%s)".formatted(
          organisationName,
          organisationRegisteredNumber
      );
    }
    return organisationName;
  }

  public Optional<String> getOrganisationUnitNameForDate(OrganisationUnitDto organisationUnit, LocalDate acceptedDate) {
    var organisationNameHistory = OrganisationUnitDateUtil.getOrganisationNameHistoryForDate(organisationUnit, acceptedDate);
    return organisationNameHistory.map(OrganisationNameHistoryDto::name);
  }

  public Optional<OrganisationUnitDto> findOrganisationUnit(Integer organisationUnitId) {

    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        organisationApi.findOrganisationUnit(
            organisationUnitId,
            ORGANISATION_UNIT_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId)
    )
    .map(OrganisationUnitDto::fromOrganisationUnit);
  }

  public List<OrganisationUnitDto> getOrganisationUnitsByIds(List<Integer> organisationUnitIds) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        organisationApi.getOrganisationUnitsByIds(
            organisationUnitIds,
            ORGANISATION_UNITS_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId
        )).stream()
        .map(OrganisationUnitDto::fromOrganisationUnit)
        .toList();
  }

  public Map<Integer, OrganisationUnitDto> getOrganisationUnitMapByIds(List<Integer> organisationUnitIds) {
    return getOrganisationUnitsByIds(organisationUnitIds).stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(OrganisationUnitDto::organisationUnitId, Function.identity()));
  }

  public String getOrganisationUnitNameAndNumberByIdOrEmpty(@Nullable Integer organisationUnitId) {
    return Optional.ofNullable(organisationUnitId)
        .flatMap(this::findOrganisationUnit)
        .map(OrganisationUnitDto::getDisplayText)
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

  private List<OrganisationUnitDto> searchOrganisationUnitsWithName(String organisationName) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        searchOrganisations(
            organisationName,
            requestPurpose,
            logCorrelationId)
            .stream()
            .filter(unit -> !BooleanUtils.isTrue(unit.getIsDuplicate()))
            .map(OrganisationUnitDto::fromOrganisationUnit)
            .sorted(Comparator.comparing(OrganisationUnitDto::organisationUnitName))
            .toList());
  }

  private List<Integer> organisationGroupIdsFromOrganisationUnit(OrganisationUnit organisationUnit) {
    if (organisationUnit.getOrganisationGroups() == null) {
      return List.of();
    }

    return organisationUnit.getOrganisationGroups()
        .stream()
        .map(OrganisationGroup::getOrganisationGroupId)
        .filter(Objects::nonNull)
        .toList();
  }

  private List<OrganisationUnitDto> getOrganisationUnitsByOrganisationGroupsAndName(
      List<Integer> organisationGroupIds, String organisationName) {
    if (organisationGroupIds == null || organisationGroupIds.isEmpty()) {
      return List.of();
    }

    return getOrganisationUnitsByGroups(organisationGroupQueryService.getOrganisationGroupsByIds(organisationGroupIds))
        .stream()
        .filter(organisationUnit -> !BooleanUtils.isTrue(organisationUnit.getIsDuplicate())
            && organisationUnit.getName().toLowerCase().contains(organisationName.toLowerCase()))
        .map(OrganisationUnitDto::fromOrganisationUnit)
        .sorted(Comparator.comparing(OrganisationUnitDto::organisationUnitName))
        .toList();
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


}
