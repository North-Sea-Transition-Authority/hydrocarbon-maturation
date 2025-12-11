package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.generated.types.Field;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator.FieldOperatorAndLicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

@Service
public class SearchResourceRecordsService {
  static final int MAX_RESULTS = 200;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final SearchSelectorService searchSelectorService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final LicenceQueryService licenceQueryService;
  private final FieldOperatorAndLicenceQueryService fieldOperatorAndLicenceQueryService;
  private final InternalTeamService internalTeamService;
  private final IndustryTeamService industryTeamService;

  @Autowired
  public SearchResourceRecordsService(SubmissionDetailDisplayService submissionDetailDisplayService,
                                      SearchSelectorService searchSelectorService,
                                      OrganisationUnitQueryService organisationUnitQueryService,
                                      LicenceQueryService licenceQueryService,
                                      FieldOperatorAndLicenceQueryService fieldOperatorAndLicenceQueryService,
                                      InternalTeamService internalTeamService,
                                      IndustryTeamService industryTeamService) {
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.searchSelectorService = searchSelectorService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.licenceQueryService = licenceQueryService;
    this.fieldOperatorAndLicenceQueryService = fieldOperatorAndLicenceQueryService;
    this.internalTeamService = internalTeamService;
    this.industryTeamService = industryTeamService;
  }

  List<ResourceRecordSearchResultItem> getResourceRecordSearchResults(SearchResourceRecordsForm form,
                                                                      ResourceRecordQueryServiceStrategy queryServiceStrategy,
                                                                      User user) {
    var resourceRecordSearchResults = queryServiceStrategy.getResourceRecordSearchResults(form);

    var prospectOrDiscoveryOrganisationUnitIds = getProspectOrDiscoveryOrganisationUnitIdsFromResults(
        resourceRecordSearchResults
    );
    var prospectOrDiscoveryOrganisationUnitMap = organisationUnitQueryService.getOrganisationUnitMapByIds(
        prospectOrDiscoveryOrganisationUnitIds
    );

    var fieldList = getFieldsFromResults(resourceRecordSearchResults);
    var fieldOperatorMap = getFieldOperatorOrganisationUnitDtos(fieldList);

    var resultLicenceIds = getLicencesFromResults(resourceRecordSearchResults);
    var resultLicenceMap = licenceQueryService.getLicenceMapByIds(resultLicenceIds);

    List<ResourceRecordQueryResult> userFilteredResults;
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      userFilteredResults = resourceRecordSearchResults;
    } else {
      userFilteredResults = filterResultsForUser(
          resourceRecordSearchResults,
          resultLicenceMap,
          fieldList,
          fieldOperatorMap,
          user
      );
    }

