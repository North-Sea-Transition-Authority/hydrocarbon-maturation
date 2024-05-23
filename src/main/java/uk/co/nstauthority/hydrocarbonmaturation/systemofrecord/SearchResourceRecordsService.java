package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class SearchResourceRecordsService {
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final SearchSelectorService searchSelectorService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final LicenceQueryService licenceQueryService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public SearchResourceRecordsService(SubmissionDetailDisplayService submissionDetailDisplayService,
                               SearchSelectorService searchSelectorService,
                               SubmissionResourceService submissionResourceService,
                               OrganisationUnitQueryService organisationUnitQueryService,
                               LicenceQueryService licenceQueryService,
                               SubmissionDetailService submissionDetailService) {
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.searchSelectorService = searchSelectorService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.licenceQueryService = licenceQueryService;
    this.submissionDetailService = submissionDetailService;
  }

  List<ResourceRecordSearchResultItem> getResourceRecordSearchResults(SearchResourceRecordsForm form,
                                                                      ResourceRecordQueryServiceStrategy queryServiceStrategy) {
    var resourceRecordSearchResults = queryServiceStrategy.getResourceRecordSearchResults(form);

    var organisationUnitIds = getOrganisationUnitIdsFromResults(resourceRecordSearchResults);
    var organisationUnitMap = organisationUnitQueryService.getOrganisationUnitMapByIds(organisationUnitIds);

    var licences = getLicencesFromResults(resourceRecordSearchResults);
    var licenceMap = licenceQueryService.getLicenceMapByIds(licences);

    return resourceRecordSearchResults.stream()
        .map(result -> buildResourceRecordSearchResultItem(result, organisationUnitMap, licenceMap))
        .toList();
  }

  public List<String> getResourceRecordSearchResultsDistinctResourceNames(
      SearchResourceRecordsForm form,
      ResourceRecordQueryServiceStrategy queryServiceStrategy) {
    var resourceRecordSearchResults = queryServiceStrategy.getResourceRecordSearchResults(form);

    return resourceRecordSearchResults.stream()
        .map(ResourceRecordQueryResult::resourceName)
        .distinct()
        .toList();
  }

  private List<Integer> getOrganisationUnitIdsFromResults(List<ResourceRecordQueryResult> resourceRecordSearchResults) {
    return resourceRecordSearchResults.stream()
        .map(ResourceRecordQueryResult::organisationResponsibleId)
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

  boolean maxResultsReturned(List<ResourceRecordSearchResultItem> searchResultItems) {
    return searchResultItems.size() == ResourceQueryCommonService.MAX_RESULTS;
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
      Map<Integer, OrganisationUnitDto> organisationUnitMap,
      Map<Integer, LicenceView> licenceMap) {

    return new ResourceRecordSearchResultItem(
        getHeadingText(resourceRecordQueryResult),
        getCaptionText(resourceRecordQueryResult),
        getActionUrl(resourceRecordQueryResult),
        getResultItemModelProperties(resourceRecordQueryResult, organisationUnitMap, licenceMap));
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

    if (submissionDetailId != null) {
      captionText.append(submissionDetailService.getSubmissionDetail(submissionDetailId)
          .map(submissionDetail -> submissionDetail.isRelinquishment() ? " (Relinquishment)" : "")
          .orElse(""));
    }

    if (latestSubmissionInstant != null) {
      var latestSubmissionDate = DateUtil.formatLongDate(latestSubmissionInstant);
      captionText.append(": latest submission on %s".formatted(latestSubmissionDate));
    }

    return captionText.toString();
  }

  private String getActionUrl(ResourceRecordQueryResult resourceRecordQueryResult) {
    return ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(resourceRecordQueryResult.resourceId()));
  }

  private ResourceRecordSearchResultItemModelProperties getResultItemModelProperties(
      ResourceRecordQueryResult resourceRecordQueryResult,
      Map<Integer, OrganisationUnitDto> organisationUnitMap,
      Map<Integer, LicenceView> licenceMap) {

    var responsibleTeam = resourceRecordQueryResult.responsibleTeam();
    var organisationResponsibleId = resourceRecordQueryResult.organisationResponsibleId();
    var licencesTheResourceIsOn = resourceRecordQueryResult.licencesTheResourceIsOn();

    var organisationResponsibleDisplay =
        submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(organisationUnitMap, organisationResponsibleId);
    var licencesForDisplay = submissionDetailDisplayService.getLicencesForDisplayFromMap(licenceMap, licencesTheResourceIsOn);
    var teamResponsibleDisplay = submissionDetailDisplayService.getTeamResponsibleDisplay(responsibleTeam);

    var resourceType = resourceRecordQueryResult.resourceType();
    var modelProperties = new ResourceRecordSearchResultItemModelProperties()
        .addProperty("teamResponsible", teamResponsibleDisplay)
        .addProperty("organisationResponsible", organisationResponsibleDisplay);

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
