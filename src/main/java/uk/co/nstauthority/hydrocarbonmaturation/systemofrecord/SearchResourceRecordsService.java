package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

@Service
class SearchResourceRecordsService {
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final SearchSelectorService searchSelectorService;
  private final SubmissionResourceService submissionResourceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final LicenceQueryService licenceQueryService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  SearchResourceRecordsService(SubmissionDetailDisplayService submissionDetailDisplayService,
                               SearchSelectorService searchSelectorService,
                               SubmissionResourceService submissionResourceService,
                               OrganisationUnitQueryService organisationUnitQueryService,
                               LicenceQueryService licenceQueryService, SubmissionDetailService submissionDetailService) {
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.searchSelectorService = searchSelectorService;
    this.submissionResourceService = submissionResourceService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.licenceQueryService = licenceQueryService;
    this.submissionDetailService = submissionDetailService;
  }

  List<ResourceRecordSearchResultItem> getResourceRecordSearchResults(SearchResourceRecordsForm form,
                                                                      ResourceRecordQueryServiceStrategy queryServiceStrategy) {
    return queryServiceStrategy.getResourceRecordSearchResults(form).stream()
        .map(this::buildResourceRecordSearchResultItem)
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
    return licenceQueryService.getLicenceById(licenceId)
        .map(licence ->
            searchSelectorService.buildPrePopulatedSelection(
                licenceId,
                licence.licenceRef()))
        .orElse(Map.of());
  }

  public Map<String, String> getPreSelectedResourceName(SearchResourceRecordsForm form) {
    var resourceDetailIdForResourceSubmission = form.getResourceDetailIdForResourceSubmission();

    if (resourceDetailIdForResourceSubmission == null) {
      return Map.of();
    }

    return searchSelectorService.buildPrePopulatedSelection(
        resourceDetailIdForResourceSubmission,
        submissionResourceService.getResourceDisplayName(UUID.fromString(resourceDetailIdForResourceSubmission))
    );
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
      ResourceRecordQueryResult resourceRecordQueryResult) {

    return new ResourceRecordSearchResultItem(
        getHeadingText(resourceRecordQueryResult),
        getCaptionText(resourceRecordQueryResult),
        getActionUrl(resourceRecordQueryResult),
        getResultItemModelProperties(resourceRecordQueryResult));
  }

  private String getHeadingText(ResourceRecordQueryResult resourceRecordQueryResult) {
    return submissionDetailDisplayService.getResourceNameAndCentreOfInterestBlockDisplay(
        resourceRecordQueryResult.getResourceName(),
        resourceRecordQueryResult.getCentreOfInterestQuadBlock()
    );
  }

  private String getCaptionText(ResourceRecordQueryResult resourceRecordQueryResult) {
    var submissionDetailId = resourceRecordQueryResult.getSubmissionDetailId();
    return submissionDetailService.getSubmissionDetail(submissionDetailId)
        .map(submissionDetail -> resourceRecordQueryResult.getResourceType().getDisplayName() +
            (submissionDetail.isRelinquishment() ? " (Relinquishment)" : ""))
        .orElse("");
  }

  private String getActionUrl(ResourceRecordQueryResult resourceRecordQueryResult) {
    return ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(resourceRecordQueryResult.getResourceId()));
  }

  private ResourceRecordSearchResultItemModelProperties getResultItemModelProperties(
      ResourceRecordQueryResult resourceRecordQueryResult) {
    return new ResourceRecordSearchResultItemModelProperties()
        .addProperty("teamResponsible",
            submissionDetailDisplayService.getTeamResponsibleDisplay(
                resourceRecordQueryResult.getResponsibleTeam()
            ))
        .addProperty("organisationResponsible",
            submissionDetailDisplayService.getOrganisationResponsibleDisplay(
                resourceRecordQueryResult.getOrganisationResponsibleId()))
        .addProperty("licences",
            submissionDetailDisplayService.getLicencesForDisplay(resourceRecordQueryResult.getLicencesTheResourceIsOn()));
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
