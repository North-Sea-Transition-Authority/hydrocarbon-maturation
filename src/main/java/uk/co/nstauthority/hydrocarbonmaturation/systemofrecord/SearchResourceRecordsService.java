package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;

@Service
class SearchResourceRecordsService {

  private final ResourceRecordQueryService resourceRecordQueryService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;

  @Autowired
  SearchResourceRecordsService(ResourceRecordQueryService resourceRecordQueryService,
                               SubmissionDetailDisplayService submissionDetailDisplayService) {
    this.resourceRecordQueryService = resourceRecordQueryService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
  }

  List<ResourceRecordSearchResultItem> getResourceRecordSearchResults(SearchResourceRecordsForm form) {
    return resourceRecordQueryService.getResourceRecordSearchResults(form).stream()
        .map(this::buildResourceRecordSearchResultItem)
        .toList();
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

  public boolean getFiltersAdded(SearchResourceRecordsForm form) {
    var filtersAdded = new ArrayList<String>();

    var resourceName = form.getResourceName();

    // TODO HM-132 - Enable once functioning filters are added
    return filtersAdded.isEmpty();
  }
}
