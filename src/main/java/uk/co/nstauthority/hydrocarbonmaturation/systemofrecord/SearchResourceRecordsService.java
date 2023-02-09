package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;

@Service
class SearchResourceRecordsService {

  private final ResourceRecordQueryService resourceRecordQueryService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final SearchSelectorService searchSelectorService;
  private final SubmissionResourceService submissionResourceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  SearchResourceRecordsService(ResourceRecordQueryService resourceRecordQueryService,
                               SubmissionDetailDisplayService submissionDetailDisplayService,
                               SearchSelectorService searchSelectorService, SubmissionResourceService submissionResourceService,
                               OrganisationUnitQueryService organisationUnitQueryService) {
    this.resourceRecordQueryService = resourceRecordQueryService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.searchSelectorService = searchSelectorService;
    this.submissionResourceService = submissionResourceService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  List<ResourceRecordSearchResultItem> getResourceRecordSearchResults(SearchResourceRecordsForm form) {
    return resourceRecordQueryService.getResourceRecordSearchResults(form).stream()
        .map(this::buildResourceRecordSearchResultItem)
        .toList();
  }

  boolean getFiltersAdded(SearchResourceRecordsForm form) {
    var filtersAdded = new ArrayList<>();

    var generalDetailIdForResourceSubmission = form.getResourceDetailIdForResourceSubmission();
    var responsibleOrganisationId = form.getResponsibleOrganisationId();

    if (StringUtils.isNotBlank(generalDetailIdForResourceSubmission)) {
      filtersAdded.add(generalDetailIdForResourceSubmission);
    }

    if (Objects.nonNull(form.getResponsibleOrganisationId())) {
      filtersAdded.add(responsibleOrganisationId);
    }

    return !filtersAdded.isEmpty();
  }

  public Map<String, String> getPreSelectedResourceName(SearchResourceRecordsForm form) {
    var generalDetailIdForResourceSubmission = form.getResourceDetailIdForResourceSubmission();

    if (generalDetailIdForResourceSubmission == null) {
      return Map.of();
    }

    return searchSelectorService.buildPrePopulatedSelection(
        generalDetailIdForResourceSubmission,
        submissionResourceService.getResourceDisplayName(UUID.fromString(generalDetailIdForResourceSubmission))
    );
  }

  public Map<String, String> getPreSelectedOrganisationUnit(SearchResourceRecordsForm form) {
    if (form.getResponsibleOrganisationId() == null) {
      return Map.of();
    }

    return organisationUnitQueryService.getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId());
  }

  private ResourceRecordSearchResultItem buildResourceRecordSearchResultItem(
      ResourceRecordQueryResult resourceRecordQueryResult) {
    return new ResourceRecordSearchResultItem(
        getHeadingText(resourceRecordQueryResult),
        resourceRecordQueryResult.getResourceType().getDisplayName(),
        getActionUrl(resourceRecordQueryResult),
        getResultItemModelProperties(resourceRecordQueryResult)
    );
  }

  private String getCentreOfInterestBlockDisplay(ResourceRecordQueryResult resourceRecordQueryResult) {
    return resourceRecordQueryResult.getCentreOfInterestQuadBlock() != null
        ? " (" + resourceRecordQueryResult.getCentreOfInterestQuadBlock() + ")"
        : "";
  }

  private String getHeadingText(ResourceRecordQueryResult resourceRecordQueryResult) {
    return resourceRecordQueryResult.getResourceName() != null
        ? resourceRecordQueryResult.getResourceName() + getCentreOfInterestBlockDisplay(resourceRecordQueryResult)
        : SubmissionDetailUtil.getDefaultForNamelessResource(resourceRecordQueryResult.getCreatedOn().toInstant());
  }

  private String getActionUrl(ResourceRecordQueryResult resourceRecordQueryResult) {
    // TODO HM-304 - Point to view resource record controller
    return ReverseRouter.route(on(SearchResourceRecordsController.class)
        .showSearchResourceRecordsScreen());
  }

  private ResourceRecordSearchResultItemModelProperties getResultItemModelProperties(
      ResourceRecordQueryResult resourceRecordQueryResult) {
    return new ResourceRecordSearchResultItemModelProperties()
        .addProperty("organisationResponsible",
            submissionDetailDisplayService.getOrganisationResponsibleDisplay(
                resourceRecordQueryResult.getOrganisationResponsibleId()))
        .addProperty("licences",
            submissionDetailDisplayService.getLicencesForDisplay(resourceRecordQueryResult.getLicencesTheResourceIsOn()));
  }
}