    return userFilteredResults.stream()
        .map(result -> buildResourceRecordSearchResultItem(
            result, resultLicenceMap, prospectOrDiscoveryOrganisationUnitMap, fieldOperatorMap)
        )
        .limit(MAX_RESULTS)
        .toList();
  }

  private boolean prospectOrDiscoveryOrganisationPredicate(ResourceRecordQueryResult result,
                                                           List<Integer> userOrganisationUnits) {
    return (result.resourceType().equals(ResourceType.PROSPECT)
        || result.resourceType().equals(ResourceType.DISCOVERY))
        && result.prospectOrDiscoveryOrganisationId() != null
        && userOrganisationUnits.contains(result.prospectOrDiscoveryOrganisationId());
  }

  private boolean prospectOrDiscoveryLicencePredicate(ResourceRecordQueryResult result,
                                                      List<UUID> filteredLicenceResults) {
    return (result.resourceType().equals(ResourceType.PROSPECT)
        || result.resourceType().equals(ResourceType.DISCOVERY))
        && filteredLicenceResults.contains(result.resourceId());
  }

  private boolean fieldOrganisationPredicate(ResourceRecordQueryResult result,
                                             List<Integer> userOrganisationUnits,
                                             Map<Integer, OrganisationUnitDto> fieldOperatorMap) {
    return result.resourceType().equals(ResourceType.FIELD)
        && fieldOperatorMap.containsKey(result.fieldIdentifier())
        && userOrganisationUnits.contains(fieldOperatorMap.get(result.fieldIdentifier()).organisationUnitId());
  }

  private boolean fieldLicencePredicate(ResourceRecordQueryResult result,
                                        List<UUID> filteredLicenceResults,
                                        List<UUID> filteredPearsLicenceResults) {
    return result.resourceType().equals(ResourceType.FIELD)
        && (filteredPearsLicenceResults.contains(result.resourceId())
        || filteredLicenceResults.contains(result.resourceId()));
  }

  public List<String> getResourceRecordSearchResultsDistinctResourceNames(
      SearchResourceRecordsForm form,
      ResourceRecordQueryServiceStrategy queryServiceStrategy,
      User user
  ) {
    var isRegulatorOrAreaTeamUser = internalTeamService.isRegulatorOrAreaTeamUser(user);
    if (isRegulatorOrAreaTeamUser) {
      form.setShowArchivedResources(true); // Regulator and area team users should be able to see all resources
    }
    var resourceRecordSearchResults = queryServiceStrategy.getResourceRecordSearchResults(form);
    List<ResourceRecordQueryResult> userFilteredResults;
    if (isRegulatorOrAreaTeamUser) {
      userFilteredResults = resourceRecordSearchResults;
    } else {

      var fieldList = getFieldsFromResults(resourceRecordSearchResults);
      var fieldOperatorMap = getFieldOperatorOrganisationUnitDtos(fieldList);

      var resultLicenceIds = getLicencesFromResults(resourceRecordSearchResults);
      var resultLicenceMap = licenceQueryService.getLicenceMapByIds(resultLicenceIds);

      userFilteredResults = filterResultsForUser(
          resourceRecordSearchResults,
          resultLicenceMap,
          fieldList,
          fieldOperatorMap,
          user
      );
    }

    return userFilteredResults.stream()
        .map(ResourceRecordQueryResult::resourceName)
        .distinct()
        .toList();

  }

  private List<ResourceRecordQueryResult> filterResultsForUser(
      List<ResourceRecordQueryResult> resourceRecordSearchResults,
      Map<Integer, LicenceDto> resultLicenceMap,
      List<Field> fieldList,
      Map<Integer, OrganisationUnitDto> fieldOperatorMap,
      User user
  ) {
    // prospect and discovery licences, as well as pre-pears field licences
    var extantResultsLicenceIds = resultLicenceMap.entrySet().stream()
        .filter(licence -> licence.getValue().licenceStatus().equals(LicenceStatus.EXTANT))
        .map(Map.Entry::getKey)
        .toList();

    var resultsWithExtantLicences = resourceRecordSearchResults.stream()
        .filter(result -> result.licencesTheResourceIsOn() != null)
        .filter(result -> result.licencesTheResourceIsOn().stream().anyMatch(extantResultsLicenceIds::contains))
        .map(ResourceRecordQueryResult::resourceId)
        .toList();

    var pearsLicenceMap = getFieldLicenceDtos(fieldList);

    // PEARS licences for fields
    var extantPearsLicenceIds = pearsLicenceMap.values()
        .stream()
        .flatMap(List::stream)
        .filter(licence -> licence.licenceStatus().equals(LicenceStatus.EXTANT))
        .map(LicenceDto::licenceId)
        .toList();

    var fieldsWithExtantPearsLicences = pearsLicenceMap.entrySet().stream()
        .filter(fieldLicence -> fieldLicence.getValue().stream()
            .map(LicenceDto::licenceId)
            .anyMatch(extantPearsLicenceIds::contains)
        )
        .map(Map.Entry::getKey)
        .toList();

    var fieldLicenceFilteredResults = resourceRecordSearchResults.stream()
        .filter(result -> fieldsWithExtantPearsLicences.contains(result.fieldIdentifier()))
        .map(ResourceRecordQueryResult::resourceId)
        .toList();

    var userOrgUnitIds = industryTeamService.getOrganisationUnitIdsForUserWithRoles(
        user, Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER)
    );

    // get results for user where they are in the relevant organisation(s) and the results have extant licences
    return resourceRecordSearchResults
        .stream()
        .filter(result -> prospectOrDiscoveryOrganisationPredicate(result, userOrgUnitIds)
            || fieldOrganisationPredicate(result, userOrgUnitIds, fieldOperatorMap))
        .filter(result ->
            prospectOrDiscoveryLicencePredicate(result, resultsWithExtantLicences)
                || fieldLicencePredicate(result, resultsWithExtantLicences, fieldLicenceFilteredResults)
        )
        .toList();
  }

  private List<Integer> getProspectOrDiscoveryOrganisationUnitIdsFromResults(
      List<ResourceRecordQueryResult> resourceRecordSearchResults
  ) {
    return resourceRecordSearchResults.stream()
        .map(ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId)
        .filter(Objects::nonNull)
        .distinct()
        .toList();
  }

  private List<Integer> getLicencesFromResults(List<ResourceRecordQueryResult> resourceRecordSearchResults) {
    return resourceRecordSearchResults.stream()
        .map(ResourceRecordQueryResult::licencesTheResourceIsOn)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .distinct()
        .toList();
  }

  private List<Field> getFieldsFromResults(List<ResourceRecordQueryResult> resourceRecordSearchResults) {
    var fieldIds = resourceRecordSearchResults.stream()
        .map(ResourceRecordQueryResult::fieldIdentifier)
        .filter(Objects::nonNull)
        .toList();
    if (fieldIds.isEmpty()) {
      return Collections.emptyList();
    }
    return fieldOperatorAndLicenceQueryService.getFieldsByIds(fieldIds);
  }

  private Map<Integer, OrganisationUnitDto> getFieldOperatorOrganisationUnitDtos(
      List<Field> fields
  ) {
    if (!fields.isEmpty()) {
      return fields.stream()
          .filter(field -> field.getFieldOperator() != null)
          .collect(
              StreamUtil.toLinkedHashMap(
                  Field::getFieldId,
                  field -> OrganisationUnitDto.fromOrganisationUnit(field.getFieldOperator())
              )
          );
    }

    return Map.of();
  }

  private Map<Integer, List<LicenceDto>> getFieldLicenceDtos(List<Field> fields) {
    if (!fields.isEmpty()) {
      return fields.stream()
          .collect(
              Collectors.toMap(
                  Field::getFieldId,
                  field -> field.getLicences().stream().map(LicenceDto::fromLicence).toList())
          );
    }

    return Map.of();
  }

  boolean maxResultsReturned(List<ResourceRecordSearchResultItem> searchResultItems) {
    return searchResultItems.size() == MAX_RESULTS;
  }

  Map<String, String> getPreselectedLicence(SearchResourceRecordsForm form) {
    var licenceId = form.getLicenceId();
    if (ObjectUtils.isEmpty(licenceId)) {
      return Map.of();
    }
    return licenceQueryService.getLicenceById(Integer.valueOf(licenceId))
        .map(licence ->
            searchSelectorService.buildPrePopulatedSelection(
                licenceId,
                licence.licenceRef()))
        .orElse(Map.of());
  }

  public Map<String, String> getPreSelectedResourceName(SearchResourceRecordsForm form) {
    var resourceName = form.getResourceNameFromResourceDetail();

    if (resourceName == null) {
      return Map.of();
    }

    return searchSelectorService.buildPrePopulatedSelection(resourceName);
  }

  public Map<String, String> getPreSelectedRegulatorResourceName(SearchResourceRecordsForm form) {
    var regulatorResourceName = form.getRegulatorResourceName();

    if (regulatorResourceName == null) {
      return Map.of();
    }

    return searchSelectorService.buildPrePopulatedSelection(regulatorResourceName);
  }

  public Map<String, String> getPreSelectedFieldName(SearchResourceRecordsForm form) {
    var fieldNameForResourceSubmission = form.getFieldNameForResourceSubmission();

    if (fieldNameForResourceSubmission == null) {
      return Map.of();
    }

    return searchSelectorService.buildPrePopulatedSelection(fieldNameForResourceSubmission);
  }

  public Map<String, String> getPreSelectedParentFieldName(SearchResourceRecordsForm form) {
    var parentFieldName = form.getParentFieldName();

    if (parentFieldName == null) {
      return Map.of();
    }

    return searchSelectorService.buildPrePopulatedSelection(parentFieldName);
  }

  public Map<String, String> getPreSelectedOrganisationUnit(SearchResourceRecordsForm form) {
    if (form.getResponsibleOrganisationId() == null) {
      return Map.of();
    }

    return organisationUnitQueryService.getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId());
  }

  public Map<String, String> getPreSelectedQuadAndBlock(SearchResourceRecordsForm form) {
    var centreOfInterestQuadBlock = form.getCentreOfInterestBlock();

    if (centreOfInterestQuadBlock == null) {
      return Map.of();
    }

    return searchSelectorService.buildPrePopulatedSelection(
        centreOfInterestQuadBlock,
        centreOfInterestQuadBlock
    );
  }

  private ResourceRecordSearchResultItem buildResourceRecordSearchResultItem(
      ResourceRecordQueryResult resourceRecordQueryResult,
      Map<Integer, LicenceDto> licenceMap,
      Map<Integer, OrganisationUnitDto> prePearsOrganisationUnitMap,
      Map<Integer, OrganisationUnitDto> fieldOperatorMap) {

    return new ResourceRecordSearchResultItem(
        getHeadingText(resourceRecordQueryResult),
        getCaptionText(resourceRecordQueryResult),
        getActionUrl(resourceRecordQueryResult),
        getResultItemModelProperties(
            resourceRecordQueryResult,
            licenceMap,
            prePearsOrganisationUnitMap,
            fieldOperatorMap
        ));
  }

  private String getHeadingText(ResourceRecordQueryResult resourceRecordQueryResult) {
    return SubmissionDetailDisplayUtil.getDisplayName(
        resourceRecordQueryResult.resourceName(),
        resourceRecordQueryResult.centreOfInterestQuadBlock(),
        resourceRecordQueryResult.licenceApplicationReference()
    );
  }

  private String getCaptionText(ResourceRecordQueryResult resourceRecordQueryResult) {
    var resourceType = resourceRecordQueryResult.resourceType().getDisplayName();
    var submissionDetailId = resourceRecordQueryResult.submissionDetailId();
    var latestSubmissionInstant = resourceRecordQueryResult.submissionAcceptedTimestamp();

    var captionText = new StringBuilder();
    captionText.append(resourceType);

    var isRelinquishment = SubmissionType.RELINQUISHMENT_PROSPECT.equals(resourceRecordQueryResult.submissionType())
        || SubmissionType.RELINQUISHMENT_DISCOVERY.equals(resourceRecordQueryResult.submissionType());

    if (submissionDetailId != null) {
      captionText.append(isRelinquishment ? " (Relinquishment)" : "");
    }

    if (latestSubmissionInstant != null) {
      var latestSubmissionDate = DateUtil.formatLongDate(latestSubmissionInstant);
      captionText.append(": latest submission on %s".formatted(latestSubmissionDate));
    }

    return captionText.toString();
  }

  private String getActionUrl(ResourceRecordQueryResult resourceRecordQueryResult) {
    return ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(resourceRecordQueryResult.resourceId(), null));
  }

  private ResourceRecordSearchResultItemModelProperties getResultItemModelProperties(
      ResourceRecordQueryResult resourceRecordQueryResult,
      Map<Integer, LicenceDto> licenceMap,
      Map<Integer, OrganisationUnitDto> prePearsOrganisationUnitMap,
      Map<Integer, OrganisationUnitDto> fieldOperatorMap) {

    var responsibleTeam = resourceRecordQueryResult.responsibleTeam();
    var organisationResponsibleId = resourceRecordQueryResult.prospectOrDiscoveryOrganisationId();
    var licencesTheResourceIsOn = resourceRecordQueryResult.licencesTheResourceIsOn();
    var fieldId = resourceRecordQueryResult.fieldIdentifier();

    String organisationResponsibleDisplay;
    if (fieldId != null && fieldOperatorMap.containsKey(fieldId)) {
      organisationResponsibleDisplay =
          submissionDetailDisplayService.getOrganisationResponsibleDisplay(fieldOperatorMap.get(fieldId));
    } else {
      organisationResponsibleDisplay =
        submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(
            prePearsOrganisationUnitMap,
            organisationResponsibleId
        );
    }

    var licencesForDisplay = submissionDetailDisplayService.getLicencesForDisplayFromMap(licenceMap, licencesTheResourceIsOn);
    var teamResponsibleDisplay = submissionDetailDisplayService.getTeamResponsibleDisplay(responsibleTeam);

    var resourceType = resourceRecordQueryResult.resourceType();
    var modelProperties = new ResourceRecordSearchResultItemModelProperties()
        .addProperty("teamResponsible", teamResponsibleDisplay)
        .addProperty("organisationResponsible", organisationResponsibleDisplay)
        .addProperty("isArchived", !resourceRecordQueryResult.active());

    if (ResourceType.DISCOVERY.equals(resourceType)
        || ResourceType.PROSPECT.equals(resourceType)) {
      modelProperties.addProperty("licences", licencesForDisplay);
    } else if (ResourceType.FIELD.equals(resourceType)) {
      var fieldStatus = resourceRecordQueryResult.fieldStatus() != null
          ? resourceRecordQueryResult.fieldStatus().getDisplayName()
          : "";
      modelProperties
          .addProperty("fieldStatus", fieldStatus)
          .addProperty("parentField", resourceRecordQueryResult.parentFieldName());
    }

    return modelProperties;
  }

  public Map<String, String> getOpportunityTypes() {
    var opportunityTypesMap =
        (LinkedHashMap<String, String>) DisplayableEnumOptionUtil.getDisplayableOptions(OpportunityType.class);
    opportunityTypesMap.put("NOT_SET", "Not set");
    return opportunityTypesMap;
  }

  public Map<String, String> getResourceTypes() {
    var resourceTypesMap =
        (LinkedHashMap<String, String>) DisplayableEnumOptionUtil.getDisplayableOptions(ResourceType.class);
    resourceTypesMap.remove(ResourceType.FIELD.name());
    return resourceTypesMap;
  }
}
